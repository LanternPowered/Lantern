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
package org.lanternpowered.server.registry.type.effect.entity

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.effect.entity.EntityEffectType
import org.lanternpowered.server.effect.entity.EntityEffectTypes
import org.lanternpowered.server.effect.entity.entityEffectTypeOf

val EntityEffectTypeRegistry = catalogTypeRegistry<EntityEffectType> {
    processSuggestions(EntityEffectTypes::class) { suggestedId, _ ->
        register(entityEffectTypeOf(NamespacedKey.minecraft(suggestedId)))
    }
}
