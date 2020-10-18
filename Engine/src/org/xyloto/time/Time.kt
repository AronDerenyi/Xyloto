package org.xyloto.time

import org.xyloto.System

object Time : System() {

	private var started = false
	private var start = 0L
	private var last = 0L

	var updateNumber = 0L; private set
	var current = 0.0; private set
	var delta = 0f; private set

	override fun onUpdate() {
		val nanoTime = java.lang.System.nanoTime()
		if (started) {
			updateNumber++
			current = ((nanoTime - start).toDouble() / 1000000000)
			delta = ((nanoTime - last).toFloat() / 1000000000)

			last = nanoTime
		} else {
			start = nanoTime
			last = nanoTime
		}
	}
}