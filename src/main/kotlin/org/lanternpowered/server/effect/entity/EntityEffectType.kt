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
package org.lanternpowered.server.effect.entity

import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.api.registry.factoryOf
import org.spongepowered.api.CatalogType
import org.spongepowered.api.util.annotation.CatalogedBy

/**
 * Constructs a new [EntityEffectType].
 */
fun entityEffectTypeOf(key: NamespacedKey): EntityEffectType =
        factoryOf<EntityEffectType.Factory>().of(key)

/**
 * Represents a entity sound type, this is the context
 * where a [EntityEffect] will be played.
 */
@CatalogedBy(EntityEffectTypes::class)
interface EntityEffectType : CatalogType {

    interface Factory {

        fun of(key: NamespacedKey): EntityEffectType
    }
}
