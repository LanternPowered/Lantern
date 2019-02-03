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
package org.lanternpowered.server.world.chunk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.chunk.ChunkTicketManager.PlayerEntityLoadingTicket;

import java.util.UUID;

import javax.annotation.Nullable;

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
