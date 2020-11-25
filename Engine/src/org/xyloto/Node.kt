package org.xyloto

import org.xyloto.collections.HandledCollection
import org.xyloto.collections.toImmutable
import java.util.*
import kotlin.NoSuchElementException

@Suppress("unused", "MemberVisibilityCanBePrivate")
class Node(vararg attributes: Attribute) {

	val attributes: List<Attribute> = attributes.clone().asList()

	private var parentHandle: HandledCollection<Node>.Handle? = null
	var parent: Node? = null
		set(parent) {
			checkInitialized()
			checkDestroyed()
			NodeTreeLock.check(NodeTreeLock.LOCK_PARENT)

			if (parent == field) return
			parent?.checkInitialized()
			parent?.checkDestroyed()
			check(parent != this) { "A node can't be it's own parent" }
			check(this != Engine.root) { "The root can't have a parent" }

			NodeTreeLock.use(NodeTreeLock.LOCK_PARENT) {
				val oldParent = field
				field = parent

				if (oldParent != null) {
					parentHandle?.remove()
					parentHandle = null

					if (attached) attached = false
					attributes.forEach { it.notifySeparate() }
				}

				if (parent != null) {
					parentHandle = parent.mutableChildren.Handle(this)

					attributes.forEach { it.notifyParent() }
					if (attached || parent.attached) attached = parent.attached
				}
			}
		}

	private var attachedInternal = false
	var attached: Boolean
		get() = attachedInternal
		internal set(attached) {
			fun Node.propagate() {
				attachedInternal = attached
				children.forEach { it.propagate() }
			}
			propagate()

			if (attached) {
				fun Node.notify() {
					attributes.forEach { it.notifyAttach() }
					children.forEach { it.notify() }
				}
				notify()
			} else {
				fun Node.notify() {
					children.forEach { it.notify() }
					attributes.forEach { it.notifyDetach() }
				}
				notify()
			}
		}

	private var initialized = false

	var destroyed = false
		private set

	private val mutableChildren = HandledCollection<Node>()
	val children: Collection<Node> = mutableChildren.toImmutable()

	init {
		Engine.checkInitialized()
		attributes.forEach { it.link(this) }
		attributes.forEach { it.notifyInit() }
		initialized = true

		attributes.forEach { it.notifyReady() }
	}

	internal fun checkInitialized() {
		check(initialized) { "The node hasn't been initialized" }
	}

	fun destroy() {
		checkDestroyed()
		NodeTreeLock.check(NodeTreeLock.LOCK_DESTROY)

		children.forEach { it.destroy() }

		if (this == Engine.root) {
			Engine.root = null
		} else {
			parent = null
		}

		NodeTreeLock.use(NodeTreeLock.LOCK_DESTROY) {
			attributes.forEach { it.notifyDestroy() }
			destroyed = true
		}
	}

	internal fun checkDestroyed() {
		check(!destroyed) { "The node has already been destroyed" }
	}

	@JvmName("getAttributeInlined")
	inline fun <reified T> getAttribute(): T? {
		for (attribute in attributes) {
			if (attribute is T) return attribute
		}

		return null
	}

	@JvmName("requireAttributeInlined")
	inline fun <reified T> requireAttribute(): T {
		return getAttribute() ?: throw NoSuchElementException("${T::class.qualifiedName} is missing")
	}

	@JvmName("getAttributesInlined")
	inline fun <reified T> getAttributes(): List<T> {
		val attributes: MutableList<T> = LinkedList()

		for (attribute in this.attributes) {
			if (attribute is T) attributes.add(attribute)
		}

		return attributes
	}

	fun <T> getAttribute(type: Class<T>): T? {
		for (attribute in attributes) {
			@Suppress("UNCHECKED_CAST")
			if (type.isInstance(attribute)) return attribute as T
		}

		return null
	}

	fun <T> requireAttribute(type: Class<T>): T {
		return getAttribute(type) ?: throw NoSuchElementException("${type.name} is missing")
	}

	fun <T> getAttributes(type: Class<T>): List<T> {
		val attributes: MutableList<T> = LinkedList()

		for (attribute in this.attributes) {
			@Suppress("UNCHECKED_CAST")
			if (type.isInstance(attribute)) attributes.add(attribute as T)
		}

		return attributes
	}
}