@file:Suppress("unused", "NOTHING_TO_INLINE")

package org.xyloto.math

data class Mat3(
	val m00: Float,
	val m01: Float,
	val m02: Float,
	val m10: Float,
	val m11: Float,
	val m12: Float,
	val m20: Float,
	val m21: Float,
	val m22: Float
) {

	companion object {
		val ZERO = Mat3(
			m00 = 0f, m10 = 0f, m20 = 0f,
			m01 = 0f, m11 = 0f, m21 = 0f,
			m02 = 0f, m12 = 0f, m22 = 0f
		)
		val IDENTITY = Mat3(
			m00 = 1f, m10 = 0f, m20 = 0f,
			m01 = 0f, m11 = 1f, m21 = 0f,
			m02 = 0f, m12 = 0f, m22 = 1f
		)

		fun of(quaternion: Quat): Mat3 {
			val xx = quaternion.x * quaternion.x * 2
			val xy = quaternion.x * quaternion.y * 2
			val xz = quaternion.x * quaternion.z * 2
			val xw = quaternion.x * quaternion.w * 2
			val yy = quaternion.y * quaternion.y * 2
			val yz = quaternion.y * quaternion.z * 2
			val yw = quaternion.y * quaternion.w * 2
			val zz = quaternion.z * quaternion.z * 2
			val zw = quaternion.z * quaternion.w * 2
			return Mat3(
				m00 = 1 - yy - zz, m10 = xy - zw, m20 = xz + yw,
				m01 = xy + zw, m11 = 1 - xx - zz, m21 = yz - xw,
				m02 = xz - yw, m12 = yz + xw, m22 = 1 - xx - yy
			)
		}
	}

	constructor(
		xAxis: Vec3,
		yAxis: Vec3,
		zAxis: Vec3
	) : this(
		m00 = xAxis.x, m10 = yAxis.x, m20 = zAxis.x,
		m01 = xAxis.y, m11 = yAxis.y, m21 = zAxis.y,
		m02 = xAxis.z, m12 = yAxis.z, m22 = zAxis.z
	)

	inline val determinant
		get() = m00 * (m11 * m22 - m12 * m21) +
				m01 * (m12 * m20 - m10 * m22) +
				m02 * (m10 * m21 - m11 * m20)

	override fun toString() = "[$m00, $m10, $m20,\n $m01, $m11, $m21,\n $m02, $m12, $m22]"
}

inline operator fun Mat3.unaryPlus() = this

inline operator fun Mat3.plus(m: Mat3) = Mat3(
	m00 = m00 + m.m00, m10 = m10 + m.m10, m20 = m20 + m.m20,
	m01 = m01 + m.m01, m11 = m11 + m.m11, m21 = m21 + m.m21,
	m02 = m02 + m.m02, m12 = m12 + m.m12, m22 = m22 + m.m22
)

inline operator fun Mat3.unaryMinus() = Mat3(
	m00 = -m00, m10 = -m10, m20 = -m20,
	m01 = -m01, m11 = -m11, m21 = -m21,
	m02 = -m02, m12 = -m12, m22 = -m22
)

inline operator fun Mat3.minus(m: Mat3) = Mat3(
	m00 = m00 - m.m00, m10 = m10 - m.m10, m20 = m20 - m.m20,
	m01 = m01 - m.m01, m11 = m11 - m.m11, m21 = m21 - m.m21,
	m02 = m02 - m.m02, m12 = m12 - m.m12, m22 = m22 - m.m22
)

inline operator fun Mat3.times(f: Float) = Mat3(
	m00 = m00 * f, m10 = m10 * f, m20 = m20 * f,
	m01 = m01 * f, m11 = m11 * f, m21 = m21 * f,
	m02 = m02 * f, m12 = m12 * f, m22 = m22 * f
)

inline operator fun Mat3.times(n: Number) = this * n.toFloat()

inline operator fun Mat3.div(f: Float) = this * (1f / f)
inline operator fun Mat3.div(n: Number) = this / n.toFloat()

inline fun Mat3.transpose() = Mat3(
	m00 = m00, m10 = m01, m20 = m02,
	m01 = m10, m11 = m11, m21 = m12,
	m02 = m20, m12 = m21, m22 = m22
)

inline fun Mat3.invert(): Mat3 {
	val factor = 1f / determinant
	return Mat3(
		m00 = (m11 * m22 - m12 * m21) * factor,
		m01 = -(m10 * m22 - m12 * m20) * factor,
		m02 = (m01 * m12 - m02 * m11) * factor,
		m10 = -(m10 * m22 - m12 * m20) * factor,
		m11 = (m00 * m22 - m02 * m20) * factor,
		m12 = -(m00 * m12 - m02 * m10) * factor,
		m20 = (m10 * m21 - m11 * m20) * factor,
		m21 = -(m00 * m21 - m01 * m20) * factor,
		m22 = (m00 * m11 - m01 * m10) * factor
	)
}

inline operator fun Mat3.times(v: Vec3) = Vec3(
	m00 * v.x + m10 * v.y + m20 * v.z,
	m01 * v.x + m11 * v.y + m21 * v.z,
	m02 * v.x + m12 * v.y + m22 * v.z
)

inline operator fun Mat3.times(m: Mat3) = Mat3(
	m00 * m.m00 + m10 * m.m01 + m20 * m.m02,
	m01 * m.m00 + m11 * m.m01 + m21 * m.m02,
	m02 * m.m00 + m12 * m.m01 + m22 * m.m02,
	m00 * m.m10 + m10 * m.m11 + m20 * m.m12,
	m01 * m.m10 + m11 * m.m11 + m21 * m.m12,
	m02 * m.m10 + m12 * m.m11 + m22 * m.m12,
	m00 * m.m20 + m10 * m.m21 + m20 * m.m22,
	m01 * m.m20 + m11 * m.m21 + m21 * m.m22,
	m02 * m.m20 + m12 * m.m21 + m22 * m.m22
)
