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
package org.lanternpowered.api.ext

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.item.potion.PotionType
import org.lanternpowered.api.item.potion.PotionTypeBuilder
import org.lanternpowered.api.registry.builderOf
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Constructs a new [PotionType] with the given [CatalogKey] and
 * possibility to apply other data using the function.
 */
inline fun potionTypeOf(key: CatalogKey, fn: PotionTypeBuilder.() -> Unit = {}): PotionType {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    return builderOf<PotionTypeBuilder>().key(key).apply(fn).build()
}
