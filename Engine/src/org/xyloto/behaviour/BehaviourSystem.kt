package org.xyloto.behaviour

import org.xyloto.System
import org.xyloto.collections.HandledCollection

object BehaviourSystem : System() {

	internal val behaviours = HandledCollection<Behaviour>()

	override fun onUpdate() {
		behaviours.forEach { it.update() }
	}
}