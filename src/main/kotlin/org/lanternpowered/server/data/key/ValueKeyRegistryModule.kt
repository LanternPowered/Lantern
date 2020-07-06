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
package org.lanternpowered.server.data.key

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.catalog.CatalogKeys.sponge
import org.lanternpowered.api.data.valueKeyOf
import org.lanternpowered.api.util.ranges.rangeTo
import org.lanternpowered.api.util.type.typeToken
import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.Keys
import org.spongepowered.api.data.value.BoundedValue
import org.spongepowered.api.data.value.Value

object ValueKeyRegistryModule : AdditionalPluginCatalogRegistryModule<Key<*>>(Keys::class) {

    override fun <A : Key<*>> register(catalogType: A): A {
        return super.register(catalogType)
    }

    override fun registerDefaults() {
        register(valueKeyOf<BoundedValue<Double>>(sponge("absorption")) { range(0..Double.MAX_VALUE) })

        val valueTypeParameter = Key::class.java.typeParameters[0]
        for (field in Keys::class.java.fields) {
            val catalogKey = sponge(field.name.toLowerCase())
            // Already registered manually
            if (get(catalogKey).isPresent) {
                continue
            }
            val valueToken = field.genericType.typeToken.resolveType(valueTypeParameter)
            register(valueKeyOf(catalogKey, valueToken.uncheckedCast<TypeToken<Value<Any>>>()))
        }

        for (field in LanternKeys::class.java.fields) {
            register(field.get(null).uncheckedCast())
        }
    }
}
