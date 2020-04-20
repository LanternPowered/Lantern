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
package org.lanternpowered.server.registry.type.advancement

import org.lanternpowered.api.text.format.TextColor
import org.lanternpowered.api.text.format.TextColors
import org.lanternpowered.api.text.format.TextFormat
import org.lanternpowered.server.advancement.LanternAdvancementType
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.advancement.AdvancementType
import java.util.function.Supplier

val AdvancementTypeRegistry = internalCatalogTypeRegistry<AdvancementType> {
    fun register(name: String, color: Supplier<out TextColor>) =
            register(LanternAdvancementType(CatalogKey.minecraft(name), TextFormat.of(color.get())))
    register("task", TextColors.YELLOW)
    register("challenge", TextColors.LIGHT_PURPLE)
    register("goal", TextColors.YELLOW)
}
