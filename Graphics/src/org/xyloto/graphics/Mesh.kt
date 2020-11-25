package org.xyloto.graphics

import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil
import org.xyloto.math.Vec2
import org.xyloto.math.Vec3
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.experimental.ExperimentalTypeInference

class Mesh(
	var dynamic: Boolean = false
) {

	companion object {
		internal const val POSITION_INDEX = 0
		internal const val NORMAL_INDEX = 1
		internal const val TEX_COORD_INDEX = 2

		private var boundVBO = 0
		private var boundVAO = 0

		internal var activeMesh: Mesh? = null
	}

	private val vbo = glGenBuffers()

	private val vao = glGenVertexArrays()
	private val ebo = glGenBuffers()

	private var indexCount = 0
	private var drawMode = 0

	init {
		glBindBuffer(GL_ARRAY_BUFFER, vbo)
		boundVBO = vbo

		glBindVertexArray(vao)
		boundVAO = vao

		glVertexAttribPointer(POSITION_INDEX, 3, GL_FLOAT, false, 32, 0)
		glVertexAttribPointer(NORMAL_INDEX, 3, GL_FLOAT, false, 32, 12)
		glVertexAttribPointer(TEX_COORD_INDEX, 2, GL_FLOAT, false, 32, 24)
		glEnableVertexAttribArray(POSITION_INDEX)
		glEnableVertexAttribArray(NORMAL_INDEX)
		glEnableVertexAttribArray(TEX_COORD_INDEX)

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
	}

	private fun updateVBO(buffer: FloatBuffer) {
		if (boundVBO != vbo) {
			glBindBuffer(GL_ARRAY_BUFFER, vbo)
			boundVBO = vbo
		}

		val usage = if (dynamic) GL_DYNAMIC_DRAW else GL_STATIC_DRAW
		glBufferData(GL_ARRAY_BUFFER, buffer, usage)
	}

	private fun updateEBO(buffer: IntBuffer) {
		if (boundVAO != vao) {
			glBindVertexArray(vao)
			boundVAO = vao
		}

		val usage = if (dynamic) GL_DYNAMIC_DRAW else GL_STATIC_DRAW
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, usage)
	}

	fun updateVertices(vertexCount: Int, vertexProvider: (Int) -> Vertex) {
		check(activeMesh == null) { "Can't update the vertices while a mesh is being used for rendering" }

		val buffer = MemoryUtil.memAllocFloat(vertexCount * 8)
		repeat(vertexCount) { index ->
			val (position, normal, texCoord) = vertexProvider(index)
			buffer.put(position.x).put(position.y).put(position.z)
			buffer.put(normal.x).put(normal.y).put(normal.z)
			buffer.put(texCoord.x).put(texCoord.y)
		}
		buffer.flip()

		updateVBO(buffer)
		MemoryUtil.memFree(buffer)
	}

	fun updateVertices(vertices: Array<Vertex>) {
		updateVertices(vertices.size) { index -> vertices[index] }
	}

	fun updateVertices(vertices: List<Vertex>) {
		val iterator = vertices.iterator()
		updateVertices(vertices.size) { iterator.next() }
	}

	@OptIn(ExperimentalTypeInference::class)
	@OverloadResolutionByLambdaReturnType
	@JvmName("updateIndicesWithTriangles")
	fun updateIndices(triangleCount: Int, triangleProvider: (Int) -> Triangle) {
		check(activeMesh == null) { "Can't update the indices while a mesh is being used for rendering" }

		val buffer = MemoryUtil.memAllocInt(triangleCount * 3)
		repeat(triangleCount) { index ->
			val (index1, index2, index3) = triangleProvider(index)
			buffer.put(index1).put(index2).put(index3)
		}
		buffer.flip()

		updateEBO(buffer)
		MemoryUtil.memFree(buffer)

		indexCount = triangleCount * 3
		drawMode = GL_TRIANGLES
	}

	@JvmName("updateIndicesWithTriangles")
	fun updateIndices(triangles: List<Triangle>) {
		val iterator: Iterator<Triangle> = triangles.iterator()
		updateIndices(triangles.size) { iterator.next() }
	}

	@JvmName("updateIndicesWithTriangles")
	fun updateIndices(triangles: Array<Triangle>) {
		updateIndices(triangles.size) { index -> triangles[index] }
	}

	@OptIn(ExperimentalTypeInference::class)
	@OverloadResolutionByLambdaReturnType
	@JvmName("updateIndicesWithLines")
	fun updateIndices(lineCount: Int, lineProvider: (Int) -> Line) {
		check(activeMesh == null) { "Can't update the indices while a mesh is being used for rendering" }

		val buffer = MemoryUtil.memAllocInt(lineCount * 2)
		repeat(lineCount) { index ->
			val (index1, index2) = lineProvider(index)
			buffer.put(index1).put(index2)
		}
		buffer.flip()

		updateEBO(buffer)
		MemoryUtil.memFree(buffer)

		indexCount = lineCount * 2
		drawMode = GL_LINES
	}

	@JvmName("updateIndicesWithLines")
	fun updateIndices(lines: Array<Line>) {
		updateIndices(lines.size) { index -> lines[index] }
	}

	@JvmName("updateIndicesWithLines")
	fun updateIndices(lines: List<Line>) {
		val iterator = lines.iterator()
		updateIndices(lines.size) { iterator.next() }
	}

	internal fun use(block: (mesh: Mesh) -> Unit) {
		check(Shader.activeShader != null) { "No shader is being used" }
		check(activeMesh == null) { "Can't use the mesh while a mesh is being used" }
		activeMesh = this

		if (boundVAO != vao) {
			glBindVertexArray(vao)
			boundVAO = vao
		}
		block(this)

		activeMesh = null
	}

	internal fun draw() {
		check(activeMesh == this) { "The mesh can only be rendered while it's being used" }
		if (drawMode == 0) return

		glDrawElements(drawMode, indexCount, GL_UNSIGNED_INT, 0)
	}

	data class Vertex(
		val position: Vec3,
		val normal: Vec3,
		val texCoord: Vec2
	)

	data class Triangle(
		val index1: Int,
		val index2: Int,
		val index3: Int
	)

	data class Line(
		val index1: Int,
		val index2: Int
	)
}