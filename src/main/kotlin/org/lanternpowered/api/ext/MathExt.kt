/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.api.ext

import com.flowpowered.math.imaginary.Quaterniond
import com.flowpowered.math.imaginary.Quaternionf
import com.flowpowered.math.vector.Vector2d
import com.flowpowered.math.vector.Vector2f
import com.flowpowered.math.vector.Vector2i
import com.flowpowered.math.vector.Vector3d
import com.flowpowered.math.vector.Vector3f
import com.flowpowered.math.vector.Vector3i
import org.lanternpowered.api.world.BlockVector

operator fun Vector2d.component1(): Double = x
operator fun Vector2d.component2(): Double = y
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

operator fun Vector2f.component1(): Float = x
operator fun Vector2f.component2(): Float = y
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

operator fun Vector2i.component1(): Int = x
operator fun Vector2i.component2(): Int = y
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

operator fun Vector3d.component1(): Double = x
operator fun Vector3d.component2(): Double = y
operator fun Vector3d.component3(): Double = z
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
fun Vector3d.toBlockVector() = BlockVector(this.floorX, this.floorY, this.floorZ)

operator fun Vector3f.component1(): Float = x
operator fun Vector3f.component2(): Float = y
operator fun Vector3f.component3(): Float = z
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
fun Vector3f.toBlockVector() = BlockVector(this.floorX, this.floorY, this.floorZ)

operator fun Vector3i.component1(): Int = x
operator fun Vector3i.component2(): Int = y
operator fun Vector3i.component3(): Int = z
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
fun Vector3i.toBlockVector() = BlockVector(this.x, this.y, this.z)

operator fun Quaterniond.times(value: Vector3d): Vector3d = rotate(value)
operator fun Quaterniond.times(value: Vector3f): Vector3d = rotate(value.x, value.y, value.z)

operator fun Quaternionf.times(value: Vector3f): Vector3f = rotate(value)
operator fun Quaternionf.times(value: Vector3d): Vector3f = rotate(value.x, value.y, value.z)
