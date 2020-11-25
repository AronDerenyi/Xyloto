package org.xyloto

import org.xyloto.collections.ArrayWrapperList

@Suppress("unused", "MemberVisibilityCanBePrivate")
object Engine {

	private var initialized: Boolean = false
	private var started: Boolean = false
	private var running: Boolean = false

	private var systemsInternal: List<System>? = null

	@JvmStatic
	val systems: List<System>
		get() = systemsInternal ?: throw IllegalStateException("The engine hasn't been initialized")

	@JvmStatic
	var root: Node? = null
		set(root) {
			NodeTreeLock.check(NodeTreeLock.LOCK_ROOT)

			if (root == field) return
			root?.checkInitialized()
			root?.checkDestroyed()
			check(root?.parent == null) { "The node already has a parent" }

			NodeTreeLock.use(NodeTreeLock.LOCK_ROOT) {
				val oldRoot = field
				field = root

				if (oldRoot != null) oldRoot.attached = false
				if (root != null) root.attached = true
			}
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

	@JvmStatic
	@JvmName("getSystemInlined")
	inline fun <reified T> getSystem(): T? {
		for (system in systems) {
			if (system is T) return system
		}

		return null
	}

	@JvmStatic
	@JvmName("requireSystemInlined")
	inline fun <reified T> requireSystem(): T {
		return getSystem() ?: throw NoSuchElementException("${T::class.qualifiedName} is missing")
	}

	@JvmStatic
	fun <T> getSystem(type: Class<T>): T? {
		for (system in systems) {
			@Suppress("UNCHECKED_CAST")
			if (type.isInstance(system)) return system as T
		}

		return null
	}

	@JvmStatic
	fun <T> requireSystem(type: Class<T>): T {
		return getSystem(type) ?: throw NoSuchElementException("${type.name} is missing")
	}
}