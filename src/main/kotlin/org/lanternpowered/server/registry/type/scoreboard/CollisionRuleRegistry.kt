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
@file:JvmName("CollisionRuleRegistry")
package org.lanternpowered.server.registry.type.scoreboard

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.registry.customInternalCatalogTypeRegistry
import org.spongepowered.api.scoreboard.CollisionRule
import org.spongepowered.api.text.translation.FixedTranslation

@get:JvmName("get")
val CollisionRuleRegistry = customInternalCatalogTypeRegistry<CollisionRule, String> {
    fun register(internalId: String, id: String) =
            register(internalId, LanternCollisionRule(CatalogKey.minecraft(id)))

    register("always", "never")
    register("pushOwnTeam", "push_own_team")
    register("pushOtherTeams", "push_other_teams")
    register("never", "never")
}

class LanternCollisionRule(key: CatalogKey) : DefaultCatalogType(key), CollisionRule {
    override fun getTranslation() = FixedTranslation(this.key.value) // TODO
}
