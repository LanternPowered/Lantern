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
