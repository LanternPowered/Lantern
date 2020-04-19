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

@SuppressWarnings("unchecked")
public interface ArgumentType<A extends Argument> {

    /**
     * Creates a new {@link ArgumentType} with the given id.
     *
     * @param id The id
     * @return The argument type
     */
    static ArgumentType<Argument> of(String id) {
        return new SimpleArgumentType(id, SimpleArgumentCodec.INSTANCE);
    }

    /**
     * Creates a new {@link ArgumentType} with the given id.
     *
     * @param id The id
     * @return The argument type
     */
    static <A extends Argument> ArgumentType<A> of(String id, ArgumentCodec<A> codec) {
        return new SimpleArgumentType(id, codec);
    }

    String getId();

    ArgumentCodec<A> getCodec();
}
