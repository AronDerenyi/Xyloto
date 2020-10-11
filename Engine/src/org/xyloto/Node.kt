package org.xyloto

import org.xyloto.collections.ArrayWrapperList
import org.xyloto.collections.HandledCollection
import org.xyloto.collections.toImmutable
import java.util.*

@Suppress("unused", "MemberVisibilityCanBePrivate")
class Node(vararg attributes: Attribute) {

	val attributes: List<Attribute> = ArrayWrapperList(attributes.clone())

	private var parentHandle: HandledCollection<Node>.Handle? = null
	var parent: Node? = null
		set(parent) {
			if (parent == parent) return
			check(parent != this) { "A node can't be it's own parent" }
			check(this != Engine.root) { "The root can't have a parent" }

			Engine.lockNodeTree()
			field?.let {
				field = null
				parentHandle?.remove()
				parentHandle = null

				if (attached) attached = false
				attributes.forEach(Attribute::notifySeparate)
			}
			parent?.let {
				field = it
				parentHandle = it.mutableChildren.Handle(this)

				attributes.forEach(Attribute::notifyParent)
				if (attached || parent.attached) attached = parent.attached
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
					children.forEach(Node::notify)
					attributes.forEach(Attribute::notifyDetach)
				}
				notify()
			}
		}

	private val mutableChildren = HandledCollection<Node>()
	val children: Collection<Node> = mutableChildren.toImmutable()

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