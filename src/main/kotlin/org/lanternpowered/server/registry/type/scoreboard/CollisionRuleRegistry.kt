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

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.TextRepresentable
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.registry.customInternalCatalogTypeRegistry
import org.spongepowered.api.scoreboard.CollisionRule

@get:JvmName("get")
val CollisionRuleRegistry = customInternalCatalogTypeRegistry<CollisionRule, String> {
    fun register(internalId: String, id: String) =
            register(internalId, LanternCollisionRule(minecraftKey(id), translatableTextOf("team.collision.$id")))

    register("always", "never")
    register("pushOwnTeam", "push_own_team")
    register("pushOtherTeams", "push_other_teams")
    register("never", "never")
}

private class LanternCollisionRule(key: NamespacedKey, text: Text) : DefaultCatalogType(key), CollisionRule, TextRepresentable by text
