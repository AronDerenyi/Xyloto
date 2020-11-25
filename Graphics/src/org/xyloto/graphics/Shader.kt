package org.xyloto.graphics

import org.lwjgl.opengl.GL32.*
import org.lwjgl.system.MemoryStack
import org.xyloto.collections.asImmutable
import org.xyloto.math.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.mutableListOf
import kotlin.collections.set
import kotlin.collections.sortBy

class Shader(
	vertexSource: String,
	fragmentSource: String
) {

	companion object {
		private var usedProgram: Int = 0
		internal var activeShader: Shader? = null
	}

	private val program = glCreateProgram()

	val properties: Property.Struct

	init {
		val vertexShader = createShader(GL_VERTEX_SHADER, vertexSource)
		val fragmentShader = createShader(GL_FRAGMENT_SHADER, fragmentSource)

		attachShader(vertexShader)
		attachShader(fragmentShader)
		addAttribute("position", Mesh.POSITION_INDEX)
		addAttribute("normal", Mesh.NORMAL_INDEX)
		addAttribute("texCoord", Mesh.TEX_COORD_INDEX)
		link()

		destroyShader(vertexShader)
		destroyShader(fragmentShader)

		val uniforms = getUniforms()
		properties = getStructProperty(LinkedList(uniforms), "")
	}

	private fun createShader(type: Int, source: String): Int {
		val shader = glCreateShader(type)
		glShaderSource(shader, source)
		glCompileShader(shader)

		if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) when (type) {
			GL_VERTEX_SHADER -> throw Exception("Error in vertex shader: ${glGetShaderInfoLog(shader)}")
			GL_FRAGMENT_SHADER -> throw Exception("Error in fragment shader: ${glGetShaderInfoLog(shader)}")
			else -> throw Exception("Error in shader: ${glGetShaderInfoLog(shader)}")
		}

		return shader
	}

	private fun attachShader(shader: Int) {
		glAttachShader(program, shader)
	}

	private fun addAttribute(name: String, index: Int) {
		glBindAttribLocation(program, index, name)
	}

	private fun link() {
		glLinkProgram(program)

		if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
			throw Exception("Error in while linking: ${glGetProgramInfoLog(program)}")
		}
	}

	private fun destroyShader(shader: Int) {
		glDetachShader(program, shader)
		glDeleteShader(shader)
	}

	private fun getUniforms(): List<Pair<String, Int>> {
		glUseProgram(program)
		usedProgram = program

		val uniformCount = glGetProgrami(program, GL_ACTIVE_UNIFORMS)
		val uniformList = mutableListOf<Pair<String, Int>>()

		MemoryStack.stackPush().use { stack ->
			val sizeBuffer = stack.mallocInt(1)
			val typeBuffer = stack.mallocInt(1)

			for (index in 0 until uniformCount) {
				val name = glGetActiveUniform(program, index, sizeBuffer, typeBuffer)
				val size = sizeBuffer[0]
				val type = typeBuffer[0]

				if (size == 1) {
					uniformList.add(Pair(name, type))
				} else {
					val arrayName = name.substring(0, name.length - 3)
					for (itemIndex in 0 until size) {
						val itemName = "$arrayName[$itemIndex]"
						uniformList.add(Pair(itemName, type))
					}
				}
			}
		}

		uniformList.sortBy { it.first }
		return uniformList
	}

	private fun getProperty(uniformQueue: Queue<Pair<String, Int>>, name: String): Property {
		val uniformName = uniformQueue.peek().first
		check(uniformName.startsWith(name)) { "Invalid name" }

		return if (name.length < uniformName.length) {
			when (uniformName[name.length]) {
				'.' -> getStructProperty(uniformQueue, name)
				'[' -> getArrayProperty(uniformQueue, name)
				else -> throw RuntimeException("Unexpected character in uniform name: $uniformName")
			}
		} else {
			getValueProperty(uniformQueue)
		}
	}

	private fun getStructProperty(uniformQueue: Queue<Pair<String, Int>>, name: String): Property.Struct {
		val map = HashMap<String, Property>()

		val itemRegex = if (name.isNotEmpty()) {
			Regex("""^${Regex.escape(name)}\.([a-zA-Z0-9_]+)""")
		} else {
			Regex("""([a-zA-Z0-9_]+)""")
		}

		while (uniformQueue.peek() != null) {
			val uniformName = uniformQueue.peek().first
			val result = itemRegex.find(uniformName) ?: break
			val itemKey = result.groupValues[1]
			val itemName = result.value
			map[itemKey] = getProperty(uniformQueue, itemName)
		}

		return Property.Struct(this, name, map)
	}

	private fun getArrayProperty(uniformQueue: Queue<Pair<String, Int>>, name: String): Property.Array {
		val list = ArrayList<Property>()

		var index = 0
		while (uniformQueue.peek() != null) {
			val uniformName = uniformQueue.peek().first
			val itemName = "$name[${index++}]"
			if (!uniformName.startsWith(itemName)) break
			list.add(getProperty(uniformQueue, itemName))
		}

		list.trimToSize()
		return Property.Array(this, name, list)
	}

	private fun getValueProperty(uniformQueue: Queue<Pair<String, Int>>): Property.Value {
		val (uniformName, uniformType) = uniformQueue.remove()
		return when (uniformType) {
			GL_FLOAT -> Property.FloatValue(this, uniformName)
			GL_FLOAT_VEC2 -> Property.Vec2Value(this, uniformName)
			GL_FLOAT_VEC3 -> Property.Vec3Value(this, uniformName)
			GL_FLOAT_VEC4 -> Property.Vec4Value(this, uniformName)
			GL_INT -> Property.IntValue(this, uniformName)
			GL_INT_VEC2 -> Property.IVec2Value(this, uniformName)
			GL_INT_VEC3 -> Property.IVec3Value(this, uniformName)
			GL_INT_VEC4 -> Property.IVec4Value(this, uniformName)
			GL_UNSIGNED_INT -> Property.UIntValue(this, uniformName)
			GL_UNSIGNED_INT_VEC2 -> Property.UIVec2Value(this, uniformName)
			GL_UNSIGNED_INT_VEC3 -> Property.UIVec3Value(this, uniformName)
			GL_UNSIGNED_INT_VEC4 -> Property.UIVec4Value(this, uniformName)
			GL_FLOAT_MAT2 -> Property.Mat2Value(this, uniformName)
			GL_FLOAT_MAT2x3 -> Property.Mat2x3Value(this, uniformName)
			GL_FLOAT_MAT2x4 -> Property.Mat2x4Value(this, uniformName)
			GL_FLOAT_MAT3x2 -> Property.Mat3x2Value(this, uniformName)
			GL_FLOAT_MAT3 -> Property.Mat3Value(this, uniformName)
			GL_FLOAT_MAT3x4 -> Property.Mat3x4Value(this, uniformName)
			GL_FLOAT_MAT4x2 -> Property.Mat4x2Value(this, uniformName)
			GL_FLOAT_MAT4x3 -> Property.Mat4x3Value(this, uniformName)
			GL_FLOAT_MAT4 -> Property.Mat4Value(this, uniformName)
			else -> throw IllegalArgumentException("Invalid uniform type: $uniformType")
		}
	}

	fun use(block: (shader: Shader) -> Unit) {
		check(activeShader == null) { "Can't use the shader while a shader is being used" }
		activeShader = this

		if (usedProgram != program) {
			glUseProgram(program)
			usedProgram = program
		}
		block(this)

		activeShader = null
	}

	sealed class Property(
		val shader: Shader,
		val name: String
	) {

		override fun toString() = name

		class Struct internal constructor(
			shader: Shader, name: String,
			private val properties: Map<String, Property>
		) : Property(shader, name) {

			val size = properties.size
			val keys = properties.keys.asImmutable()
			operator fun get(key: String) = properties[key]
		}

		class Array internal constructor(
			shader: Shader, name: String,
			private val properties: List<Property>
		) : Property(shader, name) {

			val size = properties.size
			operator fun get(index: Int) = properties[index]
		}

		abstract class Value internal constructor(shader: Shader, name: String) : Property(shader, name) {

			protected val location = glGetUniformLocation(shader.program, name)

			protected fun checkShader() = check(activeShader == shader) { "The shader is not being used" }
		}

		class FloatValue internal constructor(shader: Shader, name: String) : Value(shader, name) {

			fun set(f: Float) {
				checkShader()
				glUniform1f(location, f)
			}
		}

		class Vec2Value internal constructor(shader: Shader, name: String) : Value(shader, name) {

			fun set(v: Vec2) = set(v.x, v.y)
			fun set(f1: Float, f2: Float) {
				checkShader()
				glUniform2f(location, f1, f2)
			}
		}

		class Vec3Value internal constructor(shader: Shader, name: String) : Value(shader, name) {

			fun set(v: Vec3) = set(v.x, v.y, v.z)
			fun set(f1: Float, f2: Float, f3: Float) {
				checkShader()
				glUniform3f(location, f1, f2, f3)
			}
		}

		class Vec4Value internal constructor(shader: Shader, name: String) : Value(shader, name) {

			fun set(v: Vec4) = set(v.x, v.y, v.z, v.w)
			fun set(q: Quat) = set(q.x, q.y, q.z, q.w)
			fun set(f1: Float, f2: Float, f3: Float, f4: Float) {
				checkShader()
				glUniform4f(location, f1, f2, f3, f4)
			}
		}

		class IntValue internal constructor(shader: Shader, name: String) : Value(shader, name) {

			fun set(i: Int) {
				checkShader()
				glUniform1i(location, i)
			}
		}

		class IVec2Value internal constructor(shader: Shader, name: String) : Value(shader, name) {

			fun set(i1: Int, i2: Int) {
				checkShader()
				glUniform2i(location, i1, i2)
			}
		}

		class IVec3Value internal constructor(shader: Shader, name: String) : Value(shader, name) {

			fun set(i1: Int, i2: Int, i3: Int) {
				checkShader()
				glUniform3i(location, i1, i2, i3)
			}
		}

		class IVec4Value internal constructor(shader: Shader, name: String) : Value(shader, name) {

			fun set(i1: Int, i2: Int, i3: Int, i4: Int) {
				checkShader()
				glUniform4i(location, i1, i2, i3, i4)
			}
		}

		class UIntValue internal constructor(shader: Shader, name: String) : Value(shader, name) {

			fun set(i: Int) {
				checkShader()
				glUniform1ui(location, i)
			}
		}

		class UIVec2Value internal constructor(shader: Shader, name: String) : Value(shader, name) {

			fun set(i1: Int, i2: Int) {
				checkShader()
				glUniform2ui(location, i1, i2)
			}
		}

		class UIVec3Value internal constructor(shader: Shader, name: String) : Value(shader, name) {

			fun set(i1: Int, i2: Int, i3: Int) {
				checkShader()
				glUniform3ui(location, i1, i2, i3)
			}
		}

		class UIVec4Value internal constructor(shader: Shader, name: String) : Value(shader, name) {

			fun set(i1: Int, i2: Int, i3: Int, i4: Int) {
				checkShader()
				glUniform4ui(location, i1, i2, i3, i4)
			}
		}

		class Mat2Value internal constructor(shader: Shader, name: String) : Value(shader, name) {

			fun set(m: Mat2) {
				checkShader()
				glUniformMatrix2fv(
					location, false, floatArrayOf(
						m.m00, m.m01,
						m.m10, m.m11
					)
				)
			}

			fun set(x: Vec2, y: Vec2) {
				checkShader()
				glUniformMatrix2fv(
					location, false, floatArrayOf(
						x.x, x.y,
						y.x, y.y
					)
				)
			}
		}

		class Mat2x3Value internal constructor(shader: Shader, name: String) : Value(shader, name) {

			fun set(x: Vec3, y: Vec3) {
				checkShader()
				glUniformMatrix2fv(
					location, false, floatArrayOf(
						x.x, x.y, x.z,
						y.x, y.y, y.z
					)
				)
			}
		}

		class Mat2x4Value internal constructor(shader: Shader, name: String) : Value(shader, name) {

			fun set(x: Vec4, y: Vec4) {
				checkShader()
				glUniformMatrix2fv(
					location, false, floatArrayOf(
						x.x, x.y, x.z, x.w,
						y.x, y.y, y.z, y.w
					)
				)
			}
		}

		class Mat3x2Value internal constructor(shader: Shader, name: String) : Value(shader, name) {

			fun set(x: Vec2, y: Vec2, z: Vec2) {
				checkShader()
				glUniformMatrix3x2fv(
					location, false, floatArrayOf(
						x.x, x.y,
						y.x, y.y,
						z.x, z.y
					)
				)
			}
		}

		class Mat3Value internal constructor(shader: Shader, name: String) : Value(shader, name) {

			fun set(m: Mat3) {
				checkShader()
				glUniformMatrix3fv(
					location, false, floatArrayOf(
						m.m00, m.m01, m.m02,
						m.m10, m.m11, m.m12,
						m.m20, m.m21, m.m22
					)
				)
			}

			fun set(x: Vec3, y: Vec3, z: Vec3) {
				checkShader()
				glUniformMatrix3fv(
					location, false, floatArrayOf(
						x.x, x.y, x.z,
						y.x, y.y, y.z,
						z.x, z.y, z.z
					)
				)
			}
		}

		class Mat3x4Value internal constructor(shader: Shader, name: String) : Value(shader, name) {

			fun set(x: Vec4, y: Vec4, z: Vec4) {
				checkShader()
				glUniformMatrix3x4fv(
					location, false, floatArrayOf(
						x.x, x.y, x.z, x.w,
						y.x, y.y, y.z, y.w,
						z.x, z.y, z.z, z.w
					)
				)
			}
		}

		class Mat4x2Value internal constructor(shader: Shader, name: String) : Value(shader, name) {

			fun set(x: Vec2, y: Vec2, z: Vec2, w: Vec2) {
				checkShader()
				glUniformMatrix4x2fv(
					location, false, floatArrayOf(
						x.x, x.y,
						y.x, y.y,
						z.x, z.y,
						w.x, w.y
					)
				)
			}
		}

		class Mat4x3Value internal constructor(shader: Shader, name: String) : Value(shader, name) {

			fun set(x: Vec3, y: Vec3, z: Vec3, w: Vec3) {
				checkShader()
				glUniformMatrix4x3fv(
					location, false, floatArrayOf(
						x.x, x.y, x.z,
						y.x, y.y, y.z,
						z.x, z.y, z.z,
						w.x, w.y, w.z
					)
				)
			}
		}

		class Mat4Value internal constructor(shader: Shader, name: String) : Value(shader, name) {

			fun set(m: Mat4) {
				checkShader()
				glUniformMatrix4fv(
					location, false, floatArrayOf(
						m.m00, m.m01, m.m02, m.m03,
						m.m10, m.m11, m.m12, m.m13,
						m.m20, m.m21, m.m22, m.m23,
						m.m30, m.m31, m.m32, m.m33
					)
				)
			}

			fun set(x: Vec4, y: Vec4, z: Vec4, w: Vec4) {
				checkShader()
				glUniformMatrix4fv(
					location, false, floatArrayOf(
						x.x, x.y, x.z, x.w,
						y.x, y.y, y.z, y.w,
						z.x, z.y, z.z, z.w,
						w.x, w.y, w.z, w.w
					)
				)
			}
		}
	}
}