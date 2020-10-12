package org.xyloto.behaviour

import org.xyloto.Attribute
import org.xyloto.collections.HandledCollection

abstract class Behaviour : Attribute() {

	private var handle: HandledCollection<Behaviour>.Handle? = null

	override fun onAttach() {
		super.onAttach()
		handle = BehaviourSystem.behaviours.Handle(this)
	}

	override fun onDetach() {
		super.onDetach()
		handle?.remove()
		handle = null
	}

	internal fun update() {
		onUpdate()
	}

	protected open fun onUpdate() = Unit
}