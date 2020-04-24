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
package org.lanternpowered.server.network.vanilla.command

import org.lanternpowered.server.network.vanilla.command.SuggestionType.Companion.of

object SuggestionTypes {

    @JvmField
    val ASK_SERVER = of("minecraft:ask_server")
    val ALL_RECIPES = of("minecraft:all_recipes")
    val AVAILABLE_SOUNDS = of("minecraft:available_sounds")
    val SUMMONABLE_ENTITIES = of("minecraft:summonable_entities")
}
