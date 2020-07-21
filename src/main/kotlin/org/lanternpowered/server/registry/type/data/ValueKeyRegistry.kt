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

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.api.data.Key
import org.lanternpowered.api.data.valueKeyOf
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.api.util.type.TypeToken
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.data.Keys
import org.spongepowered.api.data.value.Value

val ValueKeyRegistry = catalogTypeRegistry<Key<*>> {
    processSuggestions(Keys::class) { suggestedId, type ->
        val key = ResourceKey.minecraft(suggestedId)
        val valueType = type.uncheckedCast<TypeToken<Value<Any>>>()
        register(valueKeyOf(key, valueType))
    }
}
