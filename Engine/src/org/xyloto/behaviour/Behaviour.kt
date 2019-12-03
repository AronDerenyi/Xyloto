package org.xyloto.behaviour

import org.xyloto.Attribute
import org.xyloto.Engine

@Suppress("unused", "MemberVisibilityCanBePrivate")
open class Behaviour : Attribute() {

	private lateinit var behaviourSystem: BehaviourSystem

	private var callingUpdate: Boolean = false
	private var calledUpdate: Boolean = false

	private var updatingEnabled: Boolean = false

	override fun onReady() {
		super.onReady()
		behaviourSystem = Engine.requireSystem()
	}

	override fun onAttached() {
		super.onAttached()
		behaviourSystem.addBehaviour(this)
	}

	override fun onDetached() {
		super.onDetached()
		behaviourSystem.removeBehaviour(this)
	}

	internal fun enableUpdating() {
		updatingEnabled = true
	}

	internal fun disableUpdating() {
		updatingEnabled = false
	}

	internal fun update() {
		if (!updatingEnabled) return

		callingUpdate = true

		onUpdate()
		check(calledUpdate) { "${this::class.qualifiedName} did not call through to super.onUpdate()" }
		calledUpdate = false

		callingUpdate = false
	}

	protected open fun onUpdate() {
		check(callingUpdate) { "onUpdate() can be only called by the BehaviourSystem" }
		check(!calledUpdate) { "${this::class.qualifiedName} called through to super.onUpdate() multiple times" }
		calledUpdate = true
	}
}