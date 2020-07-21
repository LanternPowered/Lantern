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
package org.lanternpowered.api.cause.entity.health

import org.lanternpowered.api.registry.CatalogRegistry
import org.lanternpowered.api.registry.provide
import java.util.function.Supplier

object HealingTypes {

    @JvmField val BOSS: Supplier<HealingType> = CatalogRegistry.provide("BOSS")
    @JvmField val FOOD: Supplier<HealingType> = CatalogRegistry.provide("FOOD")
    @JvmField val PLUGIN: Supplier<HealingType> = CatalogRegistry.provide("PLUGIN")
    @JvmField val POTION: Supplier<HealingType> = CatalogRegistry.provide("POTION")
    @JvmField val UNDEAD: Supplier<HealingType> = CatalogRegistry.provide("UNDEAD")
    @JvmField val MAGIC: Supplier<HealingType> = CatalogRegistry.provide("MAGIC")
    @JvmField val GENERIC: Supplier<HealingType> = CatalogRegistry.provide("GENERIC")
}
