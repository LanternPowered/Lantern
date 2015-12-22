/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
package org.lanternpowered.server.network.message.codec.object.serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.data.io.nbt.NbtDataContainerInputStream;
import org.lanternpowered.server.data.io.nbt.NbtDataContainerOutputStream;

import org.spongepowered.api.data.DataView;

public class SerializerDataView implements ObjectSerializer<DataView> {

    @Override
    public void write(ObjectSerializerContext context, ByteBuf buf, @Nullable DataView object) throws CodecException {
        if (object == null) {
            buf.writeByte(0);
            return;
        }
        try {
            DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(new ByteBufOutputStream(buf)));
            NbtDataContainerOutputStream ndvos = new NbtDataContainerOutputStream(dos);
            ndvos.write(object);
            ndvos.flush();
            ndvos.close();
        } catch (IOException e) {
            throw new CodecException(e);
        }
    }

    @Nullable
    @Override
    public DataView read(ObjectSerializerContext context, ByteBuf buf) throws CodecException {
        int index = buf.readerIndex();
        if (buf.readByte() == 0) {
            return null;
        }
        buf.readerIndex(index);
        try {
            DataInputStream dis = new DataInputStream(new GZIPInputStream(new ByteBufInputStream(buf)));
            NbtDataContainerInputStream ndvis = new NbtDataContainerInputStream(dis);
            try {
                return ndvis.read();
            } finally {
                ndvis.close();
            }
        } catch (IOException e) {
            throw new CodecException(e);
        }
    }

}
