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
@file:JvmName("VisibilityRegistry")
package org.lanternpowered.server.registry.type.scoreboard

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.text.TextRepresentable
import org.lanternpowered.api.text.textOf
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.registry.customInternalCatalogTypeRegistry
import org.spongepowered.api.scoreboard.Visibility

@get:JvmName("get")
val VisibilityRegistry = customInternalCatalogTypeRegistry<Visibility, String> {
    fun register(internalId: String, id: String) =
            register(internalId, LanternVisibility(NamespacedKey.minecraft(id)))

    register("always", "always")
    register("hideForOwnTeam", "hide_for_own_team")
    register("hideForOtherTeams", "hide_for_other_teams")
    register("never", "never")
}

private class LanternVisibility(key: NamespacedKey) : DefaultCatalogType(key), Visibility, TextRepresentable by textOf(key.value)
