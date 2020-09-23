/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.util.math

import org.spongepowered.math.GenericMath
import org.spongepowered.math.imaginary.Quaterniond
import org.spongepowered.math.imaginary.Quaternionf
import org.spongepowered.math.vector.Vector2d
import org.spongepowered.math.vector.Vector2f
import org.spongepowered.math.vector.Vector2i
import org.spongepowered.math.vector.Vector3d
import org.spongepowered.math.vector.Vector3f
import org.spongepowered.math.vector.Vector3i
import kotlin.math.ceil

inline operator fun Vector2d.component1(): Double = this.x
inline operator fun Vector2d.component2(): Double = this.y
inline operator fun Vector2d.times(value: Double): Vector2d = this.mul(value)
inline operator fun Vector2d.times(value: Float): Vector2d = this.mul(value)
inline operator fun Vector2d.times(value: Int): Vector2d = this.mul(value.toDouble())
inline operator fun Vector2d.times(value: Vector2d): Vector2d = this.mul(value)
inline operator fun Vector2d.times(value: Vector2f): Vector2d = this.mul(value.x, value.y)
inline operator fun Vector2d.times(value: Vector2i): Vector2d = this.mul(value.x.toDouble(), value.y.toDouble())
inline operator fun Vector2d.minus(value: Vector2d): Vector2d = this.sub(value)
inline operator fun Vector2d.minus(value: Vector2f): Vector2d = this.sub(value.x, value.y)
inline operator fun Vector2d.minus(value: Vector2i): Vector2d = this.sub(value.x.toDouble(), value.y.toDouble())
inline operator fun Vector2d.plus(value: Vector2d): Vector2d = this.add(value)
inline operator fun Vector2d.plus(value: Vector2f): Vector2d = this.add(value.x, value.y)
inline operator fun Vector2d.plus(value: Vector2i): Vector2d = this.add(value.x.toDouble(), value.y.toDouble())
inline operator fun Vector2d.unaryMinus(): Vector2d = this.negate()
inline operator fun Vector2d.unaryPlus(): Vector2d = this

inline operator fun Vector2f.component1(): Float = this.x
inline operator fun Vector2f.component2(): Float = this.y
inline operator fun Vector2f.times(value: Double): Vector2f = this.mul(value)
inline operator fun Vector2f.times(value: Float): Vector2f = this.mul(value)
inline operator fun Vector2f.times(value: Int): Vector2f = this.mul(value.toDouble())
inline operator fun Vector2f.times(value: Vector2f): Vector2f = this.mul(value)
inline operator fun Vector2f.times(value: Vector2d): Vector2f = this.mul(value.x, value.y)
inline operator fun Vector2f.times(value: Vector2i): Vector2f = this.mul(value.x.toFloat(), value.y.toFloat())
inline operator fun Vector2f.minus(value: Vector2f): Vector2f = this.sub(value)
inline operator fun Vector2f.minus(value: Vector2d): Vector2f = this.sub(value.x, value.y)
inline operator fun Vector2f.minus(value: Vector2i): Vector2f = this.sub(value.x.toFloat(), value.y.toFloat())
inline operator fun Vector2f.plus(value: Vector2f): Vector2f = this.add(value)
inline operator fun Vector2f.plus(value: Vector2d): Vector2f = this.add(value.x, value.y)
inline operator fun Vector2f.plus(value: Vector2i): Vector2f = this.add(value.x.toFloat(), value.y.toFloat())
inline operator fun Vector2f.unaryMinus(): Vector2f = this.negate()
inline operator fun Vector2f.unaryPlus(): Vector2f = this

inline operator fun Vector2i.component1(): Int = this.x
inline operator fun Vector2i.component2(): Int = this.y
inline operator fun Vector2i.times(value: Int): Vector2i = this.mul(value)
inline operator fun Vector2i.times(value: Float): Vector2i = this.mul(value.toInt())
inline operator fun Vector2i.times(value: Double): Vector2i = this.mul(value)
inline operator fun Vector2i.times(value: Vector2i): Vector2i = this.mul(value)
inline operator fun Vector2i.times(value: Vector2d): Vector2i = this.mul(value.x, value.y)
inline operator fun Vector2i.times(value: Vector2f): Vector2i = this.mul(value.x.toDouble(), value.y.toDouble())
inline operator fun Vector2i.minus(value: Vector2i): Vector2i = this.sub(value)
inline operator fun Vector2i.minus(value: Vector2d): Vector2i = this.sub(value.x, value.y)
inline operator fun Vector2i.minus(value: Vector2f): Vector2i = this.sub(value.x.toDouble(), value.y.toDouble())
inline operator fun Vector2i.plus(value: Vector2i): Vector2i = this.add(value)
inline operator fun Vector2i.plus(value: Vector2d): Vector2i = this.add(value.x, value.y)
inline operator fun Vector2i.plus(value: Vector2f): Vector2i = this.add(value.x.toDouble(), value.y.toDouble())
inline operator fun Vector2i.unaryMinus(): Vector2i = this.negate()
inline operator fun Vector2i.unaryPlus(): Vector2i = this

