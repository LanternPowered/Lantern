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
package org.lanternpowered.server.world.rules;

import org.spongepowered.api.world.gamerule.DefaultGameRules;

public final class RuleTypes {

    public static final RuleType<Boolean> ANNOUNCE_ADVANCEMENTS =
            RuleType.create(DefaultGameRules.ANNOUNCE_ADVANCEMENTS, RuleDataTypes.BOOLEAN, true);
    public static final RuleType<Boolean> COMMAND_BLOCK_OUTPUT =
            RuleType.create(DefaultGameRules.COMMAND_BLOCK_OUTPUT, RuleDataTypes.BOOLEAN, true);
    public static final RuleType<Boolean> DISABLE_ELYTRA_MOVEMENT_CHECK =
            RuleType.create(DefaultGameRules.DISABLE_ELYTRA_MOVEMENT_CHECK, RuleDataTypes.BOOLEAN, false);
    public static final RuleType<Boolean> DO_DAYLIGHT_CYCLE =
            RuleType.create(DefaultGameRules.DO_DAYLIGHT_CYCLE, RuleDataTypes.BOOLEAN, true);
    public static final RuleType<Boolean> DO_ENTITY_DROPS =
            RuleType.create(DefaultGameRules.DO_ENTITY_DROPS, RuleDataTypes.BOOLEAN, true);
    public static final RuleType<Boolean> DO_FIRE_TICK =
            RuleType.create(DefaultGameRules.DO_FIRE_TICK, RuleDataTypes.BOOLEAN, true);
    public static final RuleType<Boolean> DO_LIMITED_CRAFTING =
            RuleType.create(DefaultGameRules.DO_LIMITED_CRAFTING, RuleDataTypes.BOOLEAN, false);
    public static final RuleType<Boolean> DO_MOB_LOOT =
            RuleType.create(DefaultGameRules.DO_MOB_LOOT, RuleDataTypes.BOOLEAN, true);
    public static final RuleType<Boolean> DO_MOB_SPAWNING =
            RuleType.create(DefaultGameRules.DO_MOB_SPAWNING, RuleDataTypes.BOOLEAN, true);
    public static final RuleType<Boolean> DO_TILE_DROPS =
            RuleType.create(DefaultGameRules.DO_TILE_DROPS, RuleDataTypes.BOOLEAN, true);
    public static final RuleType<Boolean> DO_WEATHER_CYCLE =
            RuleType.create(DefaultGameRules.DO_WEATHER_CYCLE, RuleDataTypes.BOOLEAN, true);
    public static final RuleType<String> GAME_LOOP_FUNCTION =
            RuleType.create(DefaultGameRules.GAME_LOOP_FUNCTION, RuleDataTypes.STRING, "-");
    public static final RuleType<Boolean> KEEP_INVENTORY =
            RuleType.create(DefaultGameRules.KEEP_INVENTORY, RuleDataTypes.BOOLEAN, false);
    public static final RuleType<Boolean> LOG_ADMIN_COMMANDS =
            RuleType.create(DefaultGameRules.LOG_ADMIN_COMMANDS, RuleDataTypes.BOOLEAN, true);
    public static final RuleType<Integer> MAX_COMMAND_CHAIN_LENGTH =
            RuleType.create(DefaultGameRules.MAX_COMMAND_CHAIN_LENGTH, RuleDataTypes.INTEGER, 65536);
    public static final RuleType<Integer> MAX_ENTITY_CRAMMING =
            RuleType.create(DefaultGameRules.MAX_ENTITY_CRAMMING, RuleDataTypes.INTEGER, 24);
    public static final RuleType<Boolean> MOB_GRIEFING =
            RuleType.create(DefaultGameRules.MOB_GRIEFING, RuleDataTypes.BOOLEAN, true);
    public static final RuleType<Boolean> NATURAL_REGENERATION =
            RuleType.create(DefaultGameRules.NATURAL_REGENERATION, RuleDataTypes.BOOLEAN, true);
    public static final RuleType<Integer> RANDOM_TICK_SPEED =
            RuleType.create(DefaultGameRules.RANDOM_TICK_SPEED, RuleDataTypes.INTEGER, 3);
    public static final RuleType<Boolean> REDUCED_DEBUG_INFO =
            RuleType.create(DefaultGameRules.REDUCED_DEBUG_INFO, RuleDataTypes.BOOLEAN, false);
    public static final RuleType<Boolean> SEND_COMMAND_FEEDBACK =
            RuleType.create(DefaultGameRules.SEND_COMMAND_FEEDBACK, RuleDataTypes.BOOLEAN, true);
    public static final RuleType<Boolean> SHOW_DEATH_MESSAGES =
            RuleType.create(DefaultGameRules.SHOW_DEATH_MESSAGES, RuleDataTypes.BOOLEAN, true);
    public static final RuleType<Integer> SPAWN_RADIUS =
            RuleType.create(DefaultGameRules.SPAWN_RADIUS, RuleDataTypes.INTEGER, 10);
    public static final RuleType<Boolean> SPECTATORS_GENERATE_CHUNKS =
            RuleType.create(DefaultGameRules.SPECTATORS_GENERATE_CHUNKS, RuleDataTypes.BOOLEAN, true);

    public static void init() {
    }

    private RuleTypes() {
    }
}
