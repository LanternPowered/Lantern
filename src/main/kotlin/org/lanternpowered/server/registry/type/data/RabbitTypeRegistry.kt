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
import org.lanternpowered.server.data.type.LanternRabbitType
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.spongepowered.api.data.type.RabbitType

val RabbitTypeRegistry = internalCatalogTypeRegistry<RabbitType> {
    fun register(id: String, internalId: Int = -1) {
        val type = LanternRabbitType(CatalogKey.minecraft(id))
        if (internalId == -1) {
            register(type)
        } else {
            register(internalId, type)
        }
    }

    register("brown")
    register("white")
    register("black")
    register("black_and_white")
    register("gold")
    register("salt_and_pepper")
    register("killer", 99)
}