inline operator fun Vector3d.component1(): Double = this.x
inline operator fun Vector3d.component2(): Double = this.y
inline operator fun Vector3d.component3(): Double = this.z
inline operator fun Vector3d.times(value: Double): Vector3d = this.mul(value)
inline operator fun Vector3d.times(value: Float): Vector3d = this.mul(value)
inline operator fun Vector3d.times(value: Int): Vector3d = this.mul(value.toDouble())
inline operator fun Vector3d.times(value: Vector3d): Vector3d = this.mul(value)
inline operator fun Vector3d.times(value: Vector3f): Vector3d = this.mul(value.x, value.y, value.z)
inline operator fun Vector3d.times(value: Vector3i): Vector3d = this.mul(value.x.toDouble(), value.y.toDouble(), value.z.toDouble())
inline operator fun Vector3d.minus(value: Vector3d): Vector3d = this.sub(value)
inline operator fun Vector3d.minus(value: Vector3f): Vector3d = this.sub(value.x, value.y, value.z)
inline operator fun Vector3d.minus(value: Vector3i): Vector3d = this.sub(value.x.toDouble(), value.y.toDouble(), value.z.toDouble())
inline operator fun Vector3d.plus(value: Vector3d): Vector3d = this.add(value)
inline operator fun Vector3d.plus(value: Vector3f): Vector3d = this.add(value.x, value.y, value.z)
inline operator fun Vector3d.plus(value: Vector3i): Vector3d = this.add(value.x.toDouble(), value.y.toDouble(), value.z.toDouble())
inline operator fun Vector3d.unaryMinus(): Vector3d = this.negate()
inline operator fun Vector3d.unaryPlus(): Vector3d = this

inline operator fun Vector3f.component1(): Float = this.x
inline operator fun Vector3f.component2(): Float = this.y
inline operator fun Vector3f.component3(): Float = this.z
inline operator fun Vector3f.times(value: Double): Vector3f = this.mul(value)
inline operator fun Vector3f.times(value: Float): Vector3f = this.mul(value)
inline operator fun Vector3f.times(value: Int): Vector3f = this.mul(value.toDouble())
inline operator fun Vector3f.times(value: Vector3f): Vector3f = this.mul(value)
inline operator fun Vector3f.times(value: Vector3d): Vector3f = this.mul(value.x, value.y, value.z)
inline operator fun Vector3f.times(value: Vector3i): Vector3f = this.mul(value.x.toFloat(), value.y.toFloat(), value.z.toFloat())
inline operator fun Vector3f.minus(value: Vector3f): Vector3f = this.sub(value)
inline operator fun Vector3f.minus(value: Vector3d): Vector3f = this.sub(value.x, value.y, value.z)
inline operator fun Vector3f.minus(value: Vector3i): Vector3f = this.sub(value.x.toFloat(), value.y.toFloat(), value.z.toFloat())
inline operator fun Vector3f.plus(value: Vector3f): Vector3f = this.add(value)
inline operator fun Vector3f.plus(value: Vector3d): Vector3f = this.add(value.x, value.y, value.z)
inline operator fun Vector3f.plus(value: Vector3i): Vector3f = this.add(value.x.toFloat(), value.y.toFloat(), value.z.toFloat())
inline operator fun Vector3f.unaryMinus(): Vector3f = this.negate()
inline operator fun Vector3f.unaryPlus(): Vector3f = this

inline operator fun Vector3i.component1(): Int = this.x
inline operator fun Vector3i.component2(): Int = this.y
inline operator fun Vector3i.component3(): Int = this.z
inline operator fun Vector3i.times(value: Int): Vector3i = this.mul(value)
inline operator fun Vector3i.times(value: Float): Vector3i = this.mul(value.toInt())
inline operator fun Vector3i.times(value: Double): Vector3i = this.mul(value)
inline operator fun Vector3i.times(value: Vector3i): Vector3i = this.mul(value)
inline operator fun Vector3i.times(value: Vector3d): Vector3i = this.mul(value.x, value.y, value.z)
inline operator fun Vector3i.times(value: Vector3f): Vector3i = this.mul(value.x.toDouble(), value.y.toDouble(), value.z.toDouble())
inline operator fun Vector3i.minus(value: Vector3i): Vector3i = this.sub(value)
inline operator fun Vector3i.minus(value: Vector3d): Vector3i = this.sub(value.x, value.y, value.z)
inline operator fun Vector3i.minus(value: Vector3f): Vector3i = this.sub(value.x.toDouble(), value.y.toDouble(), value.z.toDouble())
inline operator fun Vector3i.plus(value: Vector3i): Vector3i = this.add(value)
inline operator fun Vector3i.plus(value: Vector3d): Vector3i = this.add(value.x, value.y, value.z)
inline operator fun Vector3i.plus(value: Vector3f): Vector3i = this.add(value.x.toDouble(), value.y.toDouble(), value.z.toDouble())
inline operator fun Vector3i.unaryMinus(): Vector3i = this.negate()
inline operator fun Vector3i.unaryPlus(): Vector3i = this

inline operator fun Quaterniond.times(value: Vector3d): Vector3d = this.rotate(value)
inline operator fun Quaterniond.times(value: Vector3f): Vector3d = this.rotate(value.x, value.y, value.z)

inline operator fun Quaternionf.times(value: Vector3f): Vector3f = this.rotate(value)
inline operator fun Quaternionf.times(value: Vector3d): Vector3f = this.rotate(value.x, value.y, value.z)

fun Double.floorToInt(): Int =
        GenericMath.floor(this)

fun Double.ceilToInt(): Int =
        ceil(this).toInt()
