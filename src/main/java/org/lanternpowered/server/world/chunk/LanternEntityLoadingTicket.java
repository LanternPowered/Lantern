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
package org.lanternpowered.server.world.chunk;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.MoreObjects;
import org.spongepowered.api.entity.Entity;

import java.util.Optional;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

class LanternEntityLoadingTicket extends LanternLoadingTicket implements EntityChunkLoadingTicket {

    // The reference of the entity while it's not loaded yet,
    // this field will be cleared once the entity available is
    @Nullable private EntityReference entityReference;

    // The entity instance
    @Nullable private Entity entity;

    LanternEntityLoadingTicket(String plugin, LanternChunkManager chunkManager, int maxChunks, int numChunks) {
        super(plugin, chunkManager, maxChunks, numChunks);
    }

    LanternEntityLoadingTicket(String plugin, LanternChunkManager chunkManager, int maxChunks) {
        super(plugin, chunkManager, maxChunks);
    }

    @Override
    public void bindToEntity(Entity entity) {
        this.setEntity(checkNotNull(entity, "entity"));
    }

    @Override
    public Entity getBoundEntity() {
        synchronized (this.queue) {
            checkState(this.entity != null, "No entity bound to the ticket.");
            return this.entity;
        }
    }

    @Override
    public void setEntity(@Nullable Entity entity) {
        synchronized (this.queue) {
            this.entity = entity;
        }
    }

    @Override
    public Optional<Entity> getEntity() {
        synchronized (this.queue) {
            return Optional.ofNullable(this.entity);
        }
    }

    @Override
    public void setEntityReference(@Nullable EntityReference entityReference) {
        synchronized (this.queue) {
            this.entityReference = entityReference;
        }
    }

    @Override
    public Optional<EntityReference> getEntityReference() {
        synchronized (this.queue) {
            return Optional.ofNullable(this.entityReference);
        }
    }

    @Override
    public Optional<EntityReference> getOrCreateEntityReference() {
        synchronized (this.queue) {
            if (this.entity != null) {
                return Optional.of(new EntityReference(this.entity.getLocation().getChunkPosition().toVector2(true),
                        this.entity.getUniqueId()));
            }
            return Optional.ofNullable(this.entityReference);
        }
    }

    @Nullable
    UUID getEntityUniqueId() {
        synchronized (this.queue) {
            return this.entity != null ? this.entity.getUniqueId() : this.entityReference != null ? this.entityReference.getUniqueId() : null;
        }
    }

    @Override
    MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper().omitNullValues().add("entity", getEntityUniqueId());
    }
}
