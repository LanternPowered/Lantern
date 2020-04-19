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

import org.lanternpowered.server.network.vanilla.command.argument.ArgumentAndType;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class ArgumentNode extends Node {

    private final String name;
    private final ArgumentAndType argumentAndType;
    @Nullable private final SuggestionType suggestions;

    public ArgumentNode(List<Node> children, String name, ArgumentAndType argumentAndType,
            @Nullable String command,
            @Nullable Node redirect, @Nullable SuggestionType suggestions) {
        super(children, redirect, command);
        this.argumentAndType = argumentAndType;
        this.suggestions = suggestions;
        this.name = name;
    }

    @Nullable
    public SuggestionType getCustomSuggestions() {
        return this.suggestions;
    }

    public ArgumentAndType getArgumentAndType() {
        return this.argumentAndType;
    }

    public String getName() {
        return this.name;
    }
}
