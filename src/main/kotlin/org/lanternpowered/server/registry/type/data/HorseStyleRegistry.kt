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

import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.api.namespace.minecraftKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.spongepowered.api.data.type.HorseStyle

val HorseStyleRegistry = internalCatalogTypeRegistry<HorseStyle> {
    fun register(id: String) =
            register(LanternHorseStyle(minecraftKey(id)))

    register("none")
    register("white")
    register("whitefield")
    register("white_dots")
    register("black_dots")
}

private class LanternHorseStyle(key: NamespacedKey) : DefaultCatalogType(key), HorseStyle
