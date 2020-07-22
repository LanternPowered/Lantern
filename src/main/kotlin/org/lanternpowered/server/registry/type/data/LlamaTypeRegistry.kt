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

import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.minecraftKey
import org.spongepowered.api.data.type.LlamaType

val LlamaTypeRegistry = internalCatalogTypeRegistry<LlamaType> {
    fun register(id: String) =
            register(LanternLlamaType(minecraftKey(id)))

    register("creamy")
    register("white")
    register("brown")
    register("gray")
}

private class LanternLlamaType(key: NamespacedKey) : DefaultCatalogType(key), LlamaType
