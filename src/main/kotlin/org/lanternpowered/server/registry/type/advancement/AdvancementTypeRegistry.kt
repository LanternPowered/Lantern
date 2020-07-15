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

import org.lanternpowered.api.text.format.TextColor
import org.lanternpowered.api.text.format.TextColors
import org.lanternpowered.api.text.format.TextFormat
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.advancement.AdvancementType
import java.util.function.Supplier

@get:JvmName("get")
val AdvancementTypeRegistry = internalCatalogTypeRegistry<AdvancementType> {
    fun register(id: String, color: Supplier<out TextColor>) =
            register(LanternAdvancementType(ResourceKey.minecraft(id), TextFormat.of(color.get())))

    register("task", TextColors.YELLOW)
    register("challenge", TextColors.LIGHT_PURPLE)
    register("goal", TextColors.YELLOW)
}

private class LanternAdvancementType(key: ResourceKey, private val textFormat: TextFormat) : DefaultCatalogType(key), AdvancementType {
    override fun getTextFormat(): TextFormat = this.textFormat
}
