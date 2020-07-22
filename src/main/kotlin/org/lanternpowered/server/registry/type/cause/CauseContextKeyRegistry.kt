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
package org.lanternpowered.server.registry.type.cause

import org.lanternpowered.api.cause.CauseContextKey
import org.lanternpowered.api.cause.CauseContextKeys
import org.lanternpowered.api.namespace.spongeKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.event.LanternCauseContextKey

val CauseContextKeyRegistry = catalogTypeRegistry<CauseContextKey<*>> {
    // TODO: Process LanternEventContextKeys and ContextKeys
    val elementTypeParameter = CauseContextKey::class.java.typeParameters[0]
    processSuggestions(CauseContextKeys::class) { suggestedId, type ->
        val elementType = type.resolveType(elementTypeParameter)
        register(LanternCauseContextKey(spongeKey(suggestedId), elementType))
    }
}
