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
package org.lanternpowered.server.world.chunk

import org.lanternpowered.api.util.collections.concurrentHashMapOf
import org.lanternpowered.api.util.collections.concurrentHashSetOf
import org.lanternpowered.api.world.World
import org.lanternpowered.api.world.chunk.ChunkLoadingTicket
import org.lanternpowered.api.world.chunk.ChunkPosition
import org.lanternpowered.server.world.LanternChunk
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * Manages all the chunks in a world.
 *
 * @property world The world this manager belongs to
 * @property ioExecutor The executor which is used to handle IO
 */
class ChunkManager(
        val world: World,
        val ioExecutor: ExecutorService
) {

    /**
     * All the chunk loading tickets that are currently
     * allocated and hold references.
     */
    private val tickets = concurrentHashSetOf<LanternChunkLoadingTicket>()

    /**
     * All the chunk entries.
     */
    private val entries = concurrentHashMapOf<Long, ChunkEntry>()

    /**
     * References to either a loaded or unloaded chunk,
     * and all the states in between.
     */
    class ChunkEntry(val position: ChunkPosition) {

        /**
         * A lock for modifications to the chunk entry.
         */
        val lock = ReentrantReadWriteLock()

        /**
         * The number of references that are held by
         * different tickets.
         */
        var references: Int = 0

        /**
         * The current state.
         */
        var state: State = State.Unloaded

        /**
         * Represents the state a chunk entry can be in.
         */
        sealed class State {

            /**
             * The chunk is being loaded. The [future] will be called
             * when the chunk finishes loading.
             *
             * Loading can also involve generation if the chunk didn't
             * exist before.
             */
            class Loading(val future: CompletableFuture<LanternChunk>) : State()

            /**
             * The chunk is loaded.
             */
            class Loaded(val chunk: LanternChunk) : State()

            /**
             * The chunk is unloaded, no data related to the
             * chunk is currently in memory.
             */
            object Unloaded : State()

            /**
             * The chunk is being unloaded. The [future] will be called
             * when the chunk finishes unloading.
             *
             * Unloading also includes saving.
             */
            class Unloading(val future: CompletableFuture<Unit>) : State()

            /**
             * The chunk is being saved, without unloading. The [future]
             * will be called when the chunk finishes loading.
             */
            class Saving(val future: CompletableFuture<Unit>) : State()
        }
    }

    private fun queueLoad(entry: ChunkEntry) {

    }

    private fun queueUnload(entry: ChunkEntry) {

    }

    /**
     * Creates a new chunk loading ticket.
     */
    fun createTicket(): ChunkLoadingTicket =
            LanternChunkLoadingTicket(this)

    /**
     * Adds the ticket.
     */
    fun add(ticket: LanternChunkLoadingTicket) {
        this.tickets.add(ticket)
    }

    /**
     * Removes the ticket.
     */
    fun remove(ticket: LanternChunkLoadingTicket) {
        this.tickets.remove(ticket)
    }

    /**
     * Gets whether there are references for the chunk
     * at the given [ChunkPosition].
     */
    fun hasReference(position: ChunkPosition): Boolean {
        val entry = this.entries[position.packed] ?: return false
        return entry.lock.read { entry.references > 0 }
    }

    /**
     * Acquires a reference to the given chunk at
     * the [ChunkPosition].
     */
    fun acquireReference(position: ChunkPosition) {
        val entry = this.entries.computeIfAbsent(position.packed) { ChunkEntry(position) }
        entry.lock.write {
            entry.references++
            if (entry.references == 1)
                queueLoad(entry)
        }
    }

    /**
     * Releases a reference to the given chunk at
     * the [ChunkPosition].
     */
    fun releaseReference(position: ChunkPosition) {
        val entry = this.entries[position.packed]
                ?: throw IllegalStateException("The entry for $position doesn't exist.")
        entry.lock.write {
            entry.references--
            if (entry.references == 0)
                queueUnload(entry)
        }
    }

    /**
     * Cleanup entries that are no longer relevant. This is the case
     * when the chunk is unloaded and there are no references to try
     * to load it.
     */
    private fun cleanupEntries() {
        val entries = this.entries.toMap()
        for ((key, entry) in entries) {
            entry.lock.write {
                if (entry.state is ChunkEntry.State.Unloaded && entry.references == 0)
                    this.entries.remove(key)
            }
        }
    }

    fun getChunkIfLoaded(position: ChunkPosition): LanternChunk {
        TODO()
    }
}
