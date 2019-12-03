package org.xyloto

import java.util.*

@Suppress("unused", "MemberVisibilityCanBePrivate")
class Node(vararg attributes: Attribute) {

	val attributes: List<Attribute> = Collections.unmodifiableList(attributes.toList())

	var root: Node = this
		private set(root) {
			field = root
			children.forEach { it.root = root }
		}

	var parent: Node? = null
		private set(parent) {
			field = parent

			val root = parent?.root ?: this
			this.root = root

			val attached = parent?.attached ?: false
			if (this.attached || attached) this.attached = attached
		}

	private var attachedInternal = false

	var attached: Boolean
		get() = attachedInternal
		internal set(attached) {
			fun Node.propagate() {
				attachedInternal = attached
				children.forEach(Node::propagate)
			}
			propagate()

			fun Node.notify() {
				attributes.forEach(if (attached) Attribute::notifyAttach else Attribute::notifyDetach)
				children.forEach(Node::notify)
			}
			notify()
		}

	private val mutableChildren: MutableList<Node> = LinkedList()
	val children: List<Node> = Collections.unmodifiableList(mutableChildren)

	init {
		Engine.checkInitialized()
		attributes.forEach { it.link(this) }
		attributes.forEach(Attribute::notifyReady)
	}

	fun add(node: Node) {
		Engine.lockNodeTree()

		check(node.parent == null) { "The given node already has a parent" }
		check(!node.attached) { "The given node has already been attached as a root" }

		mutableChildren.add(node)
		node.parent = this

		Engine.unlockNodeTree()
	}

	fun remove(node: Node) {
		Engine.lockNodeTree()

		check(mutableChildren.remove(node)) { "The given node isn't a child" }
		node.parent = null

		Engine.unlockNodeTree()
	}

	@JvmName("getAttributeInlined")
	inline fun <reified T : Attribute> getAttribute(): T? {
		for (attribute in attributes) {
			if (attribute is T) return attribute
		}

		return null
	}

	@JvmName("requireAttributeInlined")
	inline fun <reified T : Attribute> requireAttribute(): T {
		return getAttribute() ?: throw Exception("${T::class.qualifiedName} is missing")
	}

	@JvmName("getAttributesInlined")
	inline fun <reified T : Attribute> getAttributes(): List<T> {
		val attributes: MutableList<T> = LinkedList()

		for (attribute in this.attributes) {
			if (attribute is T) attributes.add(attribute)
		}

		return attributes
	}

	fun <T : Attribute> getAttribute(type: Class<T>): T? {
		return getAttribute(type)
	}

	fun <T : Attribute> requireAttribute(type: Class<T>): T {
		return requireAttribute(type)
	}

	fun <T : Attribute> getAttributes(type: Class<T>): List<T> {
		return getAttributes(type)
	}
}