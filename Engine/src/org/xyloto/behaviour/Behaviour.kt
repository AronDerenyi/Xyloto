package org.xyloto.behaviour

import org.xyloto.Attribute
import org.xyloto.collections.HandledCollection

abstract class Behaviour : Attribute() {

	companion object {
		private val behaviours = HandledCollection<Behaviour>()

		internal fun updateAll() {
			behaviours.forEach { it.update() }
		}
	}

	private var handle: HandledCollection<Behaviour>.Handle? = null

	override fun onAttach() {
		super.onAttach()
		handle = behaviours.Handle(this)
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