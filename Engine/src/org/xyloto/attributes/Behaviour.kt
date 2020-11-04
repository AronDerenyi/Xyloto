package org.xyloto.attributes

import org.xyloto.System
import org.xyloto.collections.HandledCollection

abstract class Behaviour : HandledAttribute(behaviours) {

	companion object : System() {

		private val behaviours = HandledCollection<Behaviour>()

		override fun onUpdate() {
			behaviours.forEach { it.onUpdate() }
		}
	}

	protected abstract fun onUpdate()
}