@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.xyloto.window

import org.lwjgl.glfw.GLFW.*

object Keyboard {

	const val KEY_SPACE = GLFW_KEY_SPACE
	const val KEY_APOSTROPHE = GLFW_KEY_APOSTROPHE
	const val KEY_COMMA = GLFW_KEY_COMMA
	const val KEY_MINUS = GLFW_KEY_MINUS
	const val KEY_PERIOD = GLFW_KEY_PERIOD
	const val KEY_SLASH = GLFW_KEY_SLASH
	const val KEY_0 = GLFW_KEY_0
	const val KEY_1 = GLFW_KEY_1
	const val KEY_2 = GLFW_KEY_2
	const val KEY_3 = GLFW_KEY_3
	const val KEY_4 = GLFW_KEY_4
	const val KEY_5 = GLFW_KEY_5
	const val KEY_6 = GLFW_KEY_6
	const val KEY_7 = GLFW_KEY_7
	const val KEY_8 = GLFW_KEY_8
	const val KEY_9 = GLFW_KEY_9
	const val KEY_SEMICOLON = GLFW_KEY_SEMICOLON
	const val KEY_EQUAL = GLFW_KEY_EQUAL
	const val KEY_A = GLFW_KEY_A
	const val KEY_B = GLFW_KEY_B
	const val KEY_C = GLFW_KEY_C
	const val KEY_D = GLFW_KEY_D
	const val KEY_E = GLFW_KEY_E
	const val KEY_F = GLFW_KEY_F
	const val KEY_G = GLFW_KEY_G
	const val KEY_H = GLFW_KEY_H
	const val KEY_I = GLFW_KEY_I
	const val KEY_J = GLFW_KEY_J
	const val KEY_K = GLFW_KEY_K
	const val KEY_L = GLFW_KEY_L
	const val KEY_M = GLFW_KEY_M
	const val KEY_N = GLFW_KEY_N
	const val KEY_O = GLFW_KEY_O
	const val KEY_P = GLFW_KEY_P
	const val KEY_Q = GLFW_KEY_Q
	const val KEY_R = GLFW_KEY_R
	const val KEY_S = GLFW_KEY_S
	const val KEY_T = GLFW_KEY_T
	const val KEY_U = GLFW_KEY_U
	const val KEY_V = GLFW_KEY_V
	const val KEY_W = GLFW_KEY_W
	const val KEY_X = GLFW_KEY_X
	const val KEY_Y = GLFW_KEY_Y
	const val KEY_Z = GLFW_KEY_Z
	const val KEY_LEFT_BRACKET = GLFW_KEY_LEFT_BRACKET
	const val KEY_BACKSLASH = GLFW_KEY_BACKSLASH
	const val KEY_RIGHT_BRACKET = GLFW_KEY_RIGHT_BRACKET
	const val KEY_GRAVE_ACCENT = GLFW_KEY_GRAVE_ACCENT
	const val KEY_WORLD_1 = GLFW_KEY_WORLD_1
	const val KEY_WORLD_2 = GLFW_KEY_WORLD_2

