package org.xyloto

@Suppress("unused", "MemberVisibilityCanBePrivate")
open class Attribute {

	private var internalNode: Node? = null

	val linked: Boolean
		get() = internalNode != null

	val node: Node
		get() = internalNode ?: throw IllegalStateException("The attribute is not linked yet")

	internal fun link(node: Node) {
		check(!linked) { "The attribute has already been linked" }
		internalNode = node
	}

	open fun onLinked() = Unit

	open fun onAttached() = Unit
	open fun onDetached() = Unit
}