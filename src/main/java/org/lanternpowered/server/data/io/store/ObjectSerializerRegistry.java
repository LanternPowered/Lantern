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
package org.lanternpowered.server.data.io.store;

import static com.google.common.base.Preconditions.checkNotNull;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.lanternpowered.server.block.entity.LanternBlockEntity;
import org.lanternpowered.server.data.io.store.entity.EntitySerializer;
import org.lanternpowered.server.data.io.store.item.ItemStackStore;
import org.lanternpowered.server.data.io.store.block.BlockEntitySerializer;
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
        register(LanternItemStack.class, new ItemStackStore());
        register(LanternEntity.class, new EntitySerializer());
        register(LanternBlockEntity.class, new BlockEntitySerializer());
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
