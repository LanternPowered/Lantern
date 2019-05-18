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
package org.lanternpowered.server.util

import com.flowpowered.math.imaginary.Quaterniond
import com.flowpowered.math.matrix.Matrix4d
import com.flowpowered.math.vector.Vector3d
import org.lanternpowered.api.util.ToStringHelper
import org.spongepowered.api.util.Transform

class LanternTransform @JvmOverloads constructor(
        private val position: Vector3d,
        private val rotation: Vector3d = Vector3d.ZERO,
        private val scale: Vector3d = Vector3d.ONE
) : Transform {

    private val rotationQuaternion by lazy { fromAxesAngles(this.rotation) }

    override fun getPosition() = this.position
    override fun getRotation() = this.rotation
    override fun getRotationAsQuaternion() = this.rotationQuaternion
    override fun getScale() = this.scale

    override fun getPitch() = this.rotation.x
    override fun getYaw() = this.rotation.y
    override fun getRoll() = this.rotation.z

    override fun withPosition(position: Vector3d) = LanternTransform(position, this.rotation, this.scale)
    override fun withRotation(rotation: Quaterniond) = withRotation(toAxesAngles(rotation))
    override fun withRotation(rotation: Vector3d) = LanternTransform(this.position, rotation, this.scale)
    override fun withScale(scale: Vector3d) = LanternTransform(this.position, this.rotation, scale)

    override fun add(other: Transform): Transform {
        val position = this.position.add(other.position)
        val rotation = toAxesAngles(other.rotationAsQuaternion.mul(this.rotationAsQuaternion))
        val scale = this.scale.mul(other.scale)
        return LanternTransform(position, rotation, scale)
    }

    override fun translate(translation: Vector3d) = withPosition(this.position.add(translation))
    override fun rotate(rotation: Vector3d) = rotate(fromAxesAngles(rotation))
    override fun rotate(rotation: Quaterniond) = withRotation(toAxesAngles(rotation.mul(this.rotationAsQuaternion)))
    override fun scale(scale: Vector3d) = withScale(this.scale.mul(scale))

    override fun toMatrix(): Matrix4d = Matrix4d.createScaling(this.scale.toVector4(1f))
            .rotate(this.rotationAsQuaternion).translate(this.position)

    override fun hashCode(): Int {
        var result = this.position.hashCode()
        result = 31 * result + this.rotation.hashCode()
        result = 31 * result + this.scale.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is Transform) {
            return false
        }
        return other.position == this.position && other.rotation == this.rotation && other.scale == this.scale
    }

    private fun toAxesAngles(quaternion: Quaterniond): Vector3d {
        val axesAngles = quaternion.axesAnglesDeg
        return Vector3d(axesAngles.x, -axesAngles.y, axesAngles.z)
    }

    private fun fromAxesAngles(angles: Vector3d): Quaterniond = Quaterniond.fromAxesAnglesDeg(angles.x, -angles.y, angles.z)

    override fun toString() = ToStringHelper("Transform")
            .add("position", this.position)
            .add("rotation", this.rotation)
            .add("scale", this.scale)
            .toString()

    object Factory : Transform.Factory {

        override fun create(position: Vector3d, rotation: Vector3d, scale: Vector3d)
                = LanternTransform(position, rotation, scale)
    }
}
