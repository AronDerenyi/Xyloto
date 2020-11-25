@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.xyloto.window

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil.NULL
import org.xyloto.collections.toImmutable
import java.util.*

class Monitor(internal val handle: Long) {

	companion object {

		private val mutableMonitors: MutableList<Monitor> = mutableListOf()
		val monitors: List<Monitor> = mutableMonitors.toImmutable()
		var monitorsChanged: Boolean = false; private set
		var primaryMonitor: Monitor? = null; private set

		private val eventQueue: Queue<Event> = LinkedList()

		internal fun init() {
			glfwGetMonitors()?.let {
				for (index in 0 until it.limit()) {
					val monitor = Monitor(it[index])
					mutableMonitors.add(monitor)
					if (index == 0) primaryMonitor = monitor
				}
			}

			glfwSetMonitorCallback { handle, event ->
				eventQueue.add(Event(handle, event == GLFW_CONNECTED))
			}
		}

		internal fun update() {
			val primaryMonitorHandle = glfwGetPrimaryMonitor()
			monitorsChanged = eventQueue.isNotEmpty() || primaryMonitorHandle != primaryMonitor?.handle ?: NULL
			while (eventQueue.peek() != null) {
				val (handle, connected) = eventQueue.remove()
				if (connected) {
					if (monitors.find { it.handle == handle } == null) {
						mutableMonitors.add(Monitor(handle))
					}
				} else {
					mutableMonitors.removeIf { it.handle == handle }
				}
			}
			primaryMonitor = monitors.find { it.handle == primaryMonitorHandle }
		}

		private data class Event(val handle: Long, val connected: Boolean)
	}

	val name: String = glfwGetMonitorName(handle) ?: ""

	val positionX: Int
	val positionY: Int

	val contentScaleX: Float
	val contentScaleY: Float

	val physicalSizeWidth: Int
	val physicalSizeHeight: Int

	val workareaX: Int
	val workareaY: Int
	val workareaWidth: Int
	val workareaHeight: Int

	val modes: List<Mode>

	init {
		MemoryStack.stackPush().use { stack ->
			val positionX = stack.mallocInt(1)
			val positionY = stack.mallocInt(1)
			glfwGetMonitorPos(handle, positionX, positionY)
			this.positionX = positionX.get()
			this.positionY = positionY.get()
		}

		MemoryStack.stackPush().use { stack ->
			val contentScaleX = stack.mallocFloat(1)
			val contentScaleY = stack.mallocFloat(1)
			glfwGetMonitorContentScale(handle, contentScaleX, contentScaleY)
			this.contentScaleX = contentScaleX.get()
			this.contentScaleY = contentScaleY.get()
		}

		MemoryStack.stackPush().use { stack ->
			val physicalWidth = stack.mallocInt(1)
			val physicalHeight = stack.mallocInt(1)
			glfwGetMonitorPhysicalSize(handle, physicalWidth, physicalHeight)
			physicalSizeWidth = physicalWidth.get()
			physicalSizeHeight = physicalHeight.get()
		}

		MemoryStack.stackPush().use { stack ->
			val workareaX = stack.mallocInt(1)
			val workareaY = stack.mallocInt(1)
			val workareaWidth = stack.mallocInt(1)
			val workareaHeight = stack.mallocInt(1)
			glfwGetMonitorWorkarea(handle, workareaX, workareaY, workareaWidth, workareaHeight)
			this.workareaX = workareaX.get()
			this.workareaY = workareaY.get()
			this.workareaWidth = workareaWidth.get()
			this.workareaHeight = workareaHeight.get()
		}

		val videoModes = glfwGetVideoModes(handle)
		modes = Array(videoModes?.limit() ?: 0) {
			val videoMode = videoModes!![it]
			Mode(
				this,
				videoMode.width(),
				videoMode.height(),
				videoMode.redBits()
			)
		}.asList()
	}

	data class Mode(
		val monitor: Monitor,
		val width: Int,
		val height: Int,
		val refreshRate: Int
	)
}