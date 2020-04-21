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

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.spongepowered.api.data.type.HorseStyle

val HorseStyleRegistry = internalCatalogTypeRegistry<HorseStyle> {
    fun register(id: String) =
            register(LanternHorseStyle(CatalogKey.minecraft(id)))

    register("none")
    register("white")
    register("whitefield")
    register("white_dots")
    register("black_dots")
}

private class LanternHorseStyle(key: CatalogKey) : DefaultCatalogType(key), HorseStyle
