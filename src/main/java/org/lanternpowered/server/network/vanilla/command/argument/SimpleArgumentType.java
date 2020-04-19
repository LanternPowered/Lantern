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
final class SimpleArgumentType<A extends Argument> implements ArgumentType<A> {

    private final String id;
    private final ArgumentCodec<A> codec;

    SimpleArgumentType(String id, ArgumentCodec<A> codec) {
        this.id = id;
        this.codec = codec;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public ArgumentCodec<A> getCodec() {
        return this.codec;
    }
}
