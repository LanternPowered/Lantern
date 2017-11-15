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
import org.lanternpowered.server.block.tile.vanilla.LanternContainerTile;
import org.lanternpowered.server.block.tile.vanilla.LanternJukebox;
import org.lanternpowered.server.data.io.store.entity.EntityStore;
import org.lanternpowered.server.data.io.store.entity.ItemStore;
import org.lanternpowered.server.data.io.store.entity.LivingStore;
import org.lanternpowered.server.data.io.store.entity.UserStore;
import org.lanternpowered.server.data.io.store.item.ItemStackStore;
import org.lanternpowered.server.data.io.store.tile.ContainerTileEntityStore;
import org.lanternpowered.server.data.io.store.tile.JukeboxTileEntitySerializer;
import org.lanternpowered.server.data.io.store.tile.TileEntityObjectStore;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.entity.LanternItem;
import org.lanternpowered.server.entity.LanternLiving;
import org.lanternpowered.server.entity.living.player.AbstractUser;
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
        register(LanternTileEntity.class, new TileEntityObjectStore<>());
        register(LanternContainerTile.class, new ContainerTileEntityStore<>());
        register(LanternJukebox.class, new JukeboxTileEntitySerializer<>());
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
