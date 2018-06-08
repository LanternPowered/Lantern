/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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
package org.lanternpowered.server.network.entity.parameter;

import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.CodecContext;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link ParameterList} which writes the content directly to
 * a {@link ByteBuffer}. The value of a specific {@link ParameterType}
 * cannot be overwritten by calling the method again, this will
 * result in an {@link IllegalStateException}.
 */
@SuppressWarnings({"ConstantConditions", "unchecked"})
public class DefaultParameterList extends AbstractParameterList {

    private final Map<ParameterType, Object> entries = new HashMap<>();

    @Override
    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    @Override
    public <T> void add(ParameterType<T> type, T value) {
        final Object oldValue = this.entries.putIfAbsent(type, value);
        checkState(oldValue == null, "The parameter type %s can only be added once: ", type.index);
    }

    @Override
    void write(CodecContext ctx, ByteBuffer buf) {
        for (Map.Entry<ParameterType, Object> entry : this.entries.entrySet()) {
            final ParameterType type = entry.getKey();
            buf.writeByte(type.index);
            buf.writeByte(type.getValueType().internalId);
            type.getValueType().serializer.serialize(ctx, buf, entry.getValue());
        }
        super.write(ctx, buf);
    }

}
