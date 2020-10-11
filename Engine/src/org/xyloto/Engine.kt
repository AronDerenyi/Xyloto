package org.xyloto

import org.xyloto.collections.ArrayWrapperList

@Suppress("unused", "MemberVisibilityCanBePrivate")
object Engine {

	private var initialized: Boolean = false
	private var started: Boolean = false
	private var running: Boolean = false

	private var nodeTreeLocked: Boolean = false

	private var systemsInternal: List<System>? = null

	@JvmStatic
	val systems: List<System>
		get() = systemsInternal ?: throw IllegalStateException("The engine hasn't been initialized")

	@JvmStatic
	var root: Node? = null
		set(node) {
			if (node == root) return
			node?.checkDestroyed()
			check(node?.parent == null) { "The node already has a parent" }

			lockNodeTree()
			field?.let {
				field = null
				it.attached = false
			}
			node?.let {
				field = it
				it.attached = true
			}
			unlockNodeTree()
		}

	@JvmStatic
	fun init(vararg systems: System) {
		check(!initialized) { "The engine has already been initialized" }
		systemsInternal = ArrayWrapperList(systems.clone())
		initialized = true
	}

	@JvmStatic
	fun start() {
		checkInitialized()
		check(!started) { "The engine has already been started" }
		started = true
		running = true

		systems.forEach(System::notifyStart)
		while (running) systems.forEach(System::update)
		systems.forEach(System::notifyStop)

		started = false
	}

	@JvmStatic
	fun stop() {
		check(started) { "The engine hasn't been started" }
		running = false
	}

	internal fun checkInitialized() {
		check(initialized) { "The engine hasn't been initialized" }
	}

	internal fun lockNodeTree() {
		check(!nodeTreeLocked) { "The node tree is locked" }
		nodeTreeLocked = true
	}

	internal fun unlockNodeTree() {
		check(nodeTreeLocked) { "The node tree isn't locked" }
		nodeTreeLocked = false
	}

	@JvmStatic
	@JvmName("getSystemInlined")
	inline fun <reified T : System> getSystem(): T? {
		for (system in systems) {
			if (system is T) return system
		}

		return null
	}

	@JvmStatic
	@JvmName("requireSystemInlined")
	inline fun <reified T : System> requireSystem(): T {
		return getSystem() ?: throw Exception("${T::class.qualifiedName} is missing")
	}

	@JvmStatic
	fun <T : Attribute> getSystem(type: Class<T>): T? {
		return getSystem(type)
	}

	@JvmStatic
	fun <T : Attribute> requireSystem(type: Class<T>): T {
		return requireSystem(type)
	}
}