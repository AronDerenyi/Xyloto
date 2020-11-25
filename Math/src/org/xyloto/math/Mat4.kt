@file:Suppress("unused", "NOTHING_TO_INLINE")

package org.xyloto.math

import kotlin.math.PI
import kotlin.math.tan

data class Mat4(
	val m00: Float,
	val m01: Float,
	val m02: Float,
	val m03: Float,
	val m10: Float,
	val m11: Float,
	val m12: Float,
	val m13: Float,
	val m20: Float,
	val m21: Float,
	val m22: Float,
	val m23: Float,
	val m30: Float,
	val m31: Float,
	val m32: Float,
	val m33: Float
) {

	companion object {
		val ZERO = Mat4(
			m00 = 0f, m10 = 0f, m20 = 0f, m30 = 0f,
			m01 = 0f, m11 = 0f, m21 = 0f, m31 = 0f,
			m02 = 0f, m12 = 0f, m22 = 0f, m32 = 0f,
			m03 = 0f, m13 = 0f, m23 = 0f, m33 = 0f
		)
		val IDENTITY = Mat4(
			m00 = 1f, m10 = 0f, m20 = 0f, m30 = 0f,
			m01 = 0f, m11 = 1f, m21 = 0f, m31 = 0f,
			m02 = 0f, m12 = 0f, m22 = 1f, m32 = 0f,
			m03 = 0f, m13 = 0f, m23 = 0f, m33 = 1f
		)

		fun of(quaternion: Quat): Mat4 {
			val xx = quaternion.x * quaternion.x * 2
			val xy = quaternion.x * quaternion.y * 2
			val xz = quaternion.x * quaternion.z * 2
			val xw = quaternion.x * quaternion.w * 2
			val yy = quaternion.y * quaternion.y * 2
			val yz = quaternion.y * quaternion.z * 2
			val yw = quaternion.y * quaternion.w * 2
			val zz = quaternion.z * quaternion.z * 2
			val zw = quaternion.z * quaternion.w * 2
			return Mat4(
				m00 = 1 - yy - zz, m10 = xy - zw, m20 = xz + yw, m30 = 0f,
				m01 = xy + zw, m11 = 1 - xx - zz, m21 = yz - xw, m31 = 0f,
				m02 = xz - yw, m12 = yz + xw, m22 = 1 - xx - yy, m32 = 0f,
				m03 = 0f, m13 = 0f, m23 = 0f, m33 = 1f
			)
		}

		fun of(
			aspectRatio: Float,
			nearClippingPlane: Float,
			farClippingPlane: Float,
			fieldOfView: Float,
			perspective: Boolean
		): Mat4 {
			val minusFarNearInverse = -1f / (farClippingPlane - nearClippingPlane)
			if (perspective) {
				val tanInverse = 1f / tan(fieldOfView / 360f * PI.toFloat())
				return Mat4(
					m00 = tanInverse / aspectRatio,
					m01 = 0f, m02 = 0f, m03 = 0f, m10 = 0f,
					m11 = tanInverse,
					m12 = 0f, m13 = 0f, m20 = 0f, m21 = 0f,
					m22 = (farClippingPlane + nearClippingPlane) * minusFarNearInverse,
					m23 = -1f, m30 = 0f, m31 = 0f,
					m32 = 2f * farClippingPlane * nearClippingPlane * minusFarNearInverse,
					m33 = 0f
				)
			} else {
				val fieldOfViewInverse = 1f / fieldOfView
				return Mat4(
					m00 = fieldOfViewInverse / aspectRatio,
					m01 = 0f, m02 = 0f, m03 = 0f, m10 = 0f,
					m11 = fieldOfViewInverse,
					m12 = 0f, m13 = 0f, m20 = 0f, m21 = 0f,
					m22 = 2f * minusFarNearInverse,
					m23 = 0f, m30 = 0f, m31 = 0f,
					m32 = (farClippingPlane + nearClippingPlane) * minusFarNearInverse,
					m33 = 1f
				)
			}
		}

		fun of(
			aspectRatio: Number,
			nearClippingPlane: Number,
			farClippingPlane: Number,
			fieldOfView: Number,
			perspective: Boolean
		) = of(
			aspectRatio.toFloat(),
			nearClippingPlane.toFloat(),
			farClippingPlane.toFloat(),
			fieldOfView.toFloat(),
			perspective
		)
	}

	constructor(
		xAxis: Vec4,
		yAxis: Vec4,
		zAxis: Vec4,
		wAxis: Vec4
	) : this(
		m00 = xAxis.x, m10 = yAxis.x, m20 = zAxis.x, m30 = wAxis.x,
		m01 = xAxis.y, m11 = yAxis.y, m21 = zAxis.y, m31 = wAxis.y,
		m02 = xAxis.z, m12 = yAxis.z, m22 = zAxis.z, m32 = wAxis.z,
		m03 = xAxis.w, m13 = yAxis.w, m23 = zAxis.w, m33 = wAxis.w
	)

	inline val determinant
		get() = m00 * (m11 * m22 * m33 + m12 * m23 * m31 + m13 * m21 * m32 - m13 * m22 * m31 - m11 * m23 * m32 - m12 * m21 * m33) -
				m01 * (m10 * m22 * m33 + m12 * m23 * m30 + m13 * m20 * m32 - m13 * m22 * m30 - m10 * m23 * m32 - m12 * m20 * m33) +
				m02 * (m10 * m21 * m33 + m11 * m23 * m30 + m13 * m20 * m31 - m13 * m21 * m30 - m10 * m23 * m31 - m11 * m20 * m33) -
				m03 * (m10 * m21 * m32 + m11 * m22 * m30 + m12 * m20 * m31 - m12 * m21 * m30 - m10 * m22 * m31 - m11 * m20 * m32)

	override fun toString() = "[$m00, $m10, $m20, $m30,\n $m01, $m11, $m21, $m31,\n $m02, $m12, $m22, $m32,\n $m03, $m13, $m23, $m33]"
}

