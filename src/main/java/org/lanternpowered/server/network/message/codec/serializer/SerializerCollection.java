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
package org.lanternpowered.server.network.message.codec.serializer;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.lanternpowered.server.network.message.codec.serializer.defaults.SerializerDataView;
import org.lanternpowered.server.network.message.codec.serializer.defaults.SerializerLocalizedText;
import org.lanternpowered.server.network.message.codec.serializer.defaults.SerializerParameters;
import org.lanternpowered.server.network.message.codec.serializer.defaults.SerializerRawItemStack;
import org.lanternpowered.server.network.message.codec.serializer.defaults.SerializerString;
import org.lanternpowered.server.network.message.codec.serializer.defaults.SerializerText;
import org.lanternpowered.server.network.message.codec.serializer.defaults.SerializerUUID;
import org.lanternpowered.server.network.message.codec.serializer.defaults.SerializerVarInt;
import org.lanternpowered.server.network.message.codec.serializer.defaults.SerializerVarLong;
import org.lanternpowered.server.network.message.codec.serializer.defaults.SerializerVector3i;

import java.util.Optional;

public final class SerializerCollection {

    public static final SerializerCollection DEFAULT = new SerializerCollection()
            .bind(Types.DATA_VIEW, new SerializerDataView())
            .bind(Types.VAR_INT, new SerializerVarInt())
            .bind(Types.VAR_LONG, new SerializerVarLong())
            .bind(Types.POSITION, new SerializerVector3i())
            .bind(Types.TEXT, new SerializerText())
            .bind(Types.STRING, new SerializerString())
            .bind(Types.UNIQUE_ID, new SerializerUUID())
            .bind(Types.LOCALIZED_TEXT, new SerializerLocalizedText())
            .bind(Types.PARAMETERS, new SerializerParameters())
            .bind(Types.RAW_ITEM_STACK, new SerializerRawItemStack());

    private final TIntObjectMap<ValueSerializer<?>> valueSerializers = new TIntObjectHashMap<>();

    /**
     * Binds a {@link ValueSerializer} to the {@link Type}.
     *
     * @param type the type
     * @param valueSerializer the value serializer
     * @param <V> the value type
     */
    public <V> SerializerCollection bind(Type<V> type, ValueSerializer<V> valueSerializer) {
        checkNotNull(type, "type");
        checkNotNull(valueSerializer, "valueSerializer");
        checkArgument(!this.valueSerializers.containsKey(type.index),
                "There is already a value serializer bound to this type.");
        this.valueSerializers.put(type.index, valueSerializer);
        return this;
    }

    /**
     * Gets the {@link ValueSerializer} for the {@link Type}, may return {@link Optional#empty()}.
     *
     * @param type the type
     * @param <V> the value type
     * @return the value serializer
     */
    @SuppressWarnings("unchecked")
    public <V> Optional<ValueSerializer<V>> get(Type<V> type) {
        return Optional.ofNullable((ValueSerializer) this.valueSerializers.get(type.index));
    }

}
