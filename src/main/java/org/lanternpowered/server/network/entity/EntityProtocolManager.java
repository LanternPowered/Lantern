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
package org.lanternpowered.server.network.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3d;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.entity.event.EntityEvent;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;

import javax.annotation.Nullable;

public final class EntityProtocolManager {

    public static final int INVALID_ENTITY_ID = -1;

    private static final int UPDATE_RATE = 3;

    public static int acquireEntityId() {
        return new EntityProtocolInitContextImpl(null).acquire();
    }

    public static void releaseEntityId(int id) {
        new EntityProtocolInitContextImpl(null).release(id);
    }

    private final Map<Entity, AbstractEntityProtocol<?>> entityProtocols = new ConcurrentHashMap<>();

    /**
     * All the {@link AbstractEntityProtocol}s that will be destroyed.
     */
    private final Queue<AbstractEntityProtocol<?>> queuedForRemoval = new ConcurrentLinkedDeque<>();

    /**
     * All the {@link LanternPlayer}s that will refreshed.
     */
    private final Queue<LanternPlayer> queuedForRefresh = new ConcurrentLinkedDeque<>();

    private final Int2ObjectMap<AbstractEntityProtocol<?>> idToEntityProtocolMap = new Int2ObjectOpenHashMap<>();

    // Start at 1, using 0 may cause some issues
    // on the client regarding elytra flying boost,
    // and maybe other related bugs.
    private static int allocatorIdCounter = 1;

    private final static IntSet allocatorReusableIds = new IntOpenHashSet();
    private final static StampedLock allocatorLock = new StampedLock();

    /**
     * The {@link EntityProtocolInitContext}.
     */
    private static class EntityProtocolInitContextImpl implements EntityProtocolInitContext {

        @Nullable private final AbstractEntityProtocol<?> entityProtocol;

        private EntityProtocolInitContextImpl(@Nullable AbstractEntityProtocol<?> entityProtocol) {
            this.entityProtocol = entityProtocol;
        }

        /**
         * Acquires the next free id.
         *
         * @return The id
         */
        @Override
        public int acquire() {
            final long stamp = allocatorLock.writeLock();
            try {
                return acquire0();
            } finally {
                allocatorLock.unlockWrite(stamp);
            }
        }

        @Override
        public int[] acquire(int count) {
            return acquire(new int[count]);
        }

        @Override
        public int[] acquire(int[] array) {
            checkNotNull(array, "array");
            final long stamp = allocatorLock.writeLock();
            try {
                for (int i = 0; i < array.length; i++) {
                    array[i] = acquire0();
                }
            } finally {
                allocatorLock.unlockWrite(stamp);
            }
            return array;
        }

        @Override
        public int[] acquireRow(int count) {
            return acquireRow(new int[count]);
        }

        @Override
        public int[] acquireRow(int[] array) {
            checkNotNull(array, "array");
            final long stamp = allocatorLock.writeLock();
            try {
                IntIterator it = allocatorReusableIds.iterator();
                boolean fail = false;
                for (int i = 0; i < array.length; i++) {
                    if (!it.hasNext()) {
                        fail = true;
                        break;
                    }
                    array[i] = it.nextInt();
                    if (i != 0 && array[i - 1] != array[i] - 1) {
                        fail = true;
                        break;
                    }
                }
                if (fail) {
                    for (int i = 0; i < array.length; i++) {
                        array[i] = allocatorIdCounter++;
                        if (this.entityProtocol != null) {
                            this.entityProtocol.entityProtocolManager.idToEntityProtocolMap.put(array[i], this.entityProtocol);
                        }
                    }
                } else {
                    it = allocatorReusableIds.iterator();
                    for (int id : array) {
                        it.nextInt();
                        it.remove();
                        if (this.entityProtocol != null) {
                            this.entityProtocol.entityProtocolManager.idToEntityProtocolMap.put(id, this.entityProtocol);
                        }
                    }
                }
            } finally {
                allocatorLock.unlockWrite(stamp);
            }
            return array;
        }

        private int acquire0() {
            final int id;
            final IntIterator it = allocatorReusableIds.iterator();
            if (it.hasNext()) {
                try {
                    id = it.nextInt();
                } finally {
                    it.remove();
                }
            } else {
                id = allocatorIdCounter++;
            }
            if (this.entityProtocol != null) {
                this.entityProtocol.entityProtocolManager.idToEntityProtocolMap.put(id, this.entityProtocol);
            }
            return id;
        }

        @Override
        public void release(int id) {
            if (id != INVALID_ENTITY_ID) {
                final long stamp = allocatorLock.writeLock();
                try {
                    allocatorReusableIds.add(id);
                    if (this.entityProtocol != null) {
                        this.entityProtocol.entityProtocolManager.idToEntityProtocolMap.remove(id);
                    }
                } finally {
                    allocatorLock.unlockWrite(stamp);
                }
            }
        }

        @Override
        public void release(int[] array) {
            checkNotNull(array, "array");
            final long stamp = allocatorLock.writeLock();
            try {
                for (int id : array) {
                    allocatorReusableIds.add(id);
                    if (this.entityProtocol != null) {
                        this.entityProtocol.entityProtocolManager.idToEntityProtocolMap.remove(id);
                    }
                }
            } finally {
                allocatorLock.unlockWrite(stamp);
            }
        }
    }

    private int pulseCounter;

    Optional<AbstractEntityProtocol<?>> getEntityProtocolById(int id) {
        long stamp = allocatorLock.tryOptimisticRead();
        AbstractEntityProtocol<?> entityProtocol = stamp != 0L ? this.idToEntityProtocolMap.get(id) : null;
        if (stamp == 0L || !allocatorLock.validate(stamp)) {
            stamp = allocatorLock.readLock();
            try {
                entityProtocol = this.idToEntityProtocolMap.get(id);
            } finally {
                allocatorLock.unlockRead(stamp);
            }
        }
        return Optional.ofNullable(entityProtocol);
    }

    Optional<AbstractEntityProtocol<?>> getEntityProtocolByEntity(Entity entity) {
        return Optional.ofNullable(this.entityProtocols.get(entity));
    }

    /**
     * Adds the {@link Entity} to be tracked.
     *
     * @param entity The entity
     */
    public void add(LanternEntity entity) {
        //noinspection ConstantConditions,unchecked
        add(entity, (EntityProtocolType) entity.getEntityProtocolType());
    }

    /**
     * Adds the {@link Entity} to be tracked with a specific {@link EntityProtocolType}.
     *
     * <p>This method forces the entity protocol to be refreshed, even if the entity
     * already a protocol.<p/>
     *
     * @param entity The entity
     * @param protocolType The protocol type
     */
    public <E extends LanternEntity> void add(E entity, EntityProtocolType<E> protocolType) {
        checkNotNull(entity, "entity");
        checkNotNull(protocolType, "protocolType");
        final AbstractEntityProtocol<E> entityProtocol = protocolType.getSupplier().apply(entity);
        entityProtocol.entityProtocolManager = this;
        final AbstractEntityProtocol<?> removed = this.entityProtocols.put(entity, entityProtocol);
        if (removed != null) {
            this.queuedForRemoval.add(removed);
        }
        entityProtocol.init(new EntityProtocolInitContextImpl(entityProtocol));
        if (entity instanceof NetworkIdHolder) {
            final long stamp = allocatorLock.writeLock();
            try {
                this.idToEntityProtocolMap.put(((NetworkIdHolder) entity).getNetworkId(), entityProtocol);
            } finally {
                allocatorLock.unlockWrite(stamp);
            }
        }
    }

    /**
     * Removes the {@link Entity} from being tracked.
     *
     * @param entity The entity
     */
    public void remove(LanternEntity entity) {
        checkNotNull(entity, "entity");
        final AbstractEntityProtocol<?> removed = this.entityProtocols.remove(entity);
        if (removed != null) {
            this.queuedForRemoval.add(removed);
        }
    }

    /**
     * Updates the trackers of the entities. The players list contains all the players that
     * are in the same world of the entities.
     *
     * @param players The players
     */
    public void updateTrackers(Set<LanternPlayer> players) {
        // TODO: Sync the updates in a different thread?
        if (this.pulseCounter++ % UPDATE_RATE != 0) {
            return;
        }

        AbstractEntityProtocol<?> removed;
        while ((removed = this.queuedForRemoval.poll()) != null) {
            removed.destroy(new EntityProtocolInitContextImpl(removed));
        }

        final Set<LanternPlayer> refreshedPlayers = new HashSet<>(this.queuedForRefresh.size());
        LanternPlayer player;
        while ((player = this.queuedForRefresh.poll()) != null) {
            refreshedPlayers.add(player);
        }

        final List<AbstractEntityProtocol.TrackerUpdateContextData> updateContextDataList = new ArrayList<>();

        final Set<AbstractEntityProtocol<?>> protocols = new HashSet<>(this.entityProtocols.values());
        for (AbstractEntityProtocol<?> protocol : protocols) {
            AbstractEntityProtocol.TrackerUpdateContextData contextData = protocol.buildUpdateContextData(players);
            if (!refreshedPlayers.isEmpty()) {
                if (contextData == null) {
                    contextData = protocol.createTrackerUpdateContextData();
                }
                if (contextData.removed == null) {
                    contextData.removed = refreshedPlayers;
                } else {
                    //noinspection unchecked
                    contextData.removed.addAll(refreshedPlayers);
                }
                if (contextData.added == null) {
                    contextData.added = refreshedPlayers;
                } else {
                    //noinspection unchecked
                    contextData.added.addAll(refreshedPlayers);
                }
            }
            if (contextData != null) {
                //noinspection unchecked
                protocol.updateTrackers(contextData);
                updateContextDataList.add(contextData);
            }
        }

        for (AbstractEntityProtocol.TrackerUpdateContextData contextData : updateContextDataList) {
            contextData.entityProtocol.postUpdateTrackers(contextData);
        }
    }

    private static final int INTERACT_DELAY = 50;

    public void playerInteract(LanternPlayer player, int entityId, @Nullable Vector3d position) {
        playerUseEntity(player, entityId, entityProtocol -> entityProtocol.playerInteract(player, entityId, position));
    }

    public void playerAttack(LanternPlayer player, int entityId) {
        playerUseEntity(player, entityId, entityProtocol -> entityProtocol.playerAttack(player, entityId));
    }

    private void playerUseEntity(LanternPlayer player, int entityId,
            Consumer<AbstractEntityProtocol<?>> entityProtocolConsumer) {
        getEntityProtocolById(entityId).ifPresent(entityProtocol -> {
            synchronized (entityProtocol.playerInteractTimes) {
                final long time = entityProtocol.playerInteractTimes.getLong(player);
                final long current = System.currentTimeMillis();
                if (time == 0L || current - time > INTERACT_DELAY) {
                    entityProtocolConsumer.accept(entityProtocol);
                    entityProtocol.playerInteractTimes.put(player, current);
                }
            }
        });
    }

    public void triggerEvent(LanternEntity entity, EntityEvent event) {
        getEntityProtocolByEntity(entity).ifPresent(entityProtocol -> {
            synchronized (entityProtocol.entityEvents) {
                entityProtocol.entityEvents.add(event);
            }
        });
    }
}
