package org.xyloto

@Suppress("unused", "MemberVisibilityCanBePrivate")
open class System {

	private var notifyingStart: Boolean = false
	private var notifiedStart: Boolean = false

	private var notifyingStop: Boolean = false
	private var notifiedStop: Boolean = false

	private var callingUpdate: Boolean = false
	private var calledUpdate: Boolean = false

	internal fun notifyStart() {
		notifyingStart = true

		onStart()
		check(notifiedStart) { "${this::class.qualifiedName} did not call through to super.onStart()" }
		notifiedStart = false

		notifyingStart = false
	}

	internal fun notifyStop() {
		notifyingStop = true

		onStop()
		check(notifiedStop) { "${this::class.qualifiedName} did not call through to super.onStop()" }
		notifiedStop = false

		notifyingStop = false
	}

	internal fun update() {
		callingUpdate = true

		onUpdate()
		check(calledUpdate) { "${this::class.qualifiedName} did not call through to super.onUpdate()" }
		calledUpdate = false

		callingUpdate = false
	}

	protected open fun onStart() {
		check(notifyingStart) { "onStart() can be only called by the engine" }
		check(!notifiedStart) { "${this::class.qualifiedName} called through to super.onStart() multiple times" }
		notifiedStart = true
	}

	protected open fun onStop() {
		check(notifyingStop) { "onStop() can be only called by the engine" }
		check(!notifiedStop) { "${this::class.qualifiedName} called through to super.onStop() multiple times" }
		notifiedStop = true
	}

	protected open fun onUpdate() {
		check(callingUpdate) { "onUpdate() can be only called by the engine" }
		check(!calledUpdate) { "${this::class.qualifiedName} called through to super.onUpdate() multiple times" }
		calledUpdate = true
	}
}