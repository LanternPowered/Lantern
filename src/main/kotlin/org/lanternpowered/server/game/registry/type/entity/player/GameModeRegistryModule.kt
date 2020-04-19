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
package org.lanternpowered.server.game.registry.type.entity.player

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode
import org.lanternpowered.server.game.registry.InternalPluginCatalogRegistryModule
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.Keys
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.entity.living.player.gamemode.GameModes

object GameModeRegistryModule : InternalPluginCatalogRegistryModule<GameMode>(GameModes::class) {

    @JvmStatic
    fun get(): GameModeRegistryModule = this

    override fun registerDefaults() {
        val register = { id: String, translationPart: String, internalId: Int, abilityApplier: DataHolder.Mutable.() -> Unit ->
            register(LanternGameMode(CatalogKey.minecraft(id), translationPart, internalId, abilityApplier)) }
        register("not_set", "notSet", -1) {}
        register("survival", "survival", 0) {
            offer(Keys.CAN_FLY, false)
            offer(Keys.IS_FLYING, false)
            offer(Keys.INVULNERABLE, false)
        }
        register("creative", "creative", 1) {
            offer(Keys.CAN_FLY, true)
            offer(Keys.INVULNERABLE, true)
        }
        register("adventure", "adventure", 2) {
            offer(Keys.CAN_FLY, false)
            offer(Keys.IS_FLYING, false)
            offer(Keys.INVULNERABLE, false)
        }
        register("spectator", "spectator", 3) {
            offer(Keys.CAN_FLY, true)
            offer(Keys.IS_FLYING, true)
            offer(Keys.INVULNERABLE, true)
        }
    }
}
