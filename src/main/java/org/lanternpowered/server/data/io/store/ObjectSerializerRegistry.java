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
package org.lanternpowered.server.data.io.store;

import static com.google.common.base.Preconditions.checkNotNull;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.lanternpowered.server.block.tile.LanternTileEntity;
import org.lanternpowered.server.data.io.store.entity.EntitySerializer;
import org.lanternpowered.server.data.io.store.item.ItemStackStore;
import org.lanternpowered.server.data.io.store.tile.TileEntitySerializer;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.inventory.LanternItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ObjectSerializerRegistry {

    private static final ObjectSerializerRegistry registry = new ObjectSerializerRegistry();

    public static ObjectSerializerRegistry get() {
        return registry;
    }

    private final Map<Class<?>, ObjectSerializer> objectSerializers = new HashMap<>();
    private final LoadingCache<Class<?>, Optional<ObjectSerializer>> objectSerializerCache =
            Caffeine.newBuilder().build(this::findSerializer);

    private Optional<ObjectSerializer> findSerializer(Class<?> key) {
        ObjectSerializer store;
        while (key != Object.class && key != null) {
            store = this.objectSerializers.get(key);
            if (store != null) {
                return Optional.of(store);
            }
            for (Class<?> interf : key.getInterfaces()) {
                store = this.objectSerializers.get(interf);
                if (store != null) {
                    return Optional.of(store);
                }
            }
            key = key.getSuperclass();
        }
        return Optional.empty();
    }

    public ObjectSerializerRegistry() {
        this.register(LanternItemStack.class, new ItemStackStore());
        this.register(LanternEntity.class, new EntitySerializer());
        this.register(LanternTileEntity.class, new TileEntitySerializer());
    }

    /**
     * Register a {@link ObjectSerializer} for the specified object type.
     *
     * @param objectType The object type
     * @param objectSerializer The object serializer
     * @param <T> The type of the object
     */
    public <T> void register(Class<? extends T> objectType, ObjectSerializer<T> objectSerializer) {
        this.objectSerializers.put(checkNotNull(objectType, "objectType"), checkNotNull(objectSerializer, "objectSerializer"));
        this.objectSerializerCache.invalidateAll();
    }

    /**
     * Gets the most suitable {@link ObjectSerializer} for the specified object type,
     * may return {@link Optional#empty()} if no suitable store could be found.
     *
     * @param objectType The object type
     * @param <T> The type of the object
     * @return The object serializer
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<ObjectSerializer<T>> get(Class<? extends T> objectType) {
        return (Optional) this.objectSerializerCache.get(objectType);
    }
}
