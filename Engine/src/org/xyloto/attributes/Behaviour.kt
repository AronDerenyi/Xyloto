package org.xyloto.attributes

import org.xyloto.System
import org.xyloto.collections.HandledCollection

abstract class Behaviour : HandledAttribute<Behaviour>(behaviours) {

	companion object : System() {

		private val behaviours = HandledCollection<Behaviour>()

		override fun onUpdate() {
			behaviours.forEach { it.update() }
		}
	}

	internal fun update() {
		onUpdate()
	}

	protected open fun onUpdate() = Unit
}