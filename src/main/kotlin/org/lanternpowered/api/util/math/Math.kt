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
package org.lanternpowered.api.util.math

import org.lanternpowered.api.world.chunk.ChunkPosition
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

operator fun Vector2d.component1(): Double = this.x
operator fun Vector2d.component2(): Double = this.y
operator fun Vector2d.times(value: Double): Vector2d = mul(value)
operator fun Vector2d.times(value: Float): Vector2d = mul(value)
operator fun Vector2d.times(value: Int): Vector2d = mul(value.toDouble())
operator fun Vector2d.times(value: Vector2d): Vector2d = mul(value)
operator fun Vector2d.times(value: Vector2f): Vector2d = mul(value.x, value.y)
operator fun Vector2d.times(value: Vector2i): Vector2d = mul(value.x.toDouble(), value.y.toDouble())
operator fun Vector2d.minus(value: Vector2d): Vector2d = sub(value)
operator fun Vector2d.minus(value: Vector2f): Vector2d = sub(value.x, value.y)
operator fun Vector2d.minus(value: Vector2i): Vector2d = sub(value.x.toDouble(), value.y.toDouble())
operator fun Vector2d.plus(value: Vector2d): Vector2d = add(value)
operator fun Vector2d.plus(value: Vector2f): Vector2d = add(value.x, value.y)
operator fun Vector2d.plus(value: Vector2i): Vector2d = add(value.x.toDouble(), value.y.toDouble())
operator fun Vector2d.unaryMinus(): Vector2d = negate()
operator fun Vector2d.unaryPlus(): Vector2d = this

operator fun Vector2f.component1(): Float = this.x
operator fun Vector2f.component2(): Float = this.y
operator fun Vector2f.times(value: Double): Vector2f = mul(value)
operator fun Vector2f.times(value: Float): Vector2f = mul(value)
operator fun Vector2f.times(value: Int): Vector2f = mul(value.toDouble())
operator fun Vector2f.times(value: Vector2f): Vector2f = mul(value)
operator fun Vector2f.times(value: Vector2d): Vector2f = mul(value.x, value.y)
operator fun Vector2f.times(value: Vector2i): Vector2f = mul(value.x.toFloat(), value.y.toFloat())
operator fun Vector2f.minus(value: Vector2f): Vector2f = sub(value)
operator fun Vector2f.minus(value: Vector2d): Vector2f = sub(value.x, value.y)
operator fun Vector2f.minus(value: Vector2i): Vector2f = sub(value.x.toFloat(), value.y.toFloat())
operator fun Vector2f.plus(value: Vector2f): Vector2f = add(value)
operator fun Vector2f.plus(value: Vector2d): Vector2f = add(value.x, value.y)
operator fun Vector2f.plus(value: Vector2i): Vector2f = add(value.x.toFloat(), value.y.toFloat())
operator fun Vector2f.unaryMinus(): Vector2f = negate()
operator fun Vector2f.unaryPlus(): Vector2f = this

operator fun Vector2i.component1(): Int = this.x
operator fun Vector2i.component2(): Int = this.y
operator fun Vector2i.times(value: Int): Vector2i = mul(value)
operator fun Vector2i.times(value: Float): Vector2i = mul(value.toInt())
operator fun Vector2i.times(value: Double): Vector2i = mul(value)
operator fun Vector2i.times(value: Vector2i): Vector2i = mul(value)
operator fun Vector2i.times(value: Vector2d): Vector2i = mul(value.x, value.y)
operator fun Vector2i.times(value: Vector2f): Vector2i = mul(value.x.toDouble(), value.y.toDouble())
operator fun Vector2i.minus(value: Vector2i): Vector2i = sub(value)
operator fun Vector2i.minus(value: Vector2d): Vector2i = sub(value.x, value.y)
operator fun Vector2i.minus(value: Vector2f): Vector2i = sub(value.x.toDouble(), value.y.toDouble())
operator fun Vector2i.plus(value: Vector2i): Vector2i = add(value)
operator fun Vector2i.plus(value: Vector2d): Vector2i = add(value.x, value.y)
operator fun Vector2i.plus(value: Vector2f): Vector2i = add(value.x.toDouble(), value.y.toDouble())
operator fun Vector2i.unaryMinus(): Vector2i = negate()
operator fun Vector2i.unaryPlus(): Vector2i = this

