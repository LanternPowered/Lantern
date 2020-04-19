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
import org.lanternpowered.server.catalog.AbstractNamedCatalogBuilder
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.text.translation.Translation
import org.spongepowered.api.world.gamerule.GameRule

@Suppress("UNCHECKED_CAST")
class LanternGameRuleBuilder<V : Any> : AbstractNamedCatalogBuilder<GameRule<V>, GameRule.Builder<V>>(), GameRule.Builder<V> {

    private var valueType: TypeToken<V>? = null
    private var defaultValue: V? = null

    override fun <NV : Any?> valueType(valueType: TypeToken<NV>) = apply {
        this.valueType = valueType as TypeToken<V>
    } as GameRule.Builder<NV>

    override fun defaultValue(defaultValue: V) = apply {
        check(this.valueType != null) { "The value type must be set before the default value" }
        this.defaultValue = defaultValue
    }

    override fun build(key: CatalogKey, name: Translation): GameRule<V> {
        val valueType = checkNotNull(this.valueType) { "The value type must be set." }
        val defaultValue = checkNotNull(this.defaultValue) { "The default value must be set." }
        return LanternGameRule(key, name, valueType, defaultValue)
    }
}
