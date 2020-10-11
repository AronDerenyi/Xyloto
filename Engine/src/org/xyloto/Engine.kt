package org.xyloto

import org.xyloto.collections.ArrayWrapperList

@Suppress("unused", "MemberVisibilityCanBePrivate")
object Engine {

	private var initialized: Boolean = false
	private var started: Boolean = false
	private var running: Boolean = false

	internal const val LOCK_NOTHING: Byte = 0
	internal const val LOCK_ROOT: Byte = 1
	internal const val LOCK_PARENT: Byte = 2
	internal const val LOCK_DESTROY: Byte = 3
	private var nodeTreeLock: Byte = LOCK_NOTHING

	private var systemsInternal: List<System>? = null

	@JvmStatic
	val systems: List<System>
		get() = systemsInternal ?: throw IllegalStateException("The engine hasn't been initialized")

	@JvmStatic
	var root: Node? = null
		set(root) {
			if (root == field) return
			root?.checkDestroyed()
			check(root?.parent == null) { "The node already has a parent" }

			lockNodeTree(LOCK_ROOT)
			field?.let {
				field = null
				it.attached = false
			}
			root?.let {
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

	internal fun lockNodeTree(lock: Byte) {
		check(nodeTreeLock == LOCK_NOTHING) {
			when (nodeTreeLock) {
				LOCK_ROOT -> "Can't modify the node tree while the root is being set"
				LOCK_PARENT -> "Can't modify the node tree while a parent is being set"
				LOCK_DESTROY -> "Can't modify the node tree while a node is being destroyed"
				else -> "Can't modify the node tree"
			}
		}
		nodeTreeLock = lock
	}

	internal fun unlockNodeTree() {
		check(nodeTreeLock != LOCK_NOTHING) { "The node tree isn't locked" }
		nodeTreeLock = LOCK_NOTHING
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