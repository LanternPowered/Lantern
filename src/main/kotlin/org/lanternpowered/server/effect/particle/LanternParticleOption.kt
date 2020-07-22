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

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.effect.particle.ParticleOption

class LanternParticleOption<V> @JvmOverloads constructor(
        key: NamespacedKey, private val valueType: Class<V>,
        internal val valueValidator: (V) -> Unit = {}
) : DefaultCatalogType(key), ParticleOption<V> {

    override fun getValueType() = this.valueType
    override fun toStringHelper() = super.toStringHelper()
            .add("valueType", this.valueType)
}
