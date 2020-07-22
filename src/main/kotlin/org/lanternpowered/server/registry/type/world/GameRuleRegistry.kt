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
package org.lanternpowered.server.registry.type.world

import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.api.util.type.TypeToken
import org.lanternpowered.api.util.type.typeTokenOf
import org.lanternpowered.server.registry.InternalCatalogTypeRegistryBuilder
import org.lanternpowered.server.registry.customInternalCatalogTypeRegistry
import org.lanternpowered.server.world.gamerule.LanternGameRule
import org.spongepowered.api.world.gamerule.GameRule

private fun <T> InternalCatalogTypeRegistryBuilder<GameRule<*>, String>.register(
        id: String, name: String, type: TypeToken<T>, value: T
) = register(name, LanternGameRule(minecraftKey(id), name, type, value))

private inline fun <reified T> InternalCatalogTypeRegistryBuilder<GameRule<*>, String>.register(
        id: String, name: String, value: T
) = register(id, name, typeTokenOf(), value)

val GameRuleRegistry = customInternalCatalogTypeRegistry<GameRule<*>, String> {
    register("announce_advancements", "announceAdvancements", true)
    register("command_block_output", "commandBlockOutput", true)
    register("disable_elytra_movement_check", "disableElytraMovementCheck", false)
    register("disable_raids", "disableRaids", false)
    register("do_daylight_cycle", "doDaylightCycle", true)
    register("do_entity_drops", "doEntityDrops", true)
    register("do_fire_tick", "doFireTick", true)
    register("do_insomnia", "doInsomnia", true)
    register("do_immediate_respawn", "doImmediateRespawn", false)
    register("do_limited_crafting", "doLimitedCrafting", false)
    register("do_mob_loot", "doMobLoot", true)
    register("do_mob_spawning", "doMobSpawning", true)
    register("do_patrol_spawning", "doPatrolSpawning", true)
    register("do_tile_drops", "doTileDrops", true)
    register("do_trader_spawning", "doTraderSpawning", true)
    register("do_weather_cycle", "doWeatherCycle", true)
    register("take_drowning_damage", "drowningDamage", true)
    register("take_fall_damage", "fallDamage", true)
    register("take_fire_damage", "fireDamage", true)
    register("forgive_dead_players", "forgiveDeadPlayers", true)
    register("keep_inventory", "keepInventory", false)
    register("log_admin_commands", "logAdminCommands", true)
    register("max_command_chain_length", "maxCommandChainLength", 65536)
    register("max_entity_cramming", "maxEntityCramming", 24)
    register("mob_griefing", "mobGriefing", true)
    register("natural_regeneration", "naturalRegeneration", true)
    register("random_tick_speed", "randomTickSpeed", 3)
    register("reduced_debug_info", "reducedDebugInfo", false)
    register("send_command_feedback", "sendCommandFeedback", true)
    register("spawn_radius", "spawnRadius", 10)
    register("show_death_messages", "showDeathMessages", true)
    register("spectators_generate_chunks", "spectatorsGenerateChunks", true)
}
