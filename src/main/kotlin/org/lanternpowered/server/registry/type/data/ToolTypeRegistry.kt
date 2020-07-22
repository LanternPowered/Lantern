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

import org.lanternpowered.api.namespace.minecraftKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.data.type.LanternToolType
import org.spongepowered.api.data.type.ToolType

val ToolTypeRegistry = catalogTypeRegistry<ToolType> {
    fun register(id: String) =
            register(LanternToolType(minecraftKey(id)))

    register("diamond")
    register("gold")
    register("iron")
    register("stone")
    register("wood")
}
