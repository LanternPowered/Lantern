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
package org.lanternpowered.server.effect.potion

import org.lanternpowered.api.effect.potion.PotionEffect
import org.lanternpowered.api.effect.potion.PotionEffectType
import org.lanternpowered.server.data.DataQueries
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.Queries

internal data class LanternPotionEffect(
        private val effectType: PotionEffectType,
        private val duration: Int,
        private val amplifier: Int,
        private val ambient: Boolean,
        private val showsParticles: Boolean,
        private val showsIcon: Boolean
) : PotionEffect {

    override fun getType(): PotionEffectType = this.effectType
    override fun getDuration(): Int = this.duration
    override fun getAmplifier(): Int = this.amplifier
    override fun isAmbient(): Boolean = this.ambient
    override fun showsParticles(): Boolean = this.showsParticles
    override fun showsIcon(): Boolean = this.showsIcon

    override fun getContentVersion(): Int = 1
    override fun toContainer(): DataContainer {
        return DataContainer.createNew()
                .set(Queries.CONTENT_VERSION, contentVersion)
                .set(DataQueries.POTION_TYPE, this.effectType)
                .set(DataQueries.POTION_DURATION, this.duration)
                .set(DataQueries.POTION_AMPLIFIER, this.amplifier)
                .set(DataQueries.POTION_AMBIANCE, this.ambient)
                .set(DataQueries.POTION_SHOWS_PARTICLES, this.showsParticles)
                .set(DataQueries.POTION_SHOWS_ICON, this.showsIcon)
    }
}
