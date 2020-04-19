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
