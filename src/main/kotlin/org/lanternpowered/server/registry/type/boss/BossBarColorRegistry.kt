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
package org.lanternpowered.server.registry.type.boss

import org.lanternpowered.api.boss.BossBarColor
import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.text.format.TextColor
import org.lanternpowered.api.text.format.TextColors
import org.lanternpowered.server.boss.LanternBossBarColor
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import java.util.function.Supplier

val BossBarColorRegistry = internalCatalogTypeRegistry<BossBarColor> {
    fun register(name: String, color: Supplier<out TextColor>) =
            register(LanternBossBarColor(CatalogKey.minecraft(name), color.get()))

    register("pink", TextColors.LIGHT_PURPLE)
    register("blue", TextColors.BLUE)
    register("red", TextColors.RED)
    register("green", TextColors.GREEN)
    register("yellow", TextColors.YELLOW)
    register("purple", TextColors.DARK_PURPLE)
    register("white", TextColors.WHITE)
}