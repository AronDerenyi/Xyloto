@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.xyloto.window

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.MemoryStack

object Mouse {

	const val BUTTON_1 = GLFW_MOUSE_BUTTON_1
	const val BUTTON_2 = GLFW_MOUSE_BUTTON_2
	const val BUTTON_3 = GLFW_MOUSE_BUTTON_3
	const val BUTTON_4 = GLFW_MOUSE_BUTTON_4
	const val BUTTON_5 = GLFW_MOUSE_BUTTON_5
	const val BUTTON_6 = GLFW_MOUSE_BUTTON_6
	const val BUTTON_7 = GLFW_MOUSE_BUTTON_7
	const val BUTTON_8 = GLFW_MOUSE_BUTTON_8
	const val BUTTON_LAST = GLFW_MOUSE_BUTTON_LAST
	const val BUTTON_LEFT = GLFW_MOUSE_BUTTON_LEFT
	const val BUTTON_RIGHT = GLFW_MOUSE_BUTTON_RIGHT
	const val BUTTON_MIDDLE = GLFW_MOUSE_BUTTON_MIDDLE

	private var buttonsPressedBacking = BooleanArray(BUTTON_LAST + 1)
	private var buttonsPressed = BooleanArray(BUTTON_LAST + 1)

	private var buttonsReleasedBacking = BooleanArray(BUTTON_LAST + 1)
	private var buttonsReleased = BooleanArray(BUTTON_LAST + 1)

	private val buttonsDownBuffer = BooleanArray(BUTTON_LAST + 1)
	private val buttonsDown = BooleanArray(BUTTON_LAST + 1)

	private var grabbedChanged = false
	@JvmStatic
	var grabbed = false
		set(grabbed) {
			if (field != grabbed) {
				field = grabbed
				grabbedChanged = true
			}
		}

	private var lastPositionEvent: PositionEvent? = null
	@JvmStatic var x = 0f; private set
	@JvmStatic var y = 0f; private set
	@JvmStatic var deltaX = 0f; private set
	@JvmStatic var deltaY = 0f; private set

	private var scrollXBacking = 0f
	private var scrollYBacking = 0f
	@JvmStatic var scrollX = 0f; private set
	@JvmStatic var scrollY = 0f; private set

	internal fun init() {
		glfwSetMouseButtonCallback(Window.handle) { _, button, action, _ ->
			when (action) {
				GLFW_PRESS -> {
					buttonsPressedBacking[button] = true
					buttonsDownBuffer[button] = true
				}
				GLFW_RELEASE -> {
					buttonsReleasedBacking[button] = true
					buttonsDownBuffer[button] = false
				}
			}
		}

		MemoryStack.stackPush().use { stack ->
			val x = stack.mallocDouble(1)
			val y = stack.mallocDouble(1)
			glfwGetCursorPos(Window.handle, x, y)
			this.x = x.get().toFloat()
			this.y = y.get().toFloat()
		}

		glfwSetScrollCallback(Window.handle) { _, scrollX, scrollY ->
			scrollXBacking += scrollX.toFloat()
			scrollYBacking += scrollY.toFloat()
		}
	}

	internal fun update() {
		updatePressedButtons()
		updateReleasedButtons()
		updateDownButtons()
		updateCursor()
		updateScroll()
	}

	private fun updatePressedButtons() {
		val backing = buttonsPressed
		buttonsPressed = buttonsPressedBacking
		buttonsPressedBacking = backing
		buttonsPressedBacking.fill(false)
	}

	private fun updateReleasedButtons() {
		val backing = buttonsReleased
		buttonsReleased = buttonsReleasedBacking
		buttonsReleasedBacking = backing
		buttonsReleasedBacking.fill(false)
	}

	private fun updateDownButtons() {
		buttonsDownBuffer.copyInto(buttonsDown)
	}

	private fun updateCursor() {
		if (grabbedChanged) {
			glfwSetInputMode(Window.handle, GLFW_CURSOR, if (grabbed) GLFW_CURSOR_DISABLED else GLFW_CURSOR_NORMAL)
		}

		lastPositionEvent?.let {
			val (x, y) = it
			glfwSetCursorPos(Window.handle, x.toDouble(), y.toDouble())
			lastPositionEvent = null
		}

		MemoryStack.stackPush().use { stack ->
			val x = stack.mallocDouble(1)
			val y = stack.mallocDouble(1)
			glfwGetCursorPos(Window.handle, x, y)

			val xValue = x.get().toFloat()
			val yValue = y.get().toFloat()

			this.deltaX = xValue - this.x
			this.deltaY = yValue - this.y

			this.x = xValue
			this.y = yValue
		}

		if (grabbedChanged) {
			this.deltaX = 0f
			this.deltaY = 0f
			grabbedChanged = false
		}
	}

	private fun updateScroll() {
		scrollX = scrollXBacking
		scrollY = scrollYBacking
		scrollXBacking = 0f
		scrollYBacking = 0f
	}

	fun isButtonPressed(button: Int) = buttonsPressed[button]
	fun isButtonReleased(button: Int) = buttonsReleased[button]
	fun isButtonDown(button: Int) = buttonsDown[button]

	fun setPosition(x: Float, y: Float) {
		lastPositionEvent = PositionEvent(x, y)
	}

	private data class PositionEvent(val x: Float, val y: Float)
}