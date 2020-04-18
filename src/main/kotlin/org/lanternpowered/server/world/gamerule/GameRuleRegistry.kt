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
package org.lanternpowered.server.world.gamerule

import com.google.common.base.CaseFormat
import com.google.common.reflect.TypeToken
import org.lanternpowered.api.util.type.typeTokenOf
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.text.translation.FixedTranslation
import org.spongepowered.api.world.gamerule.GameRule
import org.spongepowered.api.world.gamerule.GameRules

object GameRuleRegistry : DefaultCatalogRegistryModule<GameRule<*>>(GameRules::class) {

    override fun registerDefaults() {
        register("announceAdvancements", true)
        register("commandBlockOutput", true)
        register("disableElytraMovementCheck", false)
        register("doDaylightCycle", true)
        register("doEntityDrops", true)
        register("doFireTick", true)
        register("doLimitedCrafting", false)
        register("doMobLoot", true)
        register("doMobSpawning", true)
        register("doTileDrops", true)
        register("doWeatherCycle", true)
        register("keepInventory", false)
        register("logAdminCommands", true)
        register("maxCommandChainLength", 65536)
        register("maxEntityCramming", 24)
        register("mobGriefing", true)
        register("naturalRegeneration", true)
        register("randomTickSpeed", 3)
        register("reducedDebugInfo", false)
        register("sendCommandFeedback", true)
        register("showDeathMessages", true)
        register("spawnRadius", 10)
        register("spectatorsGenerateChunks", true)
    }

    private inline fun <reified T: Any> register(name: String, defaultValue: T) {
        register(name, typeTokenOf(), defaultValue)
    }

    private fun <T : Any> register(name: String, valueType: TypeToken<T>, defaultValue: T) {
        val id = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name)
        val gameRule = LanternGameRule(CatalogKey.minecraft(id), FixedTranslation(name), valueType, defaultValue)
        register(gameRule)
    }
}
