package org.xyloto.behaviour

import org.xyloto.Attribute

@Suppress("unused", "MemberVisibilityCanBePrivate")
open class Behaviour : Attribute() {

	internal var updating: Boolean = false

	override fun onAttach() {
		super.onAttach()
		BehaviourSystem.addBehaviour(this)
	}

	override fun onDetach() {
		super.onDetach()
		BehaviourSystem.removeBehaviour(this)
	}

	internal fun update() {
		onUpdate()
	}

	protected open fun onUpdate() = Unit
}