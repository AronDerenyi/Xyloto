@file:Suppress("unused", "NOTHING_TO_INLINE")

package org.xyloto.math

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Quat(
	val x: Float,
	val y: Float,
	val z: Float,
	val w: Float
) {

	companion object {
		val IDENTITY = Quat(0f, 0f, 0f, 1f)

		fun of(axis: Vec3, angle: Float): Quat {
			val axisFactor = sin(angle * 0.5f) / axis.length
			val angleFactor = cos(angle * 0.5f)
			return Quat(
				axis.x * axisFactor,
				axis.y * axisFactor,
				axis.z * axisFactor,
				angleFactor
			)
		}

		fun of(axis: Vec3, angle: Number) = of(axis, angle.toFloat())
	}

	constructor(
		x: Number,
		y: Number,
		z: Number,
		w: Number
	) : this(
		x.toFloat(),
		y.toFloat(),
		z.toFloat(),
		w.toFloat()
	)

	inline val lengthSquared get() = x * x + y * y + z * z + w * w
	inline val length get() = sqrt(lengthSquared)

	override fun toString() = "[$x, $y, $z, $w]"
}

inline operator fun Quat.unaryPlus() = this
inline operator fun Quat.unaryMinus() = Quat(-x, -y, -z, -w)

inline fun Quat.normalize(): Quat {
	val factor = 1f / length
	return Quat(x * factor, y * factor, z * factor, w * factor)
}

inline fun Quat.invert(): Quat {
	val factor = 1f / lengthSquared
	return Quat(-x * factor, -y * factor, -z * factor, w * factor)
}

inline infix fun Quat.dot(q: Quat) = x * q.x + y * q.y + z * q.z + w * q.w

inline operator fun Quat.times(q: Quat) = Quat(
	x * q.w + w * q.x + y * q.z - z * q.y,
	y * q.w + w * q.y + z * q.x - x * q.z,
	z * q.w + w * q.z + x * q.y - y * q.x,
	w * q.w - x * q.x - y * q.y - z * q.z
)

inline operator fun Quat.div(q: Quat): Quat {
	val factor = 1f / q.lengthSquared
	return Quat(
		(x * q.w - w * q.x - y * q.z + z * q.y) * factor,
		(y * q.w - w * q.y - z * q.x + x * q.z) * factor,
		(z * q.w - w * q.z - x * q.y + y * q.x) * factor,
		(w * q.w + x * q.x + y * q.y + z * q.z) * factor
	)
}