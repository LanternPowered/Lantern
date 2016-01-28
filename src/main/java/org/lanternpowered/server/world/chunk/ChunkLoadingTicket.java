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
package org.lanternpowered.server.world.chunk;

import com.flowpowered.math.vector.Vector2i;
import org.spongepowered.api.world.ChunkTicketManager.LoadingTicket;

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
     */
    void forceChunk(Vector2i chunk);

    /**
     * Removes a chunk from the force-loaded set of this ticket.
     *
     * @param chunk The chunk to remove from force-loading
     */
    void unforceChunk(Vector2i chunk);

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
