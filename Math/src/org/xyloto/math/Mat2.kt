@file:Suppress("unused", "NOTHING_TO_INLINE")

package org.xyloto.math

data class Mat2(
	val m00: Float,
	val m01: Float,
	val m10: Float,
	val m11: Float
) {

	companion object {
		val ZERO = Mat2(
			m00 = 0f, m10 = 0f,
			m01 = 0f, m11 = 0f
		)
		val IDENTITY = Mat2(
			m00 = 1f, m10 = 0f,
			m01 = 0f, m11 = 1f
		)
	}

	constructor(
		xAxis: Vec2,
		yAxis: Vec2
	) : this(
		m00 = xAxis.x, m10 = yAxis.x,
		m01 = xAxis.y, m11 = yAxis.y
	)

	inline val determinant
		get() = m00 * m11 - m01 * m10

	override fun toString() = "[$m00, $m10,\n $m01, $m11]"
}

inline operator fun Mat2.unaryPlus() = this

inline operator fun Mat2.plus(m: Mat2) = Mat2(
	m00 = m00 + m.m00, m10 = m10 + m.m10,
	m01 = m01 + m.m01, m11 = m11 + m.m11
)

inline operator fun Mat2.unaryMinus() = Mat2(
	m00 = -m00, m10 = -m10,
	m01 = -m01, m11 = -m11
)

inline operator fun Mat2.minus(m: Mat3) = Mat2(
	m00 = m00 - m.m00, m10 = m10 - m.m10,
	m01 = m01 - m.m01, m11 = m11 - m.m11
)

inline operator fun Mat2.times(f: Float) = Mat2(
	m00 = m00 * f, m10 = m10 * f,
	m01 = m01 * f, m11 = m11 * f
)

inline operator fun Mat2.times(n: Number) = this * n.toFloat()

inline operator fun Mat2.div(f: Float) = this * (1f / f)
inline operator fun Mat2.div(n: Number) = this / n.toFloat()

inline fun Mat2.transpose() = Mat2(
	m00 = m00, m10 = m01,
	m01 = m10, m11 = m11
)

inline fun Mat2.invert(): Mat2 {
	val factor = 1f / determinant
	return Mat2(
		m00 = m11 * factor,
		m01 = -m01 * factor,
		m10 = -m10 * factor,
		m11 = m00 * factor
	)
}

inline operator fun Mat2.times(v: Vec2) = Vec2(
	m00 * v.x + m10 * v.y,
	m01 * v.x + m11 * v.y
)

inline operator fun Mat2.times(m: Mat2) = Mat2(
	m00 * m.m00 + m10 * m.m01,
	m01 * m.m00 + m11 * m.m01,
	m00 * m.m10 + m10 * m.m11,
	m01 * m.m10 + m11 * m.m11
)
