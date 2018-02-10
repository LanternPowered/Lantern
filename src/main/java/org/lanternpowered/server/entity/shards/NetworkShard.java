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
package org.lanternpowered.server.entity.shards;

import com.google.inject.Inject;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.entity.event.CollectEntityShardevent;
import org.lanternpowered.server.entity.event.EntityDamagedShardevent;
import org.lanternpowered.server.entity.event.EntityWorldShardevent;
import org.lanternpowered.server.entity.event.LoveModeEntityShardvent;
import org.lanternpowered.server.entity.event.RequestPlayerAbilitiesRefreshShardevent;
import org.lanternpowered.server.entity.event.SpectateEntityShardevent;
import org.lanternpowered.server.entity.event.SwingHandEntityShardevent;
import org.lanternpowered.server.network.entity.AbstractEntityProtocol;
import org.lanternpowered.server.network.entity.EntityProtocolShardeventType;
import org.lanternpowered.server.network.entity.EntityProtocolType;
import org.lanternpowered.server.network.entity.EntityProtocolTypes;
import org.lanternpowered.server.shards.Holder;
import org.lanternpowered.server.shards.OnAttach;
import org.lanternpowered.server.shards.OnDetach;
import org.lanternpowered.server.shards.Shard;
import org.lanternpowered.server.shards.event.Shardevent;
import org.lanternpowered.server.shards.event.ShardeventBus;
import org.lanternpowered.server.shards.event.ShardeventListener;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.util.Tuple;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A {@link Shard} that can be attached to a {@link Entity} to
 * give it the possibility to be visible/rendered on the client. Without
 * this component will be client never know about the {@link Entity}.
 */
@SuppressWarnings("unchecked")
@ConfigSerializable
public final class NetworkShard extends Shard {

    /**
     * A list with all the {@link Shardevent} that should be delegated to the {@link AbstractEntityProtocol}.
     */
    private static final List<Tuple<Class<? extends Shardevent>, EntityProtocolShardeventType>> delegatedEvents = Arrays.asList(
            // Used to play damage animations
            new Tuple<>(EntityDamagedShardevent.class, EntityProtocolShardeventType.ALIVE),
            // Used to request player ability updates
            new Tuple<>(RequestPlayerAbilitiesRefreshShardevent.class, EntityProtocolShardeventType.DEATH_OR_ALIVE),
            // Used to make the entity being collected (picked up)
            new Tuple<>(CollectEntityShardevent.class, EntityProtocolShardeventType.DEATH_OR_ALIVE),
            // Used when a player spectates a specific entity or stops spectating
            new Tuple<>(SpectateEntityShardevent.class, EntityProtocolShardeventType.ALIVE),
            // Used to play arm swing animations
            new Tuple<>(SwingHandEntityShardevent.class, EntityProtocolShardeventType.ALIVE),
            // Used to play love particles
            new Tuple<>(LoveModeEntityShardvent.class, EntityProtocolShardeventType.ALIVE)
    );

    @Inject @Holder private LanternEntity holder;

    @Setting(value = "type", comment = "The entity protocol type that should be used for the entity.")
    private EntityProtocolType entityProtocolType = EntityProtocolTypes.CHICKEN; // Chickens by default? Why not...

    @Setting(value = "tracking-range", comment = "The tracking range that the entity is visible to players.")
    private double trackingRange = 64;

    @Setting(value = "tracking-update-rate", comment = "The tracking update rate is the amount of ticks between each protocol update.")
    private int trackingUpdateRate = 4;

    @Nullable private AbstractEntityProtocol entityProtocol;

    public NetworkShard() {
    }

    public NetworkShard(EntityProtocolType<?> entityProtocolType,
            int trackingRange, int trackingUpdateRate) {
        this.entityProtocolType = entityProtocolType;
        this.trackingUpdateRate = trackingUpdateRate;
        this.trackingRange = trackingRange;
    }

    /**
     * Gets the {@link EntityProtocolType}.
     *
     * @return The entity protocol type
     */
    public EntityProtocolType<?> getEntityProtocolType() {
        return this.entityProtocolType;
    }

    /**
     * Sets the {@link EntityProtocolType}. Will cause
     * the {@link LanternEntity} to respawn on the client.
     *
     * @param entityProtocolType The entity protocol type
     */
    public void setEntityProtocolType(EntityProtocolType entityProtocolType) {
        this.entityProtocolType = entityProtocolType;
        // The entity was already present in a world when the component got attached
        if (this.holder.existsInWorld()) {
            // Respawn the entity on the client, the old entry will be cleaned up
            this.entityProtocol = this.holder.getWorld().getEntityProtocolManager().add(this.holder, this);
        }
    }

    /**
     * Gets the tracking range.
     *
     * @return The tracking range
     */
    public double getTrackingRange() {
        return this.trackingRange;
    }

    /**
     * Sets the tracking range.
     *
     * @param trackingRange The tracking range
     */
    public void setTrackingRange(double trackingRange) {
        this.trackingRange = trackingRange;
    }

    /**
     * Gets the tracking update rate.
     *
     * @return The tracking update rate
     */
    public int getTrackingUpdateRate() {
        return this.trackingUpdateRate;
    }

    /**
     * Sets the tracking update rate.
     *
     * @param trackingUpdateRate The tracking update rate
     */
    public void setTrackingUpdateRate(int trackingUpdateRate) {
        this.trackingUpdateRate = trackingUpdateRate;
    }

    @OnAttach
    private void onAttach() {
        final ShardeventBus shardeventBus = this.holder.getShardeventBus();
        // Delegate the events that are used in protocol
        delegatedEvents.forEach(entry -> shardeventBus.register(entry.getFirst(), event -> {
            if (this.entityProtocol != null) {
                this.entityProtocol.addEvent(event, entry.getSecond());
            }
        }));
        // The entity was already present in a world when the component got attached
        if (this.holder.existsInWorld()) {
            // Spawn the entity on the client
            this.entityProtocol = this.holder.getWorld().getEntityProtocolManager().add(this.holder, this);
        }
    }

    @OnDetach
    private void onDetach() {
        // The entity was already present in a world when the component got detached
        if (this.holder.existsInWorld()) {
            // Remove the entity from the client
            this.holder.getWorld().getEntityProtocolManager().remove(this.holder);
            // The entity is no more
            this.entityProtocol = null;
        }
    }

    @ShardeventListener
    private void onJoinWorld(EntityWorldShardevent.Join event) {
        // Spawn the entity on the client
        this.entityProtocol = event.getWorld().getEntityProtocolManager().add(this.holder, this);
    }

    @ShardeventListener
    private void onLeaveWorld(EntityWorldShardevent.Leave event) {
        // Remove the entity on the client
        event.getWorld().getEntityProtocolManager().remove(this.holder);
        // The entity is no more
        this.entityProtocol = null;
    }
}
