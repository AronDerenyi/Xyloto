package org.xyloto

@Suppress("unused", "MemberVisibilityCanBePrivate")
open class Attribute {

	private val className = this::class.qualifiedName

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
		check(notifiedLink) { "$className did not call through to super.onLinked()" }
		notifiedLink = false

		notifyingLink = false
	}

	internal fun notifyAttach() {
		notifyingAttach = true

		onAttached()
		check(notifiedAttach) { "$className did not call through to super.onAttached()" }
		notifiedAttach = false

		notifyingAttach = false
	}

	internal fun notifyDetach() {
		notifyingDetach = true

		onDetached()
		check(notifiedDetach) { "$className did not call through to super.onDetached()" }
		notifiedDetach = false

		notifyingDetach = false
	}

	open fun onLinked() {
		check(notifyingLink) { "onLinked() can be only called by the engine" }
		check(!notifiedLink) { "$className called through to super.onLinked() multiple times" }
		notifiedLink = true
	}

	open fun onAttached() {
		check(notifyingAttach) { "onAttached() can be only called by the engine" }
		check(!notifiedAttach) { "$className called through to super.onAttached() multiple times" }
		notifiedAttach = true
	}

	open fun onDetached() {
		check(notifyingDetach) { "onDetached() can be only called by the engine" }
		check(!notifiedDetach) { "$className called through to super.onDetached() multiple times" }
		notifyingDetach = true
	}
}