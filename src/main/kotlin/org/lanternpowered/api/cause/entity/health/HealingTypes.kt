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
import org.lanternpowered.api.registry.provideSupplier
import java.util.function.Supplier

object HealingTypes {

    @JvmField val BOSS: Supplier<HealingType> = CatalogRegistry.provideSupplier("BOSS")
    @JvmField val FOOD: Supplier<HealingType> = CatalogRegistry.provideSupplier("FOOD")
    @JvmField val PLUGIN: Supplier<HealingType> = CatalogRegistry.provideSupplier("PLUGIN")
    @JvmField val POTION: Supplier<HealingType> = CatalogRegistry.provideSupplier("POTION")
    @JvmField val UNDEAD: Supplier<HealingType> = CatalogRegistry.provideSupplier("UNDEAD")
    @JvmField val MAGIC: Supplier<HealingType> = CatalogRegistry.provideSupplier("MAGIC")
    @JvmField val GENERIC: Supplier<HealingType> = CatalogRegistry.provideSupplier("GENERIC")
}
