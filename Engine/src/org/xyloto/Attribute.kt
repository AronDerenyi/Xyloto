package org.xyloto

@Suppress("unused", "MemberVisibilityCanBePrivate")
open class Attribute {

	private var notifyingReady: Boolean = false
	private var notifiedReady: Boolean = false

	private var notifyingAttach: Boolean = false
	private var notifiedAttach: Boolean = false

	private var notifyingDetach: Boolean = false
	private var notifiedDetach: Boolean = false

	private var nodeInternal: Node? = null

	val node: Node
		get() = nodeInternal ?: throw IllegalStateException("The attribute is not linked yet")

	val linked: Boolean
		get() = nodeInternal != null

	init {
		Engine.checkInitialized()
	}

	internal fun link(node: Node) {
		check(!linked) { "The attribute has already been linked" }
		nodeInternal = node
	}

	internal fun notifyReady() {
		notifyingReady = true

		onReady()
		check(notifiedReady) { "${this::class.qualifiedName} did not call through to super.onReady()" }
		notifiedReady = false

		notifyingReady = false
	}

	internal fun notifyAttach() {
		notifyingAttach = true

		onAttach()
		check(notifiedAttach) { "${this::class.qualifiedName} did not call through to super.onAttach()" }
		notifiedAttach = false

		notifyingAttach = false
	}

	internal fun notifyDetach() {
		notifyingDetach = true

		onDetach()
		check(notifiedDetach) { "${this::class.qualifiedName} did not call through to super.onDetach()" }
		notifiedDetach = false

		notifyingDetach = false
	}

	protected open fun onReady() {
		check(notifyingReady) { "onReady() can be only called by the engine" }
		check(!notifiedReady) { "${this::class.qualifiedName} called through to super.onReady() multiple times" }
		notifiedReady = true
	}

	protected open fun onAttach() {
		check(notifyingAttach) { "onAttach() can be only called by the engine" }
		check(!notifiedAttach) { "${this::class.qualifiedName} called through to super.onAttach() multiple times" }
		notifiedAttach = true
	}

	protected open fun onDetach() {
		check(notifyingDetach) { "onDetach() can be only called by the engine" }
		check(!notifiedDetach) { "${this::class.qualifiedName} called through to super.onDetach() multiple times" }
		notifyingDetach = true
	}
}