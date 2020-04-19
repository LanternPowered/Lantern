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
package org.lanternpowered.server.network.vanilla.command.argument;

public class StringArgument extends Argument {

    private final Type type;

    public StringArgument(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    public enum Type {
        SINGLE_WORD,
        QUOTABLE_PHRASE,
        GREEDY_PHRASE,
    }
}
