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
package org.lanternpowered.server.world.gamerule

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.ResourceKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.world.gamerule.GameRule

class LanternGameRule<V>(
        key: ResourceKey, name: String, private val valueType: TypeToken<V>, private val defaultValue: V
) : DefaultCatalogType.Named(key, name), GameRule<V> {

    override fun getValueType() = this.valueType
    override fun getDefaultValue() = this.defaultValue

    override fun toStringHelper() = super.toStringHelper()
            .add("valueType", this.valueType)
            .add("defaultValue", this.defaultValue)
}
