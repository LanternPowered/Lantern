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
package org.lanternpowered.server.world

import com.flowpowered.math.vector.Vector3d
import org.spongepowered.api.world.WorldBorder

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
