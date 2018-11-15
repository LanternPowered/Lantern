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
import com.flowpowered.math.vector.Vector3d
import org.lanternpowered.api.entity.Entity
import org.lanternpowered.api.entity.living.Living
import org.spongepowered.api.util.Direction

/**
 * Gets the [Direction] that the entity is looking.
 *
 * @param division The division
 * @return The direction
 */
fun Entity.getDirection(division: Direction.Division): Direction {
    return Direction.getClosest(getDirectionVector(), division)
}

fun Entity.getDirectionVector(): Vector3d {
    val rotation = if (this is Living) this.headRotation else this.rotation
    // Invert the x direction because west and east are swapped
    val vector = rotation.mul(1.0, -1.0, 1.0)
    return Quaterniond.fromAxesAnglesDeg(vector.x, vector.y, vector.z).direction
}

fun Entity.getHorizontalDirectionVector(): Vector3d {
    val rotation = if (this is Living) this.headRotation else this.rotation
    val vector = rotation.mul(0.0, 1.0, 0.0)
    return Quaterniond.fromAxesAnglesDeg(vector.x, vector.y, vector.z).direction.mul(-1f, 1f, 1f)
}

/**
 * Gets the [Direction] that the entity is looking in the horizontal plane.
 *
 * @param division The division
 * @return The direction
 */
fun Entity.getHorizontalDirection(division: Direction.Division): Direction {
    return Direction.getClosest(getHorizontalDirectionVector(), division)
}
