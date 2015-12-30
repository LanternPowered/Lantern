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

import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import org.lanternpowered.server.network.message.codec.object.LocalizedText;
import org.lanternpowered.server.network.message.codec.object.VarInt;
import org.lanternpowered.server.network.message.codec.object.VarLong;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.text.Text;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

public class SimpleObjectSerializers implements ObjectSerializers {

    public static final SimpleObjectSerializers DEFAULT = new SimpleObjectSerializers() {

        {
            this.register(DataView.class, new SerializerDataView());
            this.register(String.class, new SerializerString());
            this.register(Text.class, new SerializerText());
            this.register(LocalizedText.class, new SerializerLocalizedText());
            this.register(UUID.class, new SerializerUUID());
            this.register(VarInt.class, new SerializerVarInt());
            this.register(VarLong.class, new SerializerVarLong());
            this.register(Vector3i.class, new SerializerVector3i());
        }

    };

    private final Map<Class<?>, ObjectSerializer<?>> serializers = Maps.newConcurrentMap();
    private final LoadingCache<Class<?>, Optional<ObjectSerializer<?>>> cache =
            CacheBuilder.newBuilder().build(new CacheLoader<Class<?>, Optional<ObjectSerializer<?>>>() {

                @Override
                public Optional<ObjectSerializer<?>> load(Class<?> key) throws Exception {
                    ObjectSerializer<?> serializer = null;
                    for (Class<?> type : TypeToken.of(key).getTypes().rawTypes()) {
                        serializer = serializers.get(type);
                        if (serializer != null) {
                            return Optional.<ObjectSerializer<?>>of(serializer);
                        }
                    }
                    return Optional.absent();
                }

            });

    @Override
    public <T> void register(Class<T> type, ObjectSerializer<? super T> serializer) {
        checkNotNull(serializer, "serializer");
        checkNotNull(type, "type");
        this.serializers.put(type, serializer);
        this.cache.invalidateAll();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> ObjectSerializer<T> findExact(Class<T> type) {
        return (ObjectSerializer<T>) this.serializers.get(type);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> ObjectSerializer<? super T> find(Class<T> type) {
        try {
            return (ObjectSerializer<? super T>) this.cache.get(type).orNull();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

}
