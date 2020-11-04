package org.xyloto.attributes

import org.xyloto.Attribute
import org.xyloto.collections.HandledCollection

abstract class HandledAttribute<T : HandledAttribute<T>>(
	private val collection: HandledCollection<T>
) : Attribute() {

	private var handle: HandledCollection<T>.Handle? = null

	@Suppress("UNCHECKED_CAST")
	override fun onAttach() {
		super.onAttach()
		handle = collection.Handle(this as T)
	}

	override fun onDetach() {
		super.onDetach()
		handle?.remove()
		handle = null
	}
}