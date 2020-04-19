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
package org.lanternpowered.server.game.registry.type.bossbar

import org.lanternpowered.api.boss.BossBarColor
import org.lanternpowered.api.boss.BossBarColors
import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.boss.LanternBossBarColor
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule

class BossBarColorRegistryModule : DefaultCatalogRegistryModule<BossBarColor>(BossBarColors::class) {

    override fun registerDefaults() {
        val register = { id: String, internalId: Int -> register(LanternBossBarColor(CatalogKey.minecraft(id), internalId)) }
        register("pink", 0)
        register("blue", 1)
        register("red", 2)
        register("green", 3)
        register("yellow", 4)
        register("purple", 5)
        register("white", 6)
    }
}
