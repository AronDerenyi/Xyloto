@file:Suppress("unused", "NOTHING_TO_INLINE")

package org.xyloto.math

import kotlin.math.sqrt

data class Vec4(
	val x: Float,
	val y: Float,
	val z: Float,
	val w: Float
) {

	companion object {
		val ZERO = Vec4(0f, 0f, 0f, 0f)
		val ONE = Vec4(1f, 1f, 1f, 1f)
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

	constructor(v: Vec2, z: Number, w: Number) : this(v.x, v.y, z.toFloat(), w.toFloat())
	constructor(x: Number, v: Vec2, w: Number) : this(x.toFloat(), v.x, v.y, w.toFloat())
	constructor(x: Number, y: Number, v: Vec2) : this(x.toFloat(), y.toFloat(), v.x, v.y)

	constructor(v: Vec3, w: Number) : this(v.x, v.y, v.z, w.toFloat())
	constructor(x: Number, v: Vec3) : this(x.toFloat(), v.x, v.y, v.z)

	inline val xy get() = Vec2(x, y)
	inline val xz get() = Vec2(x, z)
	inline val xw get() = Vec2(x, w)

	inline val yx get() = Vec2(y, x)
	inline val yz get() = Vec2(y, z)
	inline val yw get() = Vec2(y, w)

	inline val zx get() = Vec2(z, x)
	inline val zy get() = Vec2(z, y)
	inline val zw get() = Vec2(z, w)

	inline val wx get() = Vec2(w, x)
	inline val wy get() = Vec2(w, y)
	inline val wz get() = Vec2(w, z)

	inline val xyz get() = Vec3(x, y, z)
	inline val xyw get() = Vec3(x, y, w)
	inline val xzy get() = Vec3(x, z, y)
	inline val xzw get() = Vec3(x, z, w)
	inline val xwy get() = Vec3(x, w, y)
	inline val xwz get() = Vec3(x, w, z)

	inline val yxz get() = Vec3(y, x, z)
	inline val yxw get() = Vec3(y, x, w)
	inline val yzx get() = Vec3(y, z, x)
	inline val yzw get() = Vec3(y, z, w)
	inline val ywx get() = Vec3(y, w, x)
	inline val ywz get() = Vec3(y, w, z)

	inline val zxy get() = Vec3(z, x, y)
	inline val zxw get() = Vec3(z, x, w)
	inline val zyx get() = Vec3(z, y, x)
	inline val zyw get() = Vec3(z, y, w)
	inline val zwx get() = Vec3(z, w, x)
	inline val zwy get() = Vec3(z, w, y)

	inline val wxy get() = Vec3(w, x, y)
	inline val wxz get() = Vec3(w, x, z)
	inline val wyx get() = Vec3(w, y, x)
	inline val wyz get() = Vec3(w, y, z)
	inline val wzx get() = Vec3(w, z, x)
	inline val wzy get() = Vec3(w, z, y)

	inline val xyzw get() = Vec4(x, y, z, w)
	inline val xywz get() = Vec4(x, y, w, z)
	inline val xzyw get() = Vec4(x, z, y, w)
	inline val xzwy get() = Vec4(x, z, w, y)
	inline val xwyz get() = Vec4(x, w, y, z)
	inline val xwzy get() = Vec4(x, w, z, y)

	inline val yxzw get() = Vec4(y, x, z, w)
	inline val yxwz get() = Vec4(y, x, w, z)
	inline val yzxw get() = Vec4(y, z, x, w)
	inline val yzwx get() = Vec4(y, z, w, x)
	inline val ywxz get() = Vec4(y, w, x, z)
	inline val ywzx get() = Vec4(y, w, z, x)

	inline val zxyw get() = Vec4(z, x, y, w)
	inline val zxwy get() = Vec4(z, x, w, y)
	inline val zyxw get() = Vec4(z, y, x, w)
	inline val zywx get() = Vec4(z, y, w, x)
	inline val zwxy get() = Vec4(z, w, x, y)
	inline val zwyx get() = Vec4(z, w, y, x)

	inline val wxyz get() = Vec4(w, x, y, z)
	inline val wxzy get() = Vec4(w, x, z, y)
	inline val wyxz get() = Vec4(w, y, x, z)
	inline val wyzx get() = Vec4(w, y, z, x)
	inline val wzxy get() = Vec4(w, z, x, y)
	inline val wzyx get() = Vec4(w, z, y, x)

	inline val lengthSquared get() = x * x + y * y + z * z + w * w
	inline val length get() = sqrt(lengthSquared)

	override fun toString() = "[$x, $y, $z, $w]"
}

inline operator fun Vec4.unaryPlus() = this
inline operator fun Vec4.plus(v: Vec4) = Vec4(x + v.x, y + v.y, z + v.z, w + v.w)

inline operator fun Vec4.unaryMinus() = Vec4(-x, -y, -z, -w)
inline operator fun Vec4.minus(v: Vec4) = Vec4(x - v.x, y - v.y, z - v.z, w - v.w)

inline operator fun Vec4.times(f: Float) = Vec4(x * f, y * f, z * f, w * f)
inline operator fun Vec4.times(n: Number) = this * n.toFloat()
inline operator fun Vec4.times(v: Vec4) = Vec4(x * v.x, y * v.y, z * v.z, w * v.w)

inline operator fun Vec4.div(f: Float) = this * (1f / f)
inline operator fun Vec4.div(n: Number) = this / n.toFloat()
inline operator fun Vec4.div(v: Vec4) = Vec4(x / v.x, y / v.y, z / v.z, w / v.w)

inline fun Vec4.normalize() = this / length

inline infix fun Vec4.dot(v: Vec4) = x * v.x + y * v.y + z * v.z + w * v.w