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
package org.lanternpowered.server.registry.type.data

import org.lanternpowered.api.data.Key
import org.lanternpowered.api.data.Keys
import org.lanternpowered.api.data.valueKeyOf
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.spongeKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.api.util.type.TypeToken
import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.server.data.SpongeKeys
import org.spongepowered.api.data.value.Value
import kotlin.reflect.full.memberProperties

val ValueKeyRegistry = catalogTypeRegistry<Key<*>> {
    val valueTypeParameter = Key::class.java.typeParameters[0]
    val found = hashSetOf<NamespacedKey>()
    for (field in Keys::class.memberProperties) {
        val valueKey = field.get(Keys) as Key<*>
        found += valueKey.key
        register(valueKey)
    }
    processSuggestions(SpongeKeys::class) { suggestedId, type ->
        val key = spongeKey(suggestedId)
        // Don't register the keys twice
        if (key in found)
            return@processSuggestions
        val valueType = type.resolveType(valueTypeParameter).uncheckedCast<TypeToken<Value<Any>>>()
        register(valueKeyOf(key, valueType))
    }
}
