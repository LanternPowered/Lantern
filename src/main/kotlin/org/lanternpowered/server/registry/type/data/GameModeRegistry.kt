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
@file:JvmName("GameModeRegistry")
package org.lanternpowered.server.registry.type.data

import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.entity.player.gamemode.LanternGameMode
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.spongepowered.api.data.DataHolder
import org.lanternpowered.api.data.Keys
import org.spongepowered.api.entity.living.player.gamemode.GameMode

@get:JvmName("get")
val GameModeRegistry = internalCatalogTypeRegistry<GameMode> {
    fun register(internalId: Int, id: String, translationPart: String, abilityApplier: DataHolder.Mutable.() -> Unit) =
            register(internalId, LanternGameMode(minecraftKey(id), translatableTextOf("gameMode.$translationPart"), abilityApplier))

    register(-1, "not_set", "notSet") {}
    register(0, "survival", "survival") {
        offer(Keys.CAN_FLY, false)
        offer(Keys.IS_FLYING, false)
        offer(Keys.IS_INVULNERABLE, false)
    }
    register(1, "creative", "creative") {
        offer(Keys.CAN_FLY, true)
        offer(Keys.IS_INVULNERABLE, true)
    }
    register(2, "adventure", "adventure") {
        offer(Keys.CAN_FLY, false)
        offer(Keys.IS_FLYING, false)
        offer(Keys.IS_INVULNERABLE, false)
    }
    register(3, "spectator", "spectator") {
        offer(Keys.CAN_FLY, true)
        offer(Keys.IS_FLYING, true)
        offer(Keys.IS_INVULNERABLE, true)
    }
}