	const val KEY_ESCAPE = GLFW_KEY_ESCAPE
	const val KEY_ENTER = GLFW_KEY_ENTER
	const val KEY_TAB = GLFW_KEY_TAB
	const val KEY_BACKSPACE = GLFW_KEY_BACKSPACE
	const val KEY_INSERT = GLFW_KEY_INSERT
	const val KEY_DELETE = GLFW_KEY_DELETE
	const val KEY_RIGHT = GLFW_KEY_RIGHT
	const val KEY_LEFT = GLFW_KEY_LEFT
	const val KEY_DOWN = GLFW_KEY_DOWN
	const val KEY_UP = GLFW_KEY_UP
	const val KEY_PAGE_UP = GLFW_KEY_PAGE_UP
	const val KEY_PAGE_DOWN = GLFW_KEY_PAGE_DOWN
	const val KEY_HOME = GLFW_KEY_HOME
	const val KEY_END = GLFW_KEY_END
	const val KEY_CAPS_LOCK = GLFW_KEY_CAPS_LOCK
	const val KEY_SCROLL_LOCK = GLFW_KEY_SCROLL_LOCK
	const val KEY_NUM_LOCK = GLFW_KEY_NUM_LOCK
	const val KEY_PRINT_SCREEN = GLFW_KEY_PRINT_SCREEN
	const val KEY_PAUSE = GLFW_KEY_PAUSE
	const val KEY_F1 = GLFW_KEY_F1
	const val KEY_F2 = GLFW_KEY_F2
	const val KEY_F3 = GLFW_KEY_F3
	const val KEY_F4 = GLFW_KEY_F4
	const val KEY_F5 = GLFW_KEY_F5
	const val KEY_F6 = GLFW_KEY_F6
	const val KEY_F7 = GLFW_KEY_F7
	const val KEY_F8 = GLFW_KEY_F8
	const val KEY_F9 = GLFW_KEY_F9
	const val KEY_F10 = GLFW_KEY_F10
	const val KEY_F11 = GLFW_KEY_F11
	const val KEY_F12 = GLFW_KEY_F12
	const val KEY_F13 = GLFW_KEY_F13
	const val KEY_F14 = GLFW_KEY_F14
	const val KEY_F15 = GLFW_KEY_F15
	const val KEY_F16 = GLFW_KEY_F16
	const val KEY_F17 = GLFW_KEY_F17
	const val KEY_F18 = GLFW_KEY_F18
	const val KEY_F19 = GLFW_KEY_F19
	const val KEY_F20 = GLFW_KEY_F20
	const val KEY_F21 = GLFW_KEY_F21
	const val KEY_F22 = GLFW_KEY_F22
	const val KEY_F23 = GLFW_KEY_F23
	const val KEY_F24 = GLFW_KEY_F24
	const val KEY_F25 = GLFW_KEY_F25
	const val KEY_KP_0 = GLFW_KEY_KP_0
	const val KEY_KP_1 = GLFW_KEY_KP_1
	const val KEY_KP_2 = GLFW_KEY_KP_2
	const val KEY_KP_3 = GLFW_KEY_KP_3
	const val KEY_KP_4 = GLFW_KEY_KP_4
	const val KEY_KP_5 = GLFW_KEY_KP_5
	const val KEY_KP_6 = GLFW_KEY_KP_6
	const val KEY_KP_7 = GLFW_KEY_KP_7
	const val KEY_KP_8 = GLFW_KEY_KP_8
	const val KEY_KP_9 = GLFW_KEY_KP_9
	const val KEY_KP_DECIMAL = GLFW_KEY_KP_DECIMAL
	const val KEY_KP_DIVIDE = GLFW_KEY_KP_DIVIDE
	const val KEY_KP_MULTIPLY = GLFW_KEY_KP_MULTIPLY
	const val KEY_KP_SUBTRACT = GLFW_KEY_KP_SUBTRACT
	const val KEY_KP_ADD = GLFW_KEY_KP_ADD
	const val KEY_KP_ENTER = GLFW_KEY_KP_ENTER
	const val KEY_KP_EQUAL = GLFW_KEY_KP_EQUAL
	const val KEY_LEFT_SHIFT = GLFW_KEY_LEFT_SHIFT
	const val KEY_LEFT_CONTROL = GLFW_KEY_LEFT_CONTROL
	const val KEY_LEFT_ALT = GLFW_KEY_LEFT_ALT
	const val KEY_LEFT_SUPER = GLFW_KEY_LEFT_SUPER
	const val KEY_RIGHT_SHIFT = GLFW_KEY_RIGHT_SHIFT
	const val KEY_RIGHT_CONTROL = GLFW_KEY_RIGHT_CONTROL
	const val KEY_RIGHT_ALT = GLFW_KEY_RIGHT_ALT
	const val KEY_RIGHT_SUPER = GLFW_KEY_RIGHT_SUPER
	const val KEY_MENU = GLFW_KEY_MENU
	const val KEY_LAST = GLFW_KEY_LAST

	private var keysPressedBacking = BooleanArray(KEY_LAST + 1)
	private var keysPressed = BooleanArray(KEY_LAST + 1)

	private var keysReleasedBacking = BooleanArray(KEY_LAST + 1)
	private var keysReleased = BooleanArray(KEY_LAST + 1)

	private val keysDownBuffer = BooleanArray(KEY_LAST + 1)
	private val keysDown = BooleanArray(KEY_LAST + 1)

	private val typedStringBuilder = StringBuilder()
	var typedString: String = ""; private set

	internal fun init() {
		glfwSetKeyCallback(Window.handle) { _, key, _, action, _ ->
			when (action) {
				GLFW_PRESS -> {
					keysPressedBacking[key] = true
					keysDownBuffer[key] = true
				}
				GLFW_RELEASE -> {
					keysReleasedBacking[key] = true
					keysDownBuffer[key] = false
				}
			}
		}

		glfwSetCharCallback(Window.handle) { _, character ->
			typedStringBuilder.append(character)
		}
	}

	internal fun update() {
		updateTypedString()
		updatePressedKeys()
		updateReleasedKeys()
		updateDownKeys()
	}

	private fun updateTypedString() {
		typedString = typedStringBuilder.toString()
		typedStringBuilder.clear()
	}

	private fun updatePressedKeys() {
		val backing = keysPressed
		keysPressed = keysPressedBacking
		keysPressedBacking = backing
		keysPressedBacking.fill(false)
	}

	private fun updateReleasedKeys() {
		val backing = keysReleased
		keysReleased = keysReleasedBacking
		keysReleasedBacking = backing
		keysReleasedBacking.fill(false)
	}

	private fun updateDownKeys() {
		keysDownBuffer.copyInto(keysDown)
	}

	fun isKeyPressed(key: Int) = keysPressed[key]
	fun isKeyReleased(key: Int) = keysReleased[key]
	fun isKeyDown(key: Int) = keysDown[key]
}