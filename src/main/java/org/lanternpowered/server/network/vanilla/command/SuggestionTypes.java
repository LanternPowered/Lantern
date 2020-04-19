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
package org.lanternpowered.server.network.vanilla.command;

public final class SuggestionTypes {

    public static final SuggestionType ASK_SERVER = SuggestionType.of("minecraft:ask_server");

    public static final SuggestionType ALL_RECIPES = SuggestionType.of("minecraft:all_recipes");

    public static final SuggestionType AVAILABLE_SOUNDS = SuggestionType.of("minecraft:available_sounds");

    public static final SuggestionType SUMMONABLE_ENTITIES = SuggestionType.of("minecraft:summonable_entities");

    private SuggestionTypes() {
    }
}
