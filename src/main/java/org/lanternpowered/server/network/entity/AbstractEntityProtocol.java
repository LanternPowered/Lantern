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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.network.entity.EntityProtocolManager.INVALID_ENTITY_ID;

import com.flowpowered.math.vector.Vector3d;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.entity.event.EntityEvent;
import org.lanternpowered.server.entity.event.EntityEventType;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.Nullable;

public abstract class AbstractEntityProtocol<E extends LanternEntity> {

    @SuppressWarnings("NullableProblems") EntityProtocolManager entityProtocolManager;

    /**
     * All the players tracking this entity.
     */
    final Set<LanternPlayer> trackers = new HashSet<>();

    /**
     * The entity that is being tracked.
     */
    protected final E entity;

    /**
     * The entity id of the entity.
     */
    private int entityId = INVALID_ENTITY_ID;

    /**
     * The amount of ticks between every update.
     */
    private int tickRate = 4;

    /**
     * The tracking range of the entity.
     */
    private double trackingRange = 64;

    private int tickCounter = 0;

    final Object2LongMap<Player> playerInteractTimes = new Object2LongOpenHashMap<>();

    final List<EntityEvent> entityEvents = new ArrayList<>();

    protected AbstractEntityProtocol(E entity) {
        this.entity = entity;
    }

    final class SimpleEntityProtocolContext implements EntityProtocolUpdateContext {

        @SuppressWarnings("NullableProblems")
        private Set<LanternPlayer> trackers;

        @Override
        public Optional<LanternEntity> getById(int entityId) {
            return entityProtocolManager.getEntityProtocolById(entityId).map(AbstractEntityProtocol::getEntity);
        }

        @Override
        public OptionalInt getId(Entity entity) {
            checkNotNull(entity, "entity");
            final Optional<AbstractEntityProtocol<?>> optProtocol = entityProtocolManager.getEntityProtocolByEntity(entity);
            return optProtocol.map(protocol -> OptionalInt.of(protocol.entityId)).orElseGet(OptionalInt::empty);
        }

        @Override
        public void sendToSelf(Message message) {
            if (entity instanceof Player) {
                ((LanternPlayer) entity).getConnection().send(message);
            }
        }

        @Override
        public void sendToSelf(Supplier<Message> messageSupplier) {
            if (entity instanceof Player) {
                sendToSelf(messageSupplier.get());
            }
        }

        @Override
        public void sendToAll(Message message) {
            this.trackers.forEach(tracker -> tracker.getConnection().send(message));
        }

        @Override
        public void sendToAll(Supplier<Message> message) {
            if (!this.trackers.isEmpty()) {
                sendToAll(message.get());
            }
        }

        @Override
        public void sendToAllExceptSelf(Message message) {
            this.trackers.forEach(tracker -> {
                if (tracker != entity) {
                    tracker.getConnection().send(message);
                }
            });
        }

        @Override
        public void sendToAllExceptSelf(Supplier<Message> messageSupplier) {
            if (!this.trackers.isEmpty()) {
                sendToAllExceptSelf(messageSupplier.get());
            }
        }
    }

    private SimpleEntityProtocolContext newContext() {
        return new SimpleEntityProtocolContext();
    }

    public E getEntity() {
        return this.entity;
    }

    protected int getRootEntityId() {
        return this.entityId;
    }

    /**
     * Sets the tick rate of this entity protocol.
     *
     * @param tickRate The tick rate
     */
    public void setTickRate(int tickRate) {
        this.tickRate = tickRate;
    }

    /**
     * Gets the tick rate of this entity protocol.
     *
     * @return The tick rate
     */
    public int getTickRate() {
        return this.tickRate;
    }

    /**
     * Gets the tracking range of the entity.
     *
     * @return The tracking range
     */
    public double getTrackingRange() {
        return this.trackingRange;
    }

    /**
     * Sets the tracking range of the entity.
     *
     * @param trackingRange The tracking range
     */
    public void setTrackingRange(double trackingRange) {
        this.trackingRange = trackingRange;
    }

    /**
     * Destroys the entity. This removes all the trackers and sends a destroy
     * message to the client.
     *
     * @param context The entity protocol context
     */
    void destroy(EntityProtocolInitContext context) {
        if (!this.trackers.isEmpty()) {
            // Destroy the entity on all the clients
            final SimpleEntityProtocolContext ctx = newContext();
            final TempEvents events = processEvents(true, true);
            ctx.trackers = this.trackers;
            if (events != null && events.deathOrAlive != null) {
                events.deathOrAlive.forEach(event -> handleEvent(ctx, event));
            }
            destroy(ctx);
            this.trackers.clear();
            synchronized (this.playerInteractTimes) {
                this.playerInteractTimes.clear();
            }
        }
        remove(context);
    }

