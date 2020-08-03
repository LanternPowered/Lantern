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
package org.lanternpowered.api.util.math.quaternions

import org.spongepowered.math.imaginary.Quaterniond
import org.spongepowered.math.imaginary.Quaternionf
import org.spongepowered.math.vector.Vector3d
import org.spongepowered.math.vector.Vector3f

/**
 * Converts the euler angles (pitch, yaw, roll) to a quaternion.
 */
fun Vector3d.eulerAnglesToQuaternion(): Quaterniond = Quaterniond.fromAxesAnglesDeg(this.x, this.y, this.z)

/**
 * Converts the euler angles (pitch, yaw, roll) to a quaternion.
 */
fun Vector3f.eulerAnglesToQuaternion(): Quaternionf = Quaternionf.fromAxesAnglesDeg(this.x, this.y, this.z)

/**
 * Converts the quaternion to euler angles (pitch, yaw, roll).
 */
fun Quaterniond.toEuler(): Vector3d = this.axesAnglesDeg

/**
 * Converts the quaternion to euler angles (pitch, yaw, roll).
 */
fun Quaternionf.toEuler(): Vector3f = this.axesAnglesDeg
