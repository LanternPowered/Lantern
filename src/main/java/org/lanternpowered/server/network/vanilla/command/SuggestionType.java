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

public final class SuggestionType {

    public static SuggestionType of(String id) {
        return new SuggestionType(id);
    }

    private final String id;

    private SuggestionType(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}
