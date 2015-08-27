package org.lanternpowered.server.data.io;

import java.io.IOException;

import org.lanternpowered.server.world.chunk.LanternChunk;
import org.spongepowered.api.world.storage.WorldStorage;

public interface ChunkIOService extends WorldStorage {

    /**
     * Reads a single chunk. The provided chunk must
     * not yet be initialized.
     * 
     * @param chunk the chunk to read into
     * @throws IOException if an i/o error occurs
     */
    boolean read(LanternChunk chunk) throws IOException;

    /**
     * Writes a single chunk.
     * 
     * @param chunk the chunk to write from
     * @throws IOException if an i/o error occurs
     */
    void write(LanternChunk chunk) throws IOException;

    /**
     * Unload the service, performing any cleanup necessary.
     * 
     * @throws IOException if an i/o error occurs
     */
    void unload() throws IOException;
}