inline operator fun Mat4.unaryPlus() = this

inline operator fun Mat4.plus(m: Mat4) = Mat4(
	m00 = m00 + m.m00, m10 = m10 + m.m10, m20 = m20 + m.m20, m30 = m30 + m.m30,
	m01 = m01 + m.m01, m11 = m11 + m.m11, m21 = m21 + m.m21, m31 = m31 + m.m31,
	m02 = m02 + m.m02, m12 = m12 + m.m12, m22 = m22 + m.m22, m32 = m32 + m.m32,
	m03 = m03 + m.m03, m13 = m13 + m.m13, m23 = m23 + m.m23, m33 = m33 + m.m33
)

inline operator fun Mat4.unaryMinus() = Mat4(
	m00 = -m00, m10 = -m10, m20 = -m20, m30 = -m30,
	m01 = -m01, m11 = -m11, m21 = -m21, m31 = -m31,
	m02 = -m02, m12 = -m12, m22 = -m22, m32 = -m32,
	m03 = -m03, m13 = -m13, m23 = -m23, m33 = -m33
)

inline operator fun Mat4.minus(m: Mat4) = Mat4(
	m00 = m00 - m.m00, m10 = m10 - m.m10, m20 = m20 - m.m20, m30 = m30 - m.m30,
	m01 = m01 - m.m01, m11 = m11 - m.m11, m21 = m21 - m.m21, m31 = m31 - m.m31,
	m02 = m02 - m.m02, m12 = m12 - m.m12, m22 = m22 - m.m22, m32 = m32 - m.m32,
	m03 = m03 - m.m03, m13 = m13 - m.m13, m23 = m23 - m.m23, m33 = m33 - m.m33
)

inline operator fun Mat4.times(f: Float) = Mat4(
	m00 = m00 * f, m10 = m10 * f, m20 = m20 * f, m30 = m30 * f,
	m01 = m01 * f, m11 = m11 * f, m21 = m21 * f, m31 = m31 * f,
	m02 = m02 * f, m12 = m12 * f, m22 = m22 * f, m32 = m32 * f,
	m03 = m03 * f, m13 = m13 * f, m23 = m23 * f, m33 = m33 * f
)

inline operator fun Mat4.times(n: Number) = this * n.toFloat()

inline operator fun Mat4.div(f: Float) = this * (1f / f)
inline operator fun Mat4.div(n: Number) = this / n.toFloat()

inline fun Mat4.transpose() = Mat4(
	m00 = m00, m10 = m01, m20 = m02, m30 = m03,
	m01 = m10, m11 = m11, m21 = m12, m31 = m13,
	m02 = m20, m12 = m21, m22 = m22, m32 = m23,
	m03 = m30, m13 = m31, m23 = m32, m33 = m33
)

