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
import org.lanternpowered.api.effect.potion.PotionEffectBuilder
import org.lanternpowered.api.effect.potion.PotionEffectType
import org.lanternpowered.api.ext.*
import org.lanternpowered.server.data.DataQueries
import org.spongepowered.api.data.persistence.AbstractDataBuilder
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.persistence.InvalidDataException
import java.util.Optional

class LanternPotionEffectBuilder : AbstractDataBuilder<PotionEffect>(PotionEffect::class.java, 1), PotionEffectBuilder {

    private var potionType: PotionEffectType? = null
    private var duration: Int = 0
    private var amplifier: Int = 0
    private var isAmbient: Boolean = true
    private var showParticles: Boolean = true

    override fun duration(duration: Int): PotionEffectBuilder = apply {
        check(duration > 0) { "Duration $duration must be greater than 0" }
        this.duration = duration
    }

    override fun potionType(potionEffectType: PotionEffectType): PotionEffectBuilder = apply { this.potionType = potionEffectType }
    override fun amplifier(amplifier: Int): PotionEffectBuilder = apply { this.amplifier = amplifier }
    // Sponge, here is a typo
    override fun ambience(ambience: Boolean): PotionEffectBuilder = apply { this.isAmbient = ambience }
    override fun particles(showsParticles: Boolean): PotionEffectBuilder = apply { this.showParticles = showsParticles }

    override fun build(): PotionEffect {
        val potionType = checkNotNull(this.potionType) { "Potion type must be set" }
        check(this.duration > 0) { "Duration must be set" }
        return LanternPotionEffect(potionType, this.duration, this.amplifier, this.isAmbient, this.showParticles)
    }

    override fun from(holder: PotionEffect): PotionEffectBuilder = apply {
        this.potionType = holder.type
        this.duration = holder.duration
        this.amplifier = holder.amplifier
        this.isAmbient = holder.isAmbient
        this.showParticles = holder.showParticles
    }

    override fun reset(): PotionEffectBuilder = apply {
        this.potionType = null
        this.amplifier = 0
        this.duration = 1
        this.isAmbient = true
        this.showParticles = true
    }

    @Throws(InvalidDataException::class)
    override fun buildContent(container: DataView): Optional<PotionEffect> {
        if (!container.contains(DataQueries.POTION_TYPE) || !container.contains(DataQueries.POTION_DURATION)
                || !container.contains(DataQueries.POTION_AMPLIFIER) || !container.contains(DataQueries.POTION_AMBIANCE)
                || !container.contains(DataQueries.POTION_SHOWS_PARTICLES)) {
            return Optional.empty()
        }
        val typeId = container.getString(DataQueries.POTION_TYPE).get()
        val type = catalogOf<PotionEffectType>(typeId) ?: throw InvalidDataException("The container has an invalid potion type name: $typeId")
        val duration = container.getInt(DataQueries.POTION_DURATION).get()
        val amplifier = container.getInt(DataQueries.POTION_AMPLIFIER).get()
        val ambient = container.getBoolean(DataQueries.POTION_AMBIANCE).get()
        val showParticles = container.getBoolean(DataQueries.POTION_SHOWS_PARTICLES).get()
        return Optional.of(LanternPotionEffect(type, duration, amplifier, ambient, showParticles))
    }

}
