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

public final class ArgumentAndType<A extends Argument> {

    public static <A extends Argument> ArgumentAndType<A> of(ArgumentType<A> type, A argument) {
        return new ArgumentAndType<>(type, argument);
    }

    public static ArgumentAndType<Argument> of(ArgumentType<Argument> type) {
        return new ArgumentAndType<>(type, new Argument());
    }

    private final ArgumentType<A> type;
    private final A argument;

    private ArgumentAndType(ArgumentType<A> type, A argument) {
        this.argument = argument;
        this.type = type;
    }

    public ArgumentType<A> getType() {
        return this.type;
    }

    public A getArgument() {
        return this.argument;
    }
}
