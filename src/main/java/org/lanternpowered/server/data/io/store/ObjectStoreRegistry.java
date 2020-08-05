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
import org.lanternpowered.server.block.entity.vanilla.ContainerBlockEntity;
import org.lanternpowered.server.block.entity.vanilla.LanternJukebox;
import org.lanternpowered.server.data.io.store.entity.EntityStore;
import org.lanternpowered.server.data.io.store.entity.ItemStore;
import org.lanternpowered.server.data.io.store.entity.LivingStore;
import org.lanternpowered.server.data.io.store.entity.UserStore;
import org.lanternpowered.server.data.io.store.item.ItemStackStore;
import org.lanternpowered.server.data.io.store.block.ContainerBlockEntityStore;
import org.lanternpowered.server.data.io.store.block.JukeboxBlockEntitySerializer;
import org.lanternpowered.server.data.io.store.block.BlockEntityObjectStore;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.entity.LanternItem;
import org.lanternpowered.server.entity.LanternLiving;
import org.lanternpowered.server.entity.player.AbstractUser;
import org.lanternpowered.server.inventory.LanternItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ObjectStoreRegistry {

    private static final ObjectStoreRegistry registry = new ObjectStoreRegistry();

    public static ObjectStoreRegistry get() {
        return registry;
    }

    private final Map<Class<?>, ObjectStore> objectsStores = new HashMap<>();
    private final LoadingCache<Class<?>, Optional<ObjectStore>> objectStoreCache =
            Caffeine.newBuilder().build(this::findStore);

    private Optional<ObjectStore> findStore(Class<?> key) {
        ObjectStore store;
        while (key != Object.class) {
            store = this.objectsStores.get(key);
            if (store != null) {
                return Optional.of(store);
            }
            for (Class<?> interf : key.getInterfaces()) {
                store = this.objectsStores.get(interf);
                if (store != null) {
                    return Optional.of(store);
                }
            }
            key = key.getSuperclass();
        }
        return Optional.empty();
    }

    public ObjectStoreRegistry() {
        register(LanternEntity.class, new EntityStore<>());
        register(LanternItem.class, new ItemStore());
        register(LanternLiving.class, new LivingStore<>());
        register(AbstractUser.class, new UserStore<>());
        register(LanternItemStack.class, ItemStackStore.INSTANCE);

        // Tile entities
        register(LanternBlockEntity.class, new BlockEntityObjectStore<>());
        register(ContainerBlockEntity.class, new ContainerBlockEntityStore<>());
        register(LanternJukebox.class, new JukeboxBlockEntitySerializer<>());
    }

    /**
     * Register a {@link ObjectStore} for the specified object type.
     *
     * @param objectType The object type
     * @param objectStore The object store
     * @param <T> The type of the object
     */
    public <T> void register(Class<? extends T> objectType, ObjectStore<T> objectStore) {
        this.objectsStores.put(checkNotNull(objectType, "objectType"), checkNotNull(objectStore, "objectStore"));
        this.objectStoreCache.invalidateAll();
    }

    /**
     * Gets the most suitable {@link ObjectStore} for the specified object type,
     * may return {@link Optional#empty()} if no suitable store could be found.
     *
     * @param objectType The object type
     * @param <T> The type of the object
     * @return The object store
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<ObjectStore<T>> get(Class<? extends T> objectType) {
        return (Optional) this.objectStoreCache.get(objectType);
    }
}
