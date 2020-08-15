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
@file:JvmName("AdvancementTypeRegistry")
package org.lanternpowered.server.registry.type.advancement

import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.api.text.format.NamedTextColor
import org.lanternpowered.api.text.format.textStyleOf
import org.lanternpowered.server.advancement.LanternAdvancementType
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.spongepowered.api.advancement.AdvancementType

@get:JvmName("get")
val AdvancementTypeRegistry = internalCatalogTypeRegistry<AdvancementType> {
    fun register(id: String, color: NamedTextColor) =
            register(LanternAdvancementType(minecraftKey(id), textStyleOf(color)))

    register("task", NamedTextColor.YELLOW)
    register("challenge", NamedTextColor.LIGHT_PURPLE)
    register("goal", NamedTextColor.YELLOW)
}
