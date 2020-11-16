@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.xyloto.window

import org.lwjgl.PointerBuffer
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.util.nfd.NativeFileDialog
import org.lwjgl.util.nfd.NativeFileDialog.*
import org.xyloto.Engine
import org.xyloto.System

// TODO: Size and aspect ratio limits
object Window : System() {

	private var initialized = false

	internal var handle: Long = NULL

	private var titleChanged = false
	var title = "Xyloto"
		set(title) {
			if (field != title) {
				field = title
				titleChanged = true
			}
		}

	private var resizableChanged = false
	var resizable = true
		set(resizable) {
			if (field != resizable) {
				field = resizable
				resizableChanged = true
			}
		}

	private var vSyncChanged = false
	var vSync = true
		set(vSync) {
			if (field != vSync) {
				field = vSync
				vSyncChanged = true
			}
		}

	private var lastFullscreenEvent: FullscreenEvent? = null
	var fullscreenMode: Monitor.Mode? = null; private set
	var fullscreenChanged = true; private set

	private var lastSizeEvent: SizeEvent? = null
	var width = 0; private set
	var height = 0; private set
	var sizeChanged = true; private set

	private var lastPositionEvent: PositionEvent? = null
	var positionX = 0; private set
	var positionY = 0; private set
	var positionChanged = true; private set

	var contentScaleX = 0f; private set
	var contentScaleY = 0f; private set
	var contentScaleChanged = true; private set

	var frameLeft = 0; private set
	var frameTop = 0; private set
	var frameRight = 0; private set
	var frameBottom = 0; private set
	var frameChanged = true; private set

