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

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.plugin.InternalPluginsInfo;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.world.World;
import org.spongepowered.math.vector.Vector2i;
import org.spongepowered.math.vector.Vector3i;

final class InternalLoadingTicket implements ChunkLoadingTicket {

    private static final ImmutableSet<Vector3i> CHUNK_LIST = ImmutableSet.of();

    @Override
    public boolean setNumChunks(int numChunks) {
        return false;
    }

    @Override
    public int getNumChunks() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxNumChunks() {
        return Integer.MAX_VALUE;
    }

    @Override
    public World getWorld() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataContainer getCompanionData() {
        return DataContainer.createNew();
    }

    @Override
    public void setCompanionData(DataContainer container) {
    }

    @Override
    public String getPlugin() {
        return InternalPluginsInfo.Minecraft.IDENTIFIER;
    }

    @Override
    public ImmutableSet<Vector3i> getChunkList() {
        return CHUNK_LIST;
    }

    @Override
    public void forceChunk(Vector3i chunk) {
    }

    @Override
    public void unforceChunk(Vector3i chunk) {
    }

    @Override
    public void prioritizeChunk(Vector3i chunk) {
    }

    @Override
    public void release() {
    }

    @Override
    public boolean forceChunk(Vector2i chunk) {
        return false;
    }

    @Override
    public boolean unforceChunk(Vector2i chunk) {
        return false;
    }

    @Override
    public void unforceChunks() {
    }

    @Override
    public boolean isReleased() {
        return false;
    }
}
