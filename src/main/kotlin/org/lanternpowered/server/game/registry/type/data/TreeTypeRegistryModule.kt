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
package org.lanternpowered.server.game.registry.type.data

import org.lanternpowered.server.data.type.LanternTreeType
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule
import org.spongepowered.api.CatalogKey

object TreeTypeRegistryModule : DefaultCatalogRegistryModule<LanternTreeType>() {

    override fun registerDefaults() {
        fun register(id: String) = register(LanternTreeType(CatalogKey.minecraft(id), "tree.$id"))

        register("oak")
        register("spruce")
        register("birch")
        register("jungle")
        register("acacia")
        register("dark_oak")
    }
}
