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
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.effect.particle.ParticleOption
import org.spongepowered.api.effect.particle.ParticleType
import java.util.Optional

@Suppress("UNCHECKED_CAST")
class LanternParticleType(key: NamespacedKey, val internalType: Int?, options: Map<ParticleOption<*>, Any>) :
        DefaultCatalogType(key), ParticleType {

    private val options: Map<ParticleOption<*>, Any> = ImmutableMap.copyOf(options)

    override fun <V> getDefaultOption(option: ParticleOption<V>) = Optional.ofNullable(this.options[option] as V)
    override fun getDefaultOptions() = this.options

    override fun toStringHelper() = super.toStringHelper()
            .omitNullValues()
            .add("internalType", this.internalType)
}
