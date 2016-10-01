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
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public final class EntityProtocolManager {

    private final Map<Entity, AbstractEntityProtocol<?>> entityProtocols = new ConcurrentHashMap<>();

    /**
     * All the {@link AbstractEntityProtocol}s that will be destroyed.
     */
    private final Queue<AbstractEntityProtocol<?>> queuedForRemoval = new ConcurrentLinkedDeque<>();

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
    public <E extends LanternEntity> void add(E entity,
            EntityProtocolType<E> protocolType) {
        checkNotNull(entity, "entity");
        checkNotNull(protocolType, "protocolType");
        final AbstractEntityProtocol<E> entityProtocol = protocolType.getSupplier().apply(entity);
        final AbstractEntityProtocol<?> removed = this.entityProtocols.put(entity, entityProtocol);
        if (removed != null) {
            this.queuedForRemoval.add(removed);
        } else if (entity.getEntityId() == -1) {
            entity.setEntityId(LanternEntity.getIdAllocator().poll());
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
            // Don't release entity ids allocated for players
            if (!(entity instanceof Player)) {
                LanternEntity.getIdAllocator().push(entity.getEntityId());
                entity.setEntityId(-1);
            }
        }
    }

    /**
     * Updates the trackers of the entities. The players list contains all the players that
     * are in the same world of the entities.
     *
     * @param players The players
     */
    public void updateTrackers(Set<LanternPlayer> players) {
        AbstractEntityProtocol<?> removed;
        while ((removed = this.queuedForRemoval.poll()) != null) {
            removed.destroy();
        }

        final Set<AbstractEntityProtocol<?>> protocols = new HashSet<>(this.entityProtocols.values());
        for (AbstractEntityProtocol<?> protocol : protocols) {
            if (protocol.tickCounter % protocol.getTickRate() == 0) {
                protocol.updateTrackers(players);
            }
        }

        for (AbstractEntityProtocol<?> protocol : protocols) {
            if (protocol.tickCounter++ % protocol.getTickRate() == 0) {
                protocol.postUpdateTrackers();
            }
        }
    }
}
