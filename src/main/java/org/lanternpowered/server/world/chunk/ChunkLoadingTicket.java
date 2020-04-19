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

import org.spongepowered.api.world.chunk.ChunkTicketManager.LoadingTicket;
import org.spongepowered.math.vector.Vector2i;

public interface ChunkLoadingTicket extends LoadingTicket {

    /**
     * Force-loads a chunk using this ticket. If the configured concurrently
     * loaded chunk limit is reached, the oldest loaded chunk will be
     * removed.
     *
     * <p>This does not cause an immediate load of the chunk. Forced chunks
     * will be loaded eventually, but may not be available for a few ticks.
     * Forced chunk loading is equivalent to the loading caused by a
     * player.</p>
     *
     * @param chunk The chunk to force-load
     * @return Whether the chunk was added
     */
    boolean forceChunk(Vector2i chunk);

    /**
     * Removes a chunk from the force-loaded set of this ticket.
     *
     * @param chunk The chunk to remove from force-loading
     * @return Whether the chunk was removed
     */
    boolean unforceChunk(Vector2i chunk);

    /**
     * Removes all the chunks from the force-loaded set of this ticket.
     */
    void unforceChunks();

    /**
     * Whether this chunk is released.
     *
     * @return is released
     */
    boolean isReleased();

}