inline fun Mat4.invert(): Mat4 {
	val factor = 1f / determinant
	return Mat4(
		m00 = (m11 * (m22 * m33 - m23 * m32) + m12 * (m23 * m31 - m21 * m33) + m13 * (m21 * m32 - m22 * m31)) * factor,
		m01 = -(m01 * (m22 * m33 - m23 * m32) + m02 * (m23 * m31 - m21 * m33) + m03 * (m21 * m32 - m22 * m31)) * factor,
		m02 = (m01 * (m12 * m33 - m13 * m32) + m02 * (m13 * m31 - m11 * m33) + m03 * (m11 * m32 - m12 * m31)) * factor,
		m03 = -(m01 * (m12 * m23 - m13 * m22) + m02 * (m13 * m21 - m11 * m23) + m03 * (m11 * m22 - m12 * m21)) * factor,
		m10 = -(m10 * (m22 * m33 - m23 * m32) + m12 * (m23 * m30 - m20 * m33) + m13 * (m20 * m32 - m22 * m30)) * factor,
		m11 = (m00 * (m22 * m33 - m23 * m32) + m02 * (m23 * m30 - m20 * m33) + m03 * (m20 * m32 - m22 * m30)) * factor,
		m12 = -(m00 * (m12 * m33 - m13 * m32) + m02 * (m13 * m30 - m10 * m33) + m03 * (m10 * m32 - m12 * m30)) * factor,
		m13 = (m00 * (m12 * m23 - m13 * m22) + m02 * (m13 * m20 - m10 * m23) + m03 * (m10 * m22 - m12 * m20)) * factor,
		m20 = (m10 * (m21 * m33 - m23 * m31) + m11 * (m23 * m30 - m20 * m33) + m13 * (m20 * m31 - m21 * m30)) * factor,
		m21 = -(m00 * (m21 * m33 - m23 * m31) + m01 * (m23 * m30 - m20 * m33) + m03 * (m20 * m31 - m21 * m30)) * factor,
		m22 = (m00 * (m11 * m33 - m13 * m31) + m01 * (m13 * m30 - m10 * m33) + m03 * (m10 * m31 - m11 * m30)) * factor,
		m23 = -(m00 * (m11 * m23 - m13 * m21) + m01 * (m13 * m20 - m10 * m23) + m03 * (m10 * m21 - m11 * m20)) * factor,
		m30 = -(m10 * (m21 * m32 - m22 * m31) + m11 * (m22 * m30 - m20 * m32) + m12 * (m20 * m31 - m21 * m30)) * factor,
		m31 = (m00 * (m21 * m32 - m22 * m31) + m01 * (m22 * m30 - m20 * m32) + m02 * (m20 * m31 - m21 * m30)) * factor,
		m32 = -(m00 * (m11 * m32 - m12 * m31) + m01 * (m12 * m30 - m10 * m32) + m02 * (m10 * m31 - m11 * m30)) * factor,
		m33 = (m00 * (m11 * m22 - m12 * m21) + m01 * (m12 * m20 - m10 * m22) + m02 * (m10 * m21 - m11 * m20)) * factor
	)
}

inline operator fun Mat4.times(v: Vec4) = Vec4(
	m00 * v.x + m10 * v.y + m20 * v.z + m30 * v.w,
	m01 * v.x + m11 * v.y + m21 * v.z + m31 * v.w,
	m02 * v.x + m12 * v.y + m22 * v.z + m32 * v.w,
	m03 * v.x + m13 * v.y + m23 * v.z + m33 * v.w
)

inline operator fun Mat4.times(m: Mat4) = Mat4(
	m00 * m.m00 + m10 * m.m01 + m20 * m.m02 + m30 * m.m03,
	m01 * m.m00 + m11 * m.m01 + m21 * m.m02 + m31 * m.m03,
	m02 * m.m00 + m12 * m.m01 + m22 * m.m02 + m32 * m.m03,
	m03 * m.m00 + m13 * m.m01 + m23 * m.m02 + m33 * m.m03,
	m00 * m.m10 + m10 * m.m11 + m20 * m.m12 + m30 * m.m13,
	m01 * m.m10 + m11 * m.m11 + m21 * m.m12 + m31 * m.m13,
	m02 * m.m10 + m12 * m.m11 + m22 * m.m12 + m32 * m.m13,
	m03 * m.m10 + m13 * m.m11 + m23 * m.m12 + m33 * m.m13,
	m00 * m.m20 + m10 * m.m21 + m20 * m.m22 + m30 * m.m23,
	m01 * m.m20 + m11 * m.m21 + m21 * m.m22 + m31 * m.m23,
	m02 * m.m20 + m12 * m.m21 + m22 * m.m22 + m32 * m.m23,
	m03 * m.m20 + m13 * m.m21 + m23 * m.m22 + m33 * m.m23,
	m00 * m.m30 + m10 * m.m31 + m20 * m.m32 + m30 * m.m33,
	m01 * m.m30 + m11 * m.m31 + m21 * m.m32 + m31 * m.m33,
	m02 * m.m30 + m12 * m.m31 + m22 * m.m32 + m32 * m.m33,
	m03 * m.m30 + m13 * m.m31 + m23 * m.m32 + m33 * m.m33
)
