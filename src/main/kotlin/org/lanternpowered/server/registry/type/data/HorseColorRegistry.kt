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

import org.lanternpowered.server.data.type.LanternHorseColor
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.data.type.HorseColor

val HorseColorRegistry = internalCatalogTypeRegistry<HorseColor> {
    fun register(id: String) =
            register(LanternHorseColor(CatalogKey.minecraft(id)))

    register("white")
    register("creamy")
    register("chestnut")
    register("brown")
    register("black")
    register("gray")
    register("dark_brown")
}
