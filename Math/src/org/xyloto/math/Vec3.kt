@file:Suppress("unused", "NOTHING_TO_INLINE")

package org.xyloto.math

import kotlin.math.acos
import kotlin.math.sqrt

data class Vec3(
	val x: Float,
	val y: Float,
	val z: Float
) {

	companion object {
		val ZERO = Vec3(0f, 0f, 0f)
		val ONE = Vec3(1f, 1f, 1f)
	}

	constructor(
		x: Number,
		y: Number,
		z: Number
	) : this(
		x.toFloat(),
		y.toFloat(),
		z.toFloat()
	)

	constructor(v: Vec2, z: Number) : this(v.x, v.y, z.toFloat())
	constructor(x: Number, v: Vec2) : this(x.toFloat(), v.x, v.y)

	inline val xy get() = Vec2(x, y)
	inline val xz get() = Vec2(x, z)

	inline val yx get() = Vec2(y, x)
	inline val yz get() = Vec2(y, z)

	inline val zx get() = Vec2(z, x)
	inline val zy get() = Vec2(z, y)

	inline val xyz get() = this
	inline val xzy get() = Vec3(x, z, y)

	inline val yxz get() = Vec3(y, x, z)
	inline val yzx get() = Vec3(y, z, x)

	inline val zxy get() = Vec3(z, x, y)
	inline val zyx get() = Vec3(z, y, x)

	inline val lengthSquared get() = x * x + y * y + z * z
	inline val length get() = sqrt(lengthSquared)

	override fun toString() = "[$x, $y, $z]"
}

inline operator fun Vec3.unaryPlus() = this
inline operator fun Vec3.plus(v: Vec3) = Vec3(x + v.x, y + v.y, z + v.z)

inline operator fun Vec3.unaryMinus() = Vec3(-x, -y, -z)
inline operator fun Vec3.minus(v: Vec3) = Vec3(x - v.x, y - v.y, z - v.z)

inline operator fun Vec3.times(f: Float) = Vec3(x * f, y * f, z * f)
inline operator fun Vec3.times(n: Number) = this * n.toFloat()
inline operator fun Vec3.times(v: Vec3) = Vec3(x * v.x, y * v.y, z * v.z)

inline operator fun Vec3.div(f: Float) = this * (1f / f)
inline operator fun Vec3.div(n: Number) = this / n.toFloat()
inline operator fun Vec3.div(v: Vec3) = Vec3(x / v.x, y / v.y, z / v.z)

inline fun Vec3.normalize() = this / length

inline infix fun Vec3.dot(v: Vec3) = x * v.x + y * v.y + z * v.z
inline infix fun Vec3.cross(v: Vec3) = Vec3(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x)

inline fun angleBetween(v1: Vec3, v2: Vec3) = acos((v1 dot v2) / sqrt(v1.length * v2.length))