    protected void remove(EntityProtocolInitContext context) {
        // Release the entity id of the entity
        if (!(this.entity instanceof NetworkIdHolder)) {
            context.release(this.entityId);
        }
        this.entityId = INVALID_ENTITY_ID;
    }

    /**
     * Initializes this entity protocol. This acquires the ids
     * that are required to spawn the entity.
     *
     * @param context The entity protocol context
     */
    protected void init(EntityProtocolInitContext context) {
        if (this.entity instanceof NetworkIdHolder) {
            initRootId(((NetworkIdHolder) this.entity).getNetworkId());
        } else {
            // Allocate the next free id
            initRootId(context.acquire());
        }
    }

    /**
     * Initializes the root entity id of this protocol.
     *
     * @param rootEntityId The root entity id
     */
    protected final void initRootId(int rootEntityId) {
        checkArgument(rootEntityId != INVALID_ENTITY_ID, "The root entity id cannot be invalid.");
        checkState(this.entityId == INVALID_ENTITY_ID, "This entity protocol is already initialized.");
        this.entityId = rootEntityId;
    }

    final class TrackerUpdateContextData {

        final AbstractEntityProtocol<?> entityProtocol;
        final SimpleEntityProtocolContext ctx = newContext();

        @Nullable Set<LanternPlayer> added;
        @Nullable Set<LanternPlayer> removed;
        @Nullable Set<LanternPlayer> update;

        TrackerUpdateContextData(AbstractEntityProtocol<?> entityProtocol) {
            this.entityProtocol = entityProtocol;
        }
    }

    @Nullable
    TrackerUpdateContextData buildUpdateContextData(Set<LanternPlayer> players) {
        players = new HashSet<>(players);

        final Set<LanternPlayer> removed = new HashSet<>();
        final Set<LanternPlayer> added = new HashSet<>();

        final Vector3d pos = this.entity.getPosition();

        final Iterator<LanternPlayer> trackerIt = this.trackers.iterator();
        while (trackerIt.hasNext()) {
            final LanternPlayer tracker = trackerIt.next();
            final boolean flag = players.remove(tracker);
            if (tracker != this.entity &&
                    (!flag || !isVisible(pos, tracker))) {
                trackerIt.remove();
                removed.add(tracker);
            }
        }

        for (LanternPlayer tracker : players) {
            if (tracker == this.entity || isVisible(pos, tracker)) {
                added.add(tracker);
            }
        }

        boolean flag0 = this.tickCounter++ % this.tickRate == 0 && !this.trackers.isEmpty();
        boolean flag1 = !added.isEmpty();
        boolean flag2 = !removed.isEmpty();

        if (!flag0 && !flag1 && !flag2) {
            return null;
        }

        final TrackerUpdateContextData contextData = new TrackerUpdateContextData(this);
        if (flag0 || flag1) {
            contextData.update = new HashSet<>(this.trackers);
        }
        if (flag1) {
            contextData.added = added;
            this.trackers.addAll(added);
        }
        if (flag2) {
            contextData.removed = removed;
        }
        return contextData;
    }

    void updateTrackers(TrackerUpdateContextData contextData) {
        final SimpleEntityProtocolContext ctx = contextData.ctx;
        final TempEvents events = processEvents(contextData.removed != null, true);
        if (contextData.removed != null) {
            ctx.trackers = contextData.removed;
            if (events != null && events.deathOrAlive != null) {
                events.deathOrAlive.forEach(event -> handleEvent(ctx, event));
            }
            destroy(ctx);
            synchronized (this.playerInteractTimes) {
                contextData.removed.forEach(this.playerInteractTimes::removeLong);
            }
        }
        Set<LanternPlayer> trackers = null;
        if (contextData.update != null) {
            ctx.trackers = contextData.update;
            update(ctx);
            if (events != null) {
                trackers = contextData.added == null ? contextData.update : new HashSet<>(contextData.update);
            }
        }
        if (contextData.added != null) {
            ctx.trackers = contextData.added;
            spawn(ctx);
            if (events != null) {
                if (trackers == null) {
                    trackers = contextData.added;
                } else {
                    trackers.addAll(contextData.added);
                }
            }
        }
        if (trackers != null) {
            ctx.trackers = trackers;
            events.alive.forEach(event -> handleEvent(ctx, event));
        }
    }

