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

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.boss.LanternBossBarOverlay
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule
import org.spongepowered.api.boss.BossBarOverlay
import org.spongepowered.api.boss.BossBarOverlays

class BossBarOverlayRegistryModule : DefaultCatalogRegistryModule<BossBarOverlay>(BossBarOverlays::class) {

    override fun registerDefaults() {
        val register = { id: String, internalId: Int -> register(LanternBossBarOverlay(CatalogKey.minecraft(id), internalId)) }
        register("progress", 0)
        register("notched_6", 1)
        register("notched_10", 2)
        register("notched_12", 3)
        register("notched_20", 4)
    }
}
