package org.lanternpowered.server.world.chunk;

import java.util.UUID;

import javax.annotation.Nullable;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.service.world.ChunkLoadService.EntityLoadingTicket;

import com.flowpowered.math.vector.Vector2i;

class LanternEntityLoadingTicket extends LanternLoadingTicket implements EntityLoadingTicket {

    // The reference of the entity while it's not loaded yet,
    // this field will be cleared once the entity available is
    @Nullable volatile EntityReference entityRef;

    // The entity instance
    @Nullable private volatile Entity entity;

    LanternEntityLoadingTicket(String plugin, LanternChunkManager chunkManager, int maxChunks, int numChunks) {
        super(plugin, chunkManager, maxChunks, numChunks);
    }

    LanternEntityLoadingTicket(String plugin, LanternChunkManager chunkManager, int maxChunks) {
        super(plugin, chunkManager, maxChunks);
    }

    @Override
    public void bindToEntity(Entity entity) {
        this.entity = entity;
    }

    @Nullable
    @Override
    public Entity getBoundEntity() {
        return this.entity;
    }

    /**
     * A reference where the entity is stored in the world,
     * if it's not already loaded.
     */
    static class EntityReference {

        final Vector2i chunkCoords;
        final UUID uniqueId;

        EntityReference(Vector2i chunkCoords, UUID uniqueId) {
            this.chunkCoords = chunkCoords;
            this.uniqueId = uniqueId;
        }
    }
}
