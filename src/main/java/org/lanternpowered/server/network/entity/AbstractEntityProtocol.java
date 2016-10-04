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

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Supplier;

public abstract class AbstractEntityProtocol<E extends LanternEntity> {

    /**
     * All the players tracking this entity.
     */
    private final Set<LanternPlayer> trackers = new HashSet<>();

    /**
     * The entity that is being tracked.
     */
    protected final E entity;

    /**
     * The amount of ticks between every update.
     */
    private int tickRate = 4;

    /**
     * The tracking range of the entity.
     */
    private double trackingRange = 64;

    int tickCounter = 0;

    public AbstractEntityProtocol(E entity) {
        this.entity = entity;
    }

    private final class SimpleEntityProtocolContext implements EntityUpdateContext {

        private Set<LanternPlayer> trackers;

        @Override
        public void sendToSelf(Message message) {
            if (entity instanceof Player) {
                ((LanternPlayer) entity).getConnection().send(message);
            }
        }

        @Override
        public void sendToSelf(Supplier<Message> messageSupplier) {
            if (entity instanceof Player) {
                this.sendToSelf(messageSupplier.get());
            }
        }

        @Override
        public void sendToAll(Message message) {
            this.trackers.forEach(tracker -> tracker.getConnection().send(message));
        }

        @Override
        public void sendToAll(Supplier<Message> message) {
            if (!this.trackers.isEmpty()) {
                this.sendToAll(message.get());
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
                this.sendToAllExceptSelf(messageSupplier.get());
            }
        }
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
     */
    public void destroy() {
        if (!this.trackers.isEmpty()) {
            final SimpleEntityProtocolContext ctx = new SimpleEntityProtocolContext();
            ctx.trackers = this.trackers;
            this.destroy(ctx);
            this.trackers.clear();
        }
    }

    /**
     * Post updates the trackers of the entity.
     */
    void postUpdateTrackers() {
    }

    /**
     * Updates the trackers of the entity. The players list contains all the players that
     * are in the same world of the entities.
     *
     * TODO: Or provide players based on the loaded chunks?
     *
     * @param players The players
     */
    void updateTrackers(Set<LanternPlayer> players) {
        players = new HashSet<>(players);

        final Set<LanternPlayer> removed = new HashSet<>();
        final Set<LanternPlayer> added = new HashSet<>();

        final Vector3d pos = this.entity.getPosition();

        final Iterator<LanternPlayer> trackerIt = this.trackers.iterator();
        while (trackerIt.hasNext()) {
            final LanternPlayer tracker = trackerIt.next();
            final boolean flag = players.remove(tracker);
            if (tracker != this.entity &&
                    (!flag || !this.isVisible(pos, tracker))) {
                trackerIt.remove();
                removed.add(tracker);
            }
        }

        for (LanternPlayer tracker : players) {
            if (tracker == this.entity || this.isVisible(pos, tracker)) {
                added.add(tracker);
            }
        }

        boolean flag0 = !this.trackers.isEmpty();
        boolean flag1 = !added.isEmpty();
        boolean flag2 = !removed.isEmpty();

        if (flag0 || flag1 || flag2) {
            final SimpleEntityProtocolContext ctx = new SimpleEntityProtocolContext();

            // Stream updates to players that are already tracking
            // The entity tracker should also be updated when the
            // a entity is being spawned, because all the fields to
            // check the changes should be updated, even if there is
            // not a player to track them yet
            if (flag0 || flag1) {
                ctx.trackers = this.trackers;
                this.update(ctx);
            }

            // Stream spawn messages to the added trackers
            if (flag1) {
                ctx.trackers = added;
                this.spawn(ctx);
                this.trackers.addAll(added);
            }

            // Stream destroy messages to the removed trackers
            if (flag2) {
                ctx.trackers = removed;
                this.destroy(ctx);
            }
        }
    }

    private boolean isVisible(Vector3d pos, LanternPlayer tracker) {
        return pos.distanceSquared(tracker.getPosition()) < this.trackingRange * this.trackingRange && this.isVisible(tracker);
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
    protected abstract void spawn(EntityUpdateContext context);

    /**
     * Destroys the tracked entity.
     *
     * @param context The entity update context
     */
    protected abstract void destroy(EntityUpdateContext context);

    /**
     * Updates the tracked entity.
     *
     * @param context The entity update context
     */
    protected abstract void update(EntityUpdateContext context);

    /**
     * Post updates the tracked entity. This method will be called after
     * all the entities that were pending for updates/spawns are processed.
     *
     * @param context The entity update context
     */
    protected void postUpdate(EntityUpdateContext context) {
    }
}
