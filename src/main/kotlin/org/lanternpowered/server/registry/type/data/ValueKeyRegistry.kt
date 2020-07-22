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
import org.lanternpowered.api.data.valueKeyOf
import org.lanternpowered.api.namespace.minecraftKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.api.util.type.TypeToken
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.data.Keys
import org.spongepowered.api.data.value.Value

val ValueKeyRegistry = catalogTypeRegistry<Key<*>> {
    val valueTypeParameter = Key::class.java.typeParameters[0]
    processSuggestions(Keys::class) { suggestedId, type ->
        val key = minecraftKey(suggestedId)
        val valueType = type.resolveType(valueTypeParameter).uncheckedCast<TypeToken<Value<Any>>>()
        register(valueKeyOf(key, valueType))
    }
}
