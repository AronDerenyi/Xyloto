package org.xyloto

import java.util.*

@Suppress("unused", "MemberVisibilityCanBePrivate")
object Engine {

	private var initialized: Boolean = false
	private var started: Boolean = false
	private var running: Boolean = false

	private var nodeTreeLocked: Boolean = false

	private val mutableSystems: MutableList<System> = LinkedList()

	@JvmStatic
	val systems: List<System> = Collections.unmodifiableList(mutableSystems)

	@JvmStatic
	var root: Node? = null
		set(node) {
			lockNodeTree()

			field?.let {
				field = null
				it.attached = false
			}

			node?.let {
				check(it.parent == null) { "The given node already has a parent" }
				check(!it.attached) { "The given node has already been attached as a root" }

				field = it
				it.attached = true
			}

			unlockNodeTree()
		}

	@JvmStatic
	fun init(vararg systems: System) {
		check(!initialized) { "The engine has already been initialized" }
		mutableSystems.addAll(systems)
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