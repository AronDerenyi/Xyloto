package org.xyloto.graphics

import org.lwjgl.opengl.GL
import org.xyloto.System

abstract class Graphics : System() {

	companion object {

		private var initialized = false
		internal var rendering = false
			private set

		fun init() {
			if (initialized) throw IllegalStateException("The graphics have already been initialized")
			initialized = true

			GL.createCapabilities()
		}
	}

	override fun onStart() {
		super.onStart()
		if (!initialized) throw IllegalStateException("The graphics have not been initialized")
	}

	final override fun onUpdate() {
		rendering = true
		onRender()
		rendering = false
	}

	protected abstract fun onRender()
}