    void updateTrackerLocale(LanternPlayer player) {
        if (!this.trackers.contains(player)) {
            return;
        }
        final SimpleEntityProtocolContext ctx = newContext();
        ctx.trackers = Collections.singleton(player);
        updateTranslations(ctx);
    }

    private final class TempEvents {

        @Nullable private final List<EntityEvent> deathOrAlive;
        private final List<EntityEvent> alive;

        private TempEvents(@Nullable List<EntityEvent> deathOrAlive, List<EntityEvent> alive) {
            this.deathOrAlive = deathOrAlive;
            this.alive = alive;
        }
    }

    @Nullable
    private TempEvents processEvents(boolean death, boolean alive) {
        if (!death && !alive) {
            return null;
        }
        List<EntityEvent> aliveList = null;
        synchronized (this.entityEvents) {
            if (!this.entityEvents.isEmpty()) {
                aliveList = new ArrayList<>(this.entityEvents);
                this.entityEvents.clear();
            }
        }
        if (aliveList == null) {
            return null;
        }
        List<EntityEvent> deathOrAliveList = null;
        if (death) {
            for (EntityEvent event : aliveList) {
                if (event.type() == EntityEventType.DEATH_OR_ALIVE) {
                    if (deathOrAliveList == null) {
                        deathOrAliveList = new ArrayList<>();
                    }
                    deathOrAliveList.add(event);
                }
            }
        }
        return new TempEvents(deathOrAliveList, aliveList);
    }

    void postUpdateTrackers(TrackerUpdateContextData contextData) {
        final SimpleEntityProtocolContext ctx = contextData.ctx;
        if (contextData.update != null) {
            ctx.trackers = contextData.update;
            postUpdate(ctx);
        }
        if (contextData.added != null) {
            ctx.trackers = contextData.added;
            postSpawn(ctx);
        }
    }

    private boolean isVisible(Vector3d pos, LanternPlayer tracker) {
        return pos.distanceSquared(tracker.getPosition()) < this.trackingRange * this.trackingRange && isVisible(tracker);
    }

    /**
     * Gets whether the tracked entity is visible for the tracker.
     *
     * @param tracker The tracker
     * @return Whether the tracker can see the entity
     */
    protected boolean isVisible(LanternPlayer tracker) {
        return tracker.canSee(this.entity);
    }

    /**
     * Spawns the tracked entity.
     *
     * @param context The entity update context
     */
    protected abstract void spawn(EntityProtocolUpdateContext context);

    /**
     * Destroys the tracked entity.
     *
     * @param context The entity update context
     */
    protected abstract void destroy(EntityProtocolUpdateContext context);

    /**
     * Updates the tracked entity.
     *
     * @param context The entity update context
     */
    protected abstract void update(EntityProtocolUpdateContext context);

    /**
     * Updates the tracked entity for {@link Locale} changes.
     *
     * @param context The context
     */
    protected abstract void updateTranslations(EntityProtocolUpdateContext context);

    protected void handleEvent(EntityProtocolUpdateContext context, EntityEvent event) {
    }

    /**
     * Post spawns the tracked entity. This method will be called after
     * all the entities that were pending for updates/spawns are processed.
     *
     * @param context The entity update context
     */
    protected void postSpawn(EntityProtocolUpdateContext context) {
    }

    /**
     * Post updates the tracked entity. This method will be called after
     * all the entities that were pending for updates/spawns are processed.
     *
     * @param context The entity update context
     */
    protected void postUpdate(EntityProtocolUpdateContext context) {
    }

    /**
     * Is called when the specified {@link LanternPlayer} tries to interact
     * with this entity, or at least one of the ids assigned to it.
     *
     * @param player The player that interacted with the entity
     * @param entityId The entity the player interacted with
     * @param position The position where the player interacted with the entity, if present
     */
    protected void playerInteract(LanternPlayer player, int entityId, @Nullable Vector3d position) {
    }

    /**
     * Is called when the specified {@link LanternPlayer} tries to attach
     * this entity, or at least one of the ids assigned to it.
     *
     * @param player The player that attack the entity
     * @param entityId The entity id the player attacked
     */
    protected void playerAttack(LanternPlayer player, int entityId) {
    }
}
