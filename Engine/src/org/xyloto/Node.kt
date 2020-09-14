package org.xyloto

import java.util.*

@Suppress("unused", "MemberVisibilityCanBePrivate")
class Node(vararg attributes: Attribute) {

	val attributes: List<Attribute> = Collections.unmodifiableList(attributes.asList())

	var parent: Node? = null
		set(node) {
			if (node == parent) return
			check(this != Engine.root) { "The root can't have a parent" }

			Engine.lockNodeTree()
			field?.let {
				field = null
				it.mutableChildren.remove(this)

				if (attached) attached = false
				attributes.forEach(Attribute::notifySeparate)
			}
			node?.let {
				field = it
				it.mutableChildren.add(this)

				attributes.forEach(Attribute::notifyParent)
				if (attached || node.attached) attached = node.attached
			}
			Engine.unlockNodeTree()
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

			if (attached) {
				fun Node.notify() {
					attributes.forEach(Attribute::notifyAttach)
					children.forEach(Node::notify)
				}
				notify()
			} else {
				fun Node.notify() {
					attributes.forEach(Attribute::notifyDetach)
					children.forEach(Node::notify)
				}
				notify()
			}
		}

	private val mutableChildren: MutableList<Node> = LinkedList()
	val children: List<Node> = Collections.unmodifiableList(mutableChildren)

	init {
		Engine.checkInitialized()
		attributes.forEach { it.link(this) }
		attributes.forEach { it.notifyReady() }
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