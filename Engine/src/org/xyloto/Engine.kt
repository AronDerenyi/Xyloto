package org.xyloto

import java.util.*

@Suppress("unused", "MemberVisibilityCanBePrivate")
object Engine {

	private var initialized: Boolean = false
	private var started: Boolean = false
	private var running: Boolean = false

	private var nodeTreeLocked: Boolean = false

	private val mutableSystems: MutableList<System> = LinkedList()
	val systems: List<System> = Collections.unmodifiableList(mutableSystems)

	private val mutableRoots: MutableList<Node> = LinkedList()
	val roots: List<Node> = Collections.unmodifiableList(mutableRoots)

	fun init(vararg systems: System) {
		check(!initialized) { "The engine has already been initialized" }
		mutableSystems.addAll(systems)
		initialized = true
	}

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

	fun add(node: Node) {
		lockNodeTree()

		check(node.parent == null) { "The given node already has a parent" }
		check(!node.attached) { "The given node has already been attached as a root" }

		mutableRoots.add(node)
		node.attached = true

		unlockNodeTree()
	}

	fun remove(node: Node) {
		lockNodeTree()

		check(mutableRoots.remove(node)) { "The given node isn't a child" }
		node.attached = false

		unlockNodeTree()
	}

	@JvmName("getSystemInlined")
	inline fun <reified T : System> getSystem(): T? {
		for (system in systems) {
			if (system is T) return system
		}

		return null
	}

	@JvmName("requireSystemInlined")
	inline fun <reified T : System> requireSystem(): T {
		return getSystem() ?: throw Exception("${T::class.qualifiedName} is missing")
	}

	fun <T : Attribute> getSystem(type: Class<T>): T? {
		return getSystem(type)
	}

	fun <T : Attribute> requireSystem(type: Class<T>): T {
		return requireSystem(type)
	}
}