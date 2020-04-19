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
package org.lanternpowered.server.util

import org.lanternpowered.api.util.ToStringHelper
import org.spongepowered.api.util.Transform
import org.spongepowered.math.imaginary.Quaterniond
import org.spongepowered.math.matrix.Matrix4d
import org.spongepowered.math.vector.Vector3d

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
