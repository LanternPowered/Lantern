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

import org.lanternpowered.server.network.buffer.ByteBuffer;

final class SimpleArgumentCodec implements ArgumentCodec<Argument> {

    static final SimpleArgumentCodec INSTANCE = new SimpleArgumentCodec();

    @Override
    public void encode(ByteBuffer buf, Argument argument) {
    }
}
