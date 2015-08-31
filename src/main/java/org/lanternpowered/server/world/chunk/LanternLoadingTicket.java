package org.lanternpowered.server.world.chunk;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.spongepowered.api.service.world.ChunkLoadService.LoadingTicket;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;

class LanternLoadingTicket implements LoadingTicket {

    private final ConcurrentLinkedQueue<Vector2i> queue = new ConcurrentLinkedQueue<Vector2i>();
    private final LanternChunkManager chunkManager;
    private final String plugin;

    // The maximum amount of chunks that can be loaded by this ticket
    private final int maxChunks;

    // The amount of chunks that may be loaded by this ticket
    private volatile int numChunks;

    public LanternLoadingTicket(String plugin, LanternChunkManager chunkManager, int maxChunks) {
        this.chunkManager = chunkManager;
        this.maxChunks = maxChunks;
        this.plugin = plugin;
    }

    /**
     * Gets whether there no entries are inside the ticket.
     * 
     * @return is empty
     */
    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    @Override
    public boolean setNumChunks(int numChunks) {
        checkArgument(numChunks >= 0, "numChunks may not be negative");
        if (numChunks > this.maxChunks) {
            return false;
        }
        // Remove the oldest chunks that cannot be loaded anymore
        if (numChunks < this.numChunks) {
            int size = this.queue.size();

            if (numChunks < size) {
                for (int i = 0; i < size - numChunks; i++) {
                    this.chunkManager.release(this, this.queue.poll());
                }
            }
        }
        this.numChunks = numChunks;
        return true;
    }

    @Override
    public int getNumChunks() {
        return this.numChunks;
    }

    @Override
    public int getMaxNumChunks() {
        return this.maxChunks;
    }

    @Override
    public String getPlugin() {
        return this.plugin;
    }

    @Override
    public ImmutableSet<Vector3i> getChunkList() {
        return ImmutableSet.copyOf(Collections2.transform(this.queue, new Function<Vector2i, Vector3i>() {
            @Override
            public Vector3i apply(Vector2i input) {
                return LanternChunk.fromVector2(input);
            }
        }));
    }

    @Override
    public void forceChunk(Vector3i chunk) {
        Vector2i chunk0 = checkNotNull(chunk, "chunk").toVector2(true);
        // Only force if not done before
        if (!this.queue.contains(chunk0)) {
            // Remove the oldest chunk if necessary
            if (this.queue.size() >= this.numChunks) {
                this.chunkManager.release(this, this.queue.poll());
            }
            this.queue.add(chunk0);
            this.chunkManager.force(this, chunk0);
        }
    }

    @Override
    public void unforceChunk(Vector3i chunk) {
        Vector2i chunk0 = checkNotNull(chunk, "chunk").toVector2(true);
        if (this.queue.remove(chunk0)) {
            this.chunkManager.release(this, chunk0);
        }
    }

    @Override
    public void prioritizeChunk(Vector3i chunk) {
        Vector2i chunk0 = checkNotNull(chunk, "chunk").toVector2(true);
        // Move the chunk to the bottom of the queue if found
        if (this.queue.remove(chunk0)) {
            this.queue.add(chunk0);
        }
    }

    @Override
    public void release() {
        while (!this.queue.isEmpty()) {
            this.chunkManager.release(this, this.queue.poll());
        }
    }
}
