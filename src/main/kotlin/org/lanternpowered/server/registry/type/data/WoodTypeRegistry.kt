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

import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.data.type.LanternWoodType
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.data.type.WoodType

val WoodTypeRegistry = catalogTypeRegistry<WoodType> {
    fun register(id: String) = register(LanternWoodType(CatalogKey.minecraft(id), "tree.$id"))
    register("oak")
    register("spruce")
    register("birch")
    register("jungle")
    register("acacia")
    register("dark_oak")
}
