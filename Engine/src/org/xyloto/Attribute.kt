package org.xyloto

@Suppress("unused", "MemberVisibilityCanBePrivate")
open class Attribute {

	private var notifyingLink: Boolean = false
	private var notifiedLink: Boolean = false

	private var notifyingAttach: Boolean = false
	private var notifiedAttach: Boolean = false

	private var notifyingDetach: Boolean = false
	private var notifiedDetach: Boolean = false

	private var nodeInternal: Node? = null

	val node: Node
		get() = nodeInternal ?: throw IllegalStateException("The attribute is not linked yet")

	val linked: Boolean
		get() = nodeInternal != null

	internal fun link(node: Node) {
		check(!linked) { "The attribute has already been linked" }
		nodeInternal = node
	}

	internal fun notifyLink() {
		notifyingLink = true

		onLinked()
		check(notifiedLink) { "super.onLinked() was not called" }
		notifiedLink = false

		notifyingLink = false
	}

	internal fun notifyAttach() {
		notifyingAttach = true

		onLinked()
		check(notifiedAttach) { "super.onAttached() was not called" }
		notifiedAttach = false

		notifyingAttach = false
	}

	internal fun notifyDetach() {
		notifyingDetach = true

		onLinked()
		check(notifiedDetach) { "super.onLDetached() was not called" }
		notifiedDetach = false

		notifyingDetach = false
	}

	open fun onLinked() {
		check(notifyingLink) { "onLinked() can be only called by the engine" }
		check(!notifiedLink) { "super.onLinked() can only be called once" }
		notifiedLink = true
	}

	open fun onAttached() {
		check(notifyingAttach) { "onAttached() can be only called by the engine" }
		check(!notifiedAttach) { "super.onAttached() can only be called once" }
		notifiedAttach = true
	}

	open fun onDetached() {
		check(notifyingDetach) { "onDetached() can be only called by the engine" }
		check(!notifiedDetach) { "super.onDetached() can only be called once" }
		notifyingDetach = true
	}
}