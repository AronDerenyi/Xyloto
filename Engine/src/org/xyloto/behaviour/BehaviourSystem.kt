package org.xyloto.behaviour

import org.xyloto.System

@Suppress("unused", "MemberVisibilityCanBePrivate")
object BehaviourSystem : System() {

	private val behaviours = mutableSetOf<Behaviour>()

	internal fun addBehaviour(behaviour: Behaviour) {
		check(behaviours.add(behaviour)) { "The behaviour has already been added to the system" }
	}

	internal fun removeBehaviour(behaviour: Behaviour) {
		check(behaviours.remove(behaviour)) { "The behaviour has not been added to the system yet" }
		behaviour.disableUpdating()
	}

	override fun onUpdate() {
		super.onUpdate()

		val behaviours = behaviours.toList()
		behaviours.forEach(Behaviour::enableUpdating)
		behaviours.forEach(Behaviour::update)
	}
}