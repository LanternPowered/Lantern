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

import com.google.common.collect.ImmutableList
import org.lanternpowered.server.data.DataQueries
import org.spongepowered.api.data.persistence.AbstractDataBuilder
import org.spongepowered.api.data.persistence.DataSerializable
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.persistence.InvalidDataException
import org.spongepowered.api.effect.particle.ParticleEffect
import org.spongepowered.api.effect.particle.ParticleOption
import org.spongepowered.api.effect.particle.ParticleType
import java.util.HashMap
import java.util.Optional

@Suppress("UNCHECKED_CAST")
class LanternParticleEffectBuilder : AbstractDataBuilder<ParticleEffect>(ParticleEffect::class.java, 1), ParticleEffect.Builder {

    private var type: LanternParticleType? = null
    private val options: MutableMap<ParticleOption<*>, Any> = HashMap()

    init {
        reset()
    }

    @Throws(InvalidDataException::class)
    override fun buildContent(container: DataView): Optional<ParticleEffect> {
        if (!container.contains(DataQueries.PARTICLE_TYPE, DataQueries.PARTICLE_OPTIONS)) {
            return Optional.empty()
        }
        val particleType = container.getCatalogType(DataQueries.PARTICLE_TYPE, ParticleType::class.java).get()
        val options = HashMap<ParticleOption<*>, Any>()
        container.getViewList(DataQueries.PARTICLE_OPTIONS).get().forEach { view ->
            val option = view.getCatalogType(DataQueries.PARTICLE_OPTION_KEY, ParticleOption::class.java).get()
            val value = if (option.valueType.isAssignableFrom(DataSerializable::class.java)) {
                view.getSerializable<DataSerializable>(DataQueries.PARTICLE_OPTION_VALUE, option.valueType as Class<DataSerializable>).get()
            } else {
                view.getObject(DataQueries.PARTICLE_OPTION_VALUE, option.valueType).get()
            }
            options[option] = value
        }
        return Optional.of(LanternParticleEffect(particleType as LanternParticleType, options))
    }

    override fun from(particleEffect: ParticleEffect): ParticleEffect.Builder = apply {
        this.type = particleEffect.type as LanternParticleType
        this.options.clear()
        this.options.putAll(particleEffect.options)
    }

    override fun type(particleType: ParticleType): ParticleEffect.Builder = apply {
        this.type = particleType as LanternParticleType
    }

    override fun reset(): ParticleEffect.Builder = apply {
        this.type = null
        this.options.clear()
    }

    @Throws(IllegalArgumentException::class)
    override fun <V> option(option: ParticleOption<V>, value: V): ParticleEffect.Builder = apply {
        val exception = (option as LanternParticleOption<V>).valueValidator.invoke(value)
        if (exception != null) {
            throw exception
        }
        var actualValue: Any = value as Any
        if (actualValue is List<*>) {
            actualValue = ImmutableList.copyOf(value as List<*>)
        }
        this.options[option] = actualValue
    }

    override fun build(): ParticleEffect {
        val type: LanternParticleType = checkNotNull(this.type) { "ParticleType must be set" }
        return LanternParticleEffect(type, this.options)
    }
}