	fun init() {
		if (initialized) throw IllegalStateException("The window has already been initialized")
		initialized = true

		GLFWErrorCallback.createPrint().set()

		glfwInit()
		glfwDefaultWindowHints()
		glfwWindowHint(GLFW_DOUBLEBUFFER, GLFW_TRUE)
		glfwWindowHint(GLFW_DEPTH_BITS, 24)
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2)
		glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE)

		handle = glfwCreateWindow(960, 540, title, NULL, NULL)
		glfwMakeContextCurrent(handle)
		glfwSetWindowAttrib(handle, GLFW_RESIZABLE, if (resizable) GLFW_TRUE else GLFW_FALSE)
		glfwSwapInterval(if (vSync) 1 else 0)

		initSize()
		initPosition()
		initContentScale()
		initFrame()

		Monitor.init()
		Keyboard.init()
		Mouse.init()
	}

	private fun initSize() {
		MemoryStack.stackPush().use { stack ->
			val width = stack.mallocInt(1)
			val height = stack.mallocInt(1)
			glfwGetWindowSize(handle, width, height)
			this.width = width.get()
			this.height = height.get()
		}
	}

	private fun initPosition() {
		MemoryStack.stackPush().use { stack ->
			val positionX = stack.mallocInt(1)
			val positionY = stack.mallocInt(1)
			glfwGetWindowPos(handle, positionX, positionY)
			this.positionX = positionX.get()
			this.positionY = positionY.get()
		}
	}

	private fun initContentScale() {
		MemoryStack.stackPush().use { stack ->
			val contentScaleX = stack.mallocFloat(1)
			val contentScaleY = stack.mallocFloat(1)
			glfwGetWindowContentScale(handle, contentScaleX, contentScaleY)
			this.contentScaleX = contentScaleX.get()
			this.contentScaleY = contentScaleY.get()
		}
	}

	private fun initFrame() {
		MemoryStack.stackPush().use { stack ->
			val frameLeft = stack.mallocInt(1)
			val frameTop = stack.mallocInt(1)
			val frameRight = stack.mallocInt(1)
			val frameBottom = stack.mallocInt(1)
			glfwGetWindowFrameSize(handle, frameLeft, frameTop, frameRight, frameBottom)
			this.frameLeft = frameLeft.get()
			this.frameTop = frameTop.get()
			this.frameRight = frameRight.get()
			this.frameBottom = frameBottom.get()
		}
	}

	override fun onStart() {
		super.onStart()
		if (!initialized) throw IllegalStateException("The window has not been initialized")
	}

	override fun onUpdate() {
		glfwSwapBuffers(handle)
		glfwPollEvents()

		updateTitle()
		updateResized()
		updateVSync()
		updateFullscreen()
		updateSize()
		updatePosition()
		updateContentScale()
		updateFrame()

		Monitor.update()
		Keyboard.update()
		Mouse.update()

		if (glfwWindowShouldClose(handle)) Engine.stop()
	}

	private fun updateTitle() {
		if (titleChanged) {
			glfwSetWindowTitle(handle, title)
			titleChanged = false
		}
	}

	private fun updateResized() {
		if (resizableChanged) {
			glfwSetWindowAttrib(handle, GLFW_RESIZABLE, if (resizable) GLFW_TRUE else GLFW_FALSE)
			resizableChanged = false
		}
	}

	private fun updateVSync() {
		if (vSyncChanged) {
			glfwSwapInterval(if (vSync) 1 else 0)
			vSyncChanged = false
		}
	}

	private fun updateFullscreen() {
		lastFullscreenEvent?.let {
			val (mode) = it
			if (fullscreenMode != mode) {
				fullscreenMode = mode
				fullscreenChanged = true

				if (mode == null) {
					glfwSetWindowMonitor(handle, NULL, 0, 0, 0, 0, 0)
				} else {
					glfwSetWindowMonitor(handle, mode.monitor.handle, 0, 0, mode.width, mode.height, mode.refreshRate)
				}
			}

			lastFullscreenEvent = null
		}
	}

	private fun updateSize() {
		lastSizeEvent?.let {
			val (width, height) = it
			if (fullscreenMode == null) {
				glfwSetWindowSize(handle, width, height)
				lastSizeEvent = null
			}
		}

		MemoryStack.stackPush().use { stack ->
			val width = stack.mallocInt(1)
			val height = stack.mallocInt(1)
			glfwGetWindowSize(handle, width, height)

			this.sizeChanged = width.get(0) != this.width ||
					height.get(0) != this.height

			this.width = width.get(0)
			this.height = height.get(0)
		}
	}

	private fun updatePosition() {
		lastPositionEvent?.let {
			val (positionX, positionY) = it
			if (fullscreenMode == null) {
				glfwSetWindowPos(handle, positionX, positionY)
				lastPositionEvent = null
			}
		}

		MemoryStack.stackPush().use { stack ->
			val positionX = stack.mallocInt(1)
			val positionY = stack.mallocInt(1)
			glfwGetWindowPos(handle, positionX, positionY)

			this.positionChanged = positionX.get(0) != this.positionX ||
					positionY.get(0) != this.positionY

			this.positionX = positionX.get(0)
			this.positionY = positionY.get(0)
		}
	}

	private fun updateContentScale() {
		MemoryStack.stackPush().use { stack ->
			val contentScaleX = stack.mallocFloat(1)
			val contentScaleY = stack.mallocFloat(1)
			glfwGetWindowContentScale(handle, contentScaleX, contentScaleY)

			this.contentScaleChanged = contentScaleX.get(0) != this.contentScaleX ||
					contentScaleY.get(0) != this.contentScaleY

			this.contentScaleX = contentScaleX.get(0)
			this.contentScaleY = contentScaleY.get(0)
		}
	}

	private fun updateFrame() {
		MemoryStack.stackPush().use { stack ->
			val frameLeft = stack.mallocInt(1)
			val frameTop = stack.mallocInt(1)
			val frameRight = stack.mallocInt(1)
			val frameBottom = stack.mallocInt(1)
			glfwGetWindowFrameSize(handle, frameLeft, frameTop, frameRight, frameBottom)

			this.frameChanged = frameLeft.get(0) != this.frameLeft ||
					frameTop.get(0) != this.frameTop ||
					frameRight.get(0) != this.frameRight ||
					frameBottom.get(0) != this.frameBottom

			this.frameLeft = frameLeft.get(0)
			this.frameTop = frameTop.get(0)
			this.frameRight = frameRight.get(0)
			this.frameBottom = frameBottom.get(0)
		}
	}

	fun enterFullscreen(mode: Monitor.Mode) {
		lastFullscreenEvent = FullscreenEvent(mode)
	}

	fun exitFullscreen() {
		lastFullscreenEvent = FullscreenEvent(null)
	}

	fun setSize(width: Int, height: Int) {
		lastSizeEvent = SizeEvent(width, height)
	}

	fun setPosition(positionX: Int, positionY: Int) {
		lastPositionEvent = PositionEvent(positionX, positionY)
	}

	fun showOpenFileDialog(
		filter: List<String>? = null,
		default: String? = null
	) = showFileDialog(NativeFileDialog::NFD_OpenDialog, filter, default)

	fun showSaveFileDialog(
		filter: List<String>? = null,
		default: String? = null
	) = showFileDialog(NativeFileDialog::NFD_SaveDialog, filter, default)

	private inline fun showFileDialog(
		dialogFunction: (CharSequence?, CharSequence?, PointerBuffer) -> Int,
		filter: List<String>?,
		default: String?
	): String? {
		MemoryStack.stackPush().use { stack ->
			val pathPointer = stack.pointers(1)
			val result = dialogFunction(filter?.joinToString(";"), default, pathPointer)

			if (result == NFD_OKAY) {
				val path = pathPointer.getStringUTF8(0)
				nNFD_Free(pathPointer[0])
				return path
			}

			if (result == NFD_ERROR) {
				java.lang.System.err.println("NFD Error: ${NFD_GetError()}")
			}

			return null
		}
	}

	private data class SizeEvent(val width: Int, val height: Int)
	private data class FullscreenEvent(val mode: Monitor.Mode?)
	private data class PositionEvent(val positionX: Int, val positionY: Int)
}
