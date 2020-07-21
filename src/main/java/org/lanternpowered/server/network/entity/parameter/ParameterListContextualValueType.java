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
package org.lanternpowered.server.network.entity.parameter;

import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueType;
import org.lanternpowered.server.network.packet.codec.CodecContext;

public final class ParameterListContextualValueType implements ContextualValueType<ParameterList> {

    @Override
    public void write(CodecContext ctx, ParameterList object, ByteBuffer buf) throws CodecException {
        ((AbstractParameterList) object).write(ctx, buf);
    }

    @Override
    public ParameterList read(CodecContext ctx, ByteBuffer buf) throws CodecException {
        throw new UnsupportedOperationException();
    }
}
