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
/*
 * Copyright (c) 2011-2014 Glowstone - Tad Hardesty
 * Copyright (c) 2010-2011 Lightstone - Graham Edgecombe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.data.io;

import org.lanternpowered.server.world.chunk.LanternChunk;
import org.spongepowered.api.world.storage.WorldStorage;
import org.spongepowered.math.vector.Vector3i;

import java.io.IOException;

public interface ChunkIOService extends WorldStorage {

    /**
     * Reads a single chunk. The provided chunk must
     * not yet be initialized.
     * 
     * @param chunk The chunk to read into
     * @throws IOException If an i/o error occurs
     */
    boolean read(LanternChunk chunk) throws IOException;

    /**
     * Writes a single chunk.
     * 
     * @param chunk The chunk to write from
     * @throws IOException If an i/o error occurs
     */
    void write(LanternChunk chunk) throws IOException;

    /**
     * Unload the service, performing any cleanup necessary.
     * 
     * @throws IOException If an i/o error occurs
     */
    void unload() throws IOException;

    boolean exists(int x, int z) throws IOException;

    default boolean exists(Vector3i chunkCoords) throws IOException {
        return exists(chunkCoords.getX(), chunkCoords.getZ());
    }
}
