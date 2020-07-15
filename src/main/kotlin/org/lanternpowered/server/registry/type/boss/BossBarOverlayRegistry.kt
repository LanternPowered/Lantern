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

import org.lanternpowered.api.boss.BossBarOverlay
import org.lanternpowered.api.ResourceKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.registry.internalCatalogTypeRegistry

val BossBarOverlayRegistry = internalCatalogTypeRegistry<BossBarOverlay> {
    fun register(name: String) =
            register(LanternBossBarOverlay(ResourceKey.minecraft(name)))

    register("progress")
    register("notched_6")
    register("notched_10")
    register("notched_12")
    register("notched_20")
}

private class LanternBossBarOverlay(key: ResourceKey) : DefaultCatalogType(key), BossBarOverlay
