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

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.api.text.TextRepresentable
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.data.type.WoodType

val WoodTypeRegistry = catalogTypeRegistry<WoodType> {
    fun register(id: String) =
            register(LanternWoodType(minecraftKey(id), "tree.$id"))

    register("oak")
    register("spruce")
    register("birch")
    register("jungle")
    register("acacia")
    register("dark_oak")
}

private class LanternWoodType(key: NamespacedKey, translationKey: String) :
        DefaultCatalogType(key), TextRepresentable by translatableTextOf(translationKey), WoodType
