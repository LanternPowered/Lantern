/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
