package org.xyloto.attributes

import org.xyloto.Attribute
import org.xyloto.collections.HandledCollection

abstract class HandledAttribute(collection: HandledCollection<*>) : Attribute() {

	@Suppress("UNCHECKED_CAST")
	private val collection: HandledCollection<HandledAttribute> = collection as HandledCollection<HandledAttribute>
	private lateinit var handle: HandledCollection<HandledAttribute>.Handle

	override fun onAttach() {
		super.onAttach()
		handle = collection.Handle(this)
	}

	override fun onDetach() {
		super.onDetach()
		handle.remove()
	}
}