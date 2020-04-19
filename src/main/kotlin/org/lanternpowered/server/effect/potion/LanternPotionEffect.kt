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