operator fun Vector3d.component1(): Double = this.x
operator fun Vector3d.component2(): Double = this.y
operator fun Vector3d.component3(): Double = this.z
operator fun Vector3d.times(value: Double): Vector3d = mul(value)
operator fun Vector3d.times(value: Float): Vector3d = mul(value)
operator fun Vector3d.times(value: Int): Vector3d = mul(value.toDouble())
operator fun Vector3d.times(value: Vector3d): Vector3d = mul(value)
operator fun Vector3d.times(value: Vector3f): Vector3d = mul(value.x, value.y, value.z)
operator fun Vector3d.times(value: Vector3i): Vector3d = mul(value.x.toDouble(), value.y.toDouble(), value.z.toDouble())
operator fun Vector3d.minus(value: Vector3d): Vector3d = sub(value)
operator fun Vector3d.minus(value: Vector3f): Vector3d = sub(value.x, value.y, value.z)
operator fun Vector3d.minus(value: Vector3i): Vector3d = sub(value.x.toDouble(), value.y.toDouble(), value.z.toDouble())
operator fun Vector3d.plus(value: Vector3d): Vector3d = add(value)
operator fun Vector3d.plus(value: Vector3f): Vector3d = add(value.x, value.y, value.z)
operator fun Vector3d.plus(value: Vector3i): Vector3d = add(value.x.toDouble(), value.y.toDouble(), value.z.toDouble())
operator fun Vector3d.unaryMinus(): Vector3d = negate()
operator fun Vector3d.unaryPlus(): Vector3d = this

operator fun Vector3f.component1(): Float = this.x
operator fun Vector3f.component2(): Float = this.y
operator fun Vector3f.component3(): Float = this.z
operator fun Vector3f.times(value: Double): Vector3f = mul(value)
operator fun Vector3f.times(value: Float): Vector3f = mul(value)
operator fun Vector3f.times(value: Int): Vector3f = mul(value.toDouble())
operator fun Vector3f.times(value: Vector3f): Vector3f = mul(value)
operator fun Vector3f.times(value: Vector3d): Vector3f = mul(value.x, value.y, value.z)
operator fun Vector3f.times(value: Vector3i): Vector3f = mul(value.x.toFloat(), value.y.toFloat(), value.z.toFloat())
operator fun Vector3f.minus(value: Vector3f): Vector3f = sub(value)
operator fun Vector3f.minus(value: Vector3d): Vector3f = sub(value.x, value.y, value.z)
operator fun Vector3f.minus(value: Vector3i): Vector3f = sub(value.x.toFloat(), value.y.toFloat(), value.z.toFloat())
operator fun Vector3f.plus(value: Vector3f): Vector3f = add(value)
operator fun Vector3f.plus(value: Vector3d): Vector3f = add(value.x, value.y, value.z)
operator fun Vector3f.plus(value: Vector3i): Vector3f = add(value.x.toFloat(), value.y.toFloat(), value.z.toFloat())
operator fun Vector3f.unaryMinus(): Vector3f = negate()
operator fun Vector3f.unaryPlus(): Vector3f = this

operator fun Vector3i.component1(): Int = this.x
operator fun Vector3i.component2(): Int = this.y
operator fun Vector3i.component3(): Int = this.z
operator fun Vector3i.times(value: Int): Vector3i = mul(value)
operator fun Vector3i.times(value: Float): Vector3i = mul(value.toInt())
operator fun Vector3i.times(value: Double): Vector3i = mul(value)
operator fun Vector3i.times(value: Vector3i): Vector3i = mul(value)
operator fun Vector3i.times(value: Vector3d): Vector3i = mul(value.x, value.y, value.z)
operator fun Vector3i.times(value: Vector3f): Vector3i = mul(value.x.toDouble(), value.y.toDouble(), value.z.toDouble())
operator fun Vector3i.minus(value: Vector3i): Vector3i = sub(value)
operator fun Vector3i.minus(value: Vector3d): Vector3i = sub(value.x, value.y, value.z)
operator fun Vector3i.minus(value: Vector3f): Vector3i = sub(value.x.toDouble(), value.y.toDouble(), value.z.toDouble())
operator fun Vector3i.plus(value: Vector3i): Vector3i = add(value)
operator fun Vector3i.plus(value: Vector3d): Vector3i = add(value.x, value.y, value.z)
operator fun Vector3i.plus(value: Vector3f): Vector3i = add(value.x.toDouble(), value.y.toDouble(), value.z.toDouble())
operator fun Vector3i.unaryMinus(): Vector3i = negate()
operator fun Vector3i.unaryPlus(): Vector3i = this

operator fun Quaterniond.times(value: Vector3d): Vector3d = rotate(value)
operator fun Quaterniond.times(value: Vector3f): Vector3d = rotate(value.x, value.y, value.z)

operator fun Quaternionf.times(value: Vector3f): Vector3f = rotate(value)
operator fun Quaternionf.times(value: Vector3d): Vector3f = rotate(value.x, value.y, value.z)

fun Double.floorToInt(): Int =
        GenericMath.floor(this)

fun Double.ceilToInt(): Int =
        ceil(this).toInt()
