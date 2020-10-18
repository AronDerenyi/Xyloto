package org.xyloto.behaviour

import org.xyloto.System

object BehaviourUpdater : System() {

	override fun onUpdate() {
		Behaviour.updateAll()
	}
}