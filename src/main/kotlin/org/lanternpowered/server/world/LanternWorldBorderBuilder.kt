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
package org.lanternpowered.server.world

import org.spongepowered.api.world.WorldBorder
import org.spongepowered.math.vector.Vector3d
import java.time.Duration

class LanternWorldBorderBuilder : WorldBorder.Builder {

    private var center = Vector3d.ZERO
    private var warningTime = Duration.ofSeconds(0)
    private var diameter = 0.0
    private var warningDistance = 0.0
    private var damageThreshold = 0.0
    private var damageAmount = 0.0

    override fun from(border: WorldBorder) = apply {
        this.diameter = border.diameter
        this.center = border.center
        this.warningTime = border.warningTime
        this.warningDistance = border.warningDistance
        this.damageThreshold = border.damageThreshold
        this.damageAmount = border.damageAmount
    }

    override fun diameter(diameter: Double) = apply { this.diameter = diameter }
    override fun center(x: Double, z: Double) = apply { this.center = Vector3d(x, 0.0, z) }
    override fun warningTime(time: Duration) = apply { this.warningTime = time }
    override fun warningDistance(distance: Double) = apply { this.warningDistance = distance }
    override fun damageThreshold(distance: Double) = apply { this.damageThreshold = distance }
    override fun damageAmount(damage: Double) = apply { this.damageAmount = damage }

    override fun build(): WorldBorder {
        val border = LanternWorldBorder()
        border.setCenter(this.center.x, this.center.z)
        border.damageAmount = this.damageAmount
        border.setDamageThreshold(this.damageThreshold)
        border.diameter = this.diameter
        border.setWarningDistance(this.warningDistance)
        border.setWarningTime(this.warningTime)
        return border
    }

    override fun reset() = apply {
        this.center = Vector3d.ZERO
        this.damageAmount = 0.0
        this.damageThreshold = 0.0
        this.diameter = 0.0
        this.warningDistance = 0.0
        this.warningTime = Duration.ofSeconds(0)
    }
}
