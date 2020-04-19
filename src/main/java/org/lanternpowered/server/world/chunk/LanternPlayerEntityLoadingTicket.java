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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.chunk.ChunkTicketManager.PlayerEntityLoadingTicket;

import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

class LanternPlayerEntityLoadingTicket extends LanternEntityLoadingTicket implements PlayerEntityLoadingTicket {

    private final UUID uniqueId;

    LanternPlayerEntityLoadingTicket(String plugin, LanternChunkManager chunkManager, UUID uniqueId, int maxChunks) {
        super(plugin, chunkManager, maxChunks);
        this.uniqueId = uniqueId;
    }

    LanternPlayerEntityLoadingTicket(String plugin, LanternChunkManager chunkManager, UUID uniqueId, int maxChunks, int numChunks) {
        super(plugin, chunkManager, maxChunks, numChunks);
        this.uniqueId = uniqueId;
    }

    @Override
    public void bindToEntity(Entity entity) {
        checkNotNull(entity, "entity");
        checkArgument(entity.getUniqueId().equals(this.uniqueId),
                "Only a player with the uuid (" + this.uniqueId + ") can be applied to this ticket!");
        super.bindToEntity(entity);
    }

    @Override
    public UUID getPlayerUniqueId() {
        return this.uniqueId;
    }

    @Nullable
    @Override
    UUID getEntityUniqueId() {
        return this.uniqueId;
    }
}
