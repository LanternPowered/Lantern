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
package org.lanternpowered.server.registry.type.scoreboard

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.registry.customInternalCatalogTypeRegistry
import org.spongepowered.api.scoreboard.criteria.Criterion

val CriterionRegistry = customInternalCatalogTypeRegistry<Criterion, String> {
    fun register(internalId: String, id: String) =
            register(internalId, LanternCriterion(CatalogKey.minecraft(id)))

    register("dummy", "dummy")
    register("trigger", "trigger")
    register("health", "health")
    register("playerKillCount", "player_kills")
    register("totalKillCount", "total_kills")
    register("deathCount", "deaths")
}

private class LanternCriterion(key: CatalogKey) : DefaultCatalogType(key), Criterion
