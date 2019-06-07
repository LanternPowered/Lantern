/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
        val register = { id: String, translationPart: String, internalId: Int, abilityApplier: DataHolder.() -> Unit ->
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
