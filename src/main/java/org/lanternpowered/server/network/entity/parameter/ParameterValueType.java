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

import org.lanternpowered.server.network.buffer.ByteBuffer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public final class ParameterValueType<T> {

    private final static AtomicInteger counter = new AtomicInteger();

    /**
     * The internal id of the parameter value type.
     */
    final byte internalId;

    /**
     * The serializer to encode the values.
     */
    final ParameterValueSerializer<T> serializer;

    ParameterValueType(BiConsumer<ByteBuffer, T> serializer) {
        this((ctx, buf, value) -> serializer.accept(buf, value));
    }

    ParameterValueType(ParameterValueSerializer<T> serializer) {
        this.internalId = (byte) counter.getAndIncrement();
        this.serializer = serializer;
    }
}
