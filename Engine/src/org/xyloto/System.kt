package org.xyloto

@Suppress("unused", "MemberVisibilityCanBePrivate")
open class System {

	companion object {
		private const val NOTIFYING_NOTHING: Byte = 0
		private const val NOTIFYING_START: Byte = 1
		private const val NOTIFYING_STOP: Byte = 2
	}

	private var notifying: Byte = NOTIFYING_NOTHING
	private var notified: Boolean = false

	internal fun notifyStart() {
		notifying = NOTIFYING_START

		onStart()
		check(notified) { "${this::class.qualifiedName} did not call through to super.onStart()" }
		notified = false

		notifying = NOTIFYING_NOTHING
	}

	internal fun notifyStop() {
		notifying = NOTIFYING_STOP

		onStop()
		check(notified) { "${this::class.qualifiedName} did not call through to super.onStop()" }
		notified = false

		notifying = NOTIFYING_NOTHING
	}

	protected open fun onStart() {
		check(notifying == NOTIFYING_START) {
			"onStart() can be only called by the engine"
		}
		check(!notified) {
			"${this::class.qualifiedName} called through to super.onStart() multiple times"
		}
		notified = true
	}

	protected open fun onStop() {
		check(notifying == NOTIFYING_STOP) {
			"onStop() can be only called by the engine"
		}
		check(!notified) {
			"${this::class.qualifiedName} called through to super.onStop() multiple times"
		}
		notified = true
	}

	internal fun update() {
		onUpdate()
	}

	protected open fun onUpdate() = Unit
}