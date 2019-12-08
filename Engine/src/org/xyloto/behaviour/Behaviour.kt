package org.xyloto.behaviour

import org.xyloto.Attribute

@Suppress("unused", "MemberVisibilityCanBePrivate")
open class Behaviour : Attribute() {

	private var calling: Boolean = false
	private var called: Boolean = false

	private var updatingEnabled: Boolean = false

	override fun onAttach() {
		super.onAttach()
		BehaviourSystem.addBehaviour(this)
	}

	override fun onDetach() {
		super.onDetach()
		BehaviourSystem.removeBehaviour(this)
	}

	internal fun enableUpdating() {
		updatingEnabled = true
	}

	internal fun disableUpdating() {
		updatingEnabled = false
	}

	internal fun update() {
		if (!updatingEnabled) return

		calling = true

		onUpdate()
		check(called) { "${this::class.qualifiedName} did not call through to super.onUpdate()" }
		called = false

		calling = false
	}

	protected open fun onUpdate() {
		check(calling) {
			"onUpdate() can be only called by the BehaviourSystem"
		}
		check(!called) {
			"${this::class.qualifiedName} called through to super.onUpdate() multiple times"
		}
		called = true
	}
}