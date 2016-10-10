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
package org.lanternpowered.server.network.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.util.IdAllocator;
import org.spongepowered.api.entity.Entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public final class EntityProtocolManager {

    private static final int UPDATE_RATE = 3;

    /**
     * The {@link IdAllocator} of this entity protocol manager.
     */
    private static final IdAllocator idAllocator = new IdAllocator();

    /**
     * Gets the {@link IdAllocator} that is used to allocate
     * entity ids.
     *
     * @return The id allocator
     */
    public static IdAllocator getEntityIdAllocator() {
        return idAllocator;
    }

    private final Map<Entity, AbstractEntityProtocol<?>> entityProtocols = new ConcurrentHashMap<>();

    /**
     * All the {@link AbstractEntityProtocol}s that will be destroyed.
     */
    private final Queue<AbstractEntityProtocol<?>> queuedForRemoval = new ConcurrentLinkedDeque<>();

    /**
     * The {@link EntityProtocolInitContext}.
     */
    private final EntityProtocolInitContext initContext = () -> idAllocator;

    private int pulseCounter;

    /**
     * Adds the {@link Entity} to be tracked.
     *
     * @param entity The entity
     */
    public void add(LanternEntity entity) {
        //noinspection ConstantConditions,unchecked
        this.add(entity, (EntityProtocolType) entity.getEntityProtocolType());
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
        final AbstractEntityProtocol<?> removed = this.entityProtocols.put(entity, entityProtocol);
        if (removed != null) {
            this.queuedForRemoval.add(removed);
        }
        entityProtocol.init(this.initContext);
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
            removed.destroy(this.initContext);
        }

        final List<AbstractEntityProtocol.TrackerUpdateContextData> updateContextDataList = new ArrayList<>();

        final Set<AbstractEntityProtocol<?>> protocols = new HashSet<>(this.entityProtocols.values());
        for (AbstractEntityProtocol<?> protocol : protocols) {
            final AbstractEntityProtocol.TrackerUpdateContextData contextData = protocol.buildUpdateContextData(players);
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
}
