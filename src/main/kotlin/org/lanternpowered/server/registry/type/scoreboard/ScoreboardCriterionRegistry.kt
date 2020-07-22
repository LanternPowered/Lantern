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

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.scoreboard.ScoreboardCriterion
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.registry.customInternalCatalogTypeRegistry

val ScoreboardCriterionRegistry = customInternalCatalogTypeRegistry<ScoreboardCriterion, String> {
    fun register(internalId: String, id: String) =
            register(internalId, LanternScoreboardCriterion(NamespacedKey.minecraft(id)))

    register("dummy", "dummy")
    register("trigger", "trigger")
    register("health", "health")
    register("playerKillCount", "player_kills")
    register("totalKillCount", "total_kills")
    register("deathCount", "deaths")
}

private class LanternScoreboardCriterion(key: NamespacedKey) : DefaultCatalogType(key), ScoreboardCriterion
