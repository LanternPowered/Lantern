/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.message.codec.serializer.defaults;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.message.codec.serializer.SerializerContext;
import org.lanternpowered.server.network.message.codec.serializer.Types;
import org.lanternpowered.server.network.message.codec.serializer.ValueSerializer;
import org.lanternpowered.server.network.objects.RawItemStack;
import org.spongepowered.api.data.DataView;

public class SerializerRawItemStack implements ValueSerializer<RawItemStack> {

    @Override
    public void write(SerializerContext context, ByteBuf buf, RawItemStack object) throws CodecException {
        if (object == null) {
            buf.writeByte(-1);
        } else {
            buf.writeShort(object.getItemType());
            buf.writeByte(object.getAmount());
            buf.writeShort(object.getData());
            context.write(buf, Types.DATA_VIEW, object.getDataView());
        }
    }

    @Override
    public RawItemStack read(SerializerContext context, ByteBuf buf) throws CodecException {
        short id = buf.readShort();
        if (id == -1) {
            return null;
        }
        int amount = buf.readByte();
        int data = buf.readShort();
        DataView dataView = context.read(buf, Types.DATA_VIEW);
        return new RawItemStack(id, data, amount, dataView);
    }
}
