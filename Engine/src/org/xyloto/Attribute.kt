package org.xyloto

@Suppress("unused", "MemberVisibilityCanBePrivate")
open class Attribute {

	companion object {
		private const val NOTIFYING_NOTHING: Byte = 0
		private const val NOTIFYING_READY: Byte = 1
		private const val NOTIFYING_PARENT: Byte = 2
		private const val NOTIFYING_SEPARATE: Byte = 3
		private const val NOTIFYING_ATTACH: Byte = 4
		private const val NOTIFYING_DETACH: Byte = 5
	}

	private var notifying: Byte = NOTIFYING_NOTHING
	private var notified: Boolean = false

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
		notifying = NOTIFYING_READY

		onReady()
		check(notified) { "${this::class.qualifiedName} did not call through to super.onReady()" }
		notified = false

		notifying = NOTIFYING_NOTHING
	}

	internal fun notifyParent() {
		notifying = NOTIFYING_PARENT

		onParent()
		check(notified) { "${this::class.qualifiedName} did not call through to super.onParent()" }
		notified = false

		notifying = NOTIFYING_NOTHING
	}

	internal fun notifySeparate() {
		notifying = NOTIFYING_SEPARATE

		onSeparate()
		check(notified) { "${this::class.qualifiedName} did not call through to super.onSeparate()" }
		notified = false

		notifying = NOTIFYING_NOTHING
	}

	internal fun notifyAttach() {
		notifying = NOTIFYING_ATTACH

		onAttach()
		check(notified) { "${this::class.qualifiedName} did not call through to super.onAttach()" }
		notified = false

		notifying = NOTIFYING_NOTHING
	}

	internal fun notifyDetach() {
		notifying = NOTIFYING_DETACH

		onDetach()
		check(notified) { "${this::class.qualifiedName} did not call through to super.onDetach()" }
		notified = false

		notifying = NOTIFYING_NOTHING
	}

	protected open fun onReady() {
		check(notifying == NOTIFYING_READY) {
			"onReady() can be only called by the engine"
		}
		check(!notified) {
			"${this::class.qualifiedName} called through to super.onReady() multiple times"
		}
		notified = true
	}

	protected open fun onParent() {
		check(notifying == NOTIFYING_PARENT) {
			"onParent() can be only called by the engine"
		}
		check(!notified) {
			"${this::class.qualifiedName} called through to super.onParent() multiple times"
		}
		notified = true
	}

	protected open fun onSeparate() {
		check(notifying == NOTIFYING_SEPARATE) {
			"onSeparate() can be only called by the engine"
		}
		check(!notified) {
			"${this::class.qualifiedName} called through to super.onSeparate() multiple times"
		}
		notified = true
	}

	protected open fun onAttach() {
		check(notifying == NOTIFYING_ATTACH) {
			"onAttach() can be only called by the engine"
		}
		check(!notified) {
			"${this::class.qualifiedName} called through to super.onAttach() multiple times"
		}
		notified = true
	}

	protected open fun onDetach() {
		check(notifying == NOTIFYING_DETACH) {
			"onDetach() can be only called by the engine"
		}
		check(!notified) {
			"${this::class.qualifiedName} called through to super.onDetach() multiple times"
		}
		notified = true
	}
}