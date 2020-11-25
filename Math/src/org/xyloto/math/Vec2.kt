@file:Suppress("unused", "NOTHING_TO_INLINE")

package org.xyloto.math

import kotlin.math.acos
import kotlin.math.sqrt

data class Vec2(
	val x: Float,
	val y: Float
) {

	companion object {
		val ZERO = Vec2(0f, 0f)
		val ONE = Vec2(1f, 1f)
	}

	constructor(
		x: Number,
		y: Number
	) : this(
		x.toFloat(),
		y.toFloat()
	)

	inline val xy get() = this
	inline val yx get() = Vec2(y, x)

	inline val lengthSquared get() = x * x + y * y
	inline val length get() = sqrt(lengthSquared)

	override fun toString() = "[$x, $y]"
}

inline operator fun Vec2.unaryPlus() = this
inline operator fun Vec2.plus(v: Vec2) = Vec2(x + v.x, y + v.y)

inline operator fun Vec2.unaryMinus() = Vec2(-x, -y)
inline operator fun Vec2.minus(v: Vec2) = Vec2(x - v.x, y - v.y)

inline operator fun Vec2.times(f: Float) = Vec2(x * f, y * f)
inline operator fun Vec2.times(n: Number) = this * n.toFloat()
inline operator fun Vec2.times(v: Vec2) = Vec2(x * v.x, y * v.y)

inline operator fun Vec2.div(f: Float) = this * (1f / f)
inline operator fun Vec2.div(n: Number) = this / n.toFloat()
inline operator fun Vec2.div(v: Vec2) = Vec2(x / v.x, y / v.y)

inline fun Vec2.normalize() = this / length

inline infix fun Vec2.dot(v: Vec2) = x * v.x + y * v.y

inline fun angleBetween(v1: Vec2, v2: Vec2) = acos((v1 dot v2) / sqrt(v1.length * v2.length))