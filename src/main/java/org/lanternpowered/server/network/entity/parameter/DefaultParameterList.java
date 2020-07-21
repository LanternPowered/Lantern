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

import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.packet.codec.CodecContext;

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
