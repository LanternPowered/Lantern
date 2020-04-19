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
package org.lanternpowered.server.effect.particle

import com.google.common.collect.ImmutableMap
import org.lanternpowered.server.data.DataQueries
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.effect.particle.ParticleEffect
import org.spongepowered.api.effect.particle.ParticleOption
import java.util.Optional

@Suppress("UNCHECKED_CAST")
class LanternParticleEffect internal constructor(private val type: LanternParticleType, options: Map<ParticleOption<*>, Any>) : ParticleEffect {

    private val options: Map<ParticleOption<*>, Any> = ImmutableMap.copyOf(options)

    override fun getType(): LanternParticleType = this.type
    override fun <V> getOption(option: ParticleOption<V>): Optional<V> = Optional.ofNullable(this.options[option] as V)
    override fun getOptions(): Map<ParticleOption<*>, Any> = this.options

    override fun getContentVersion(): Int = 1
    override fun toContainer(): DataContainer {
        val dataContainer = DataContainer.createNew()
        dataContainer.set(DataQueries.PARTICLE_TYPE, this.type)
        dataContainer.set(DataQueries.PARTICLE_OPTIONS, this.options
                .map { entry -> DataContainer.createNew()
                        .set(DataQueries.PARTICLE_OPTION_KEY, entry.key)
                        .set(DataQueries.PARTICLE_OPTION_VALUE, entry.value)
                })
        return dataContainer
    }
}
