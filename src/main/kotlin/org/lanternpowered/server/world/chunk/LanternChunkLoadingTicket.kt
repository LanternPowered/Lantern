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

import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import org.lanternpowered.api.util.collections.forEachLong
import org.lanternpowered.api.util.collections.toImmutableSet
import org.lanternpowered.api.world.World
import org.lanternpowered.api.world.chunk.ChunkLoadingTicket
import org.lanternpowered.api.world.chunk.ChunkPosition

class LanternChunkLoadingTicket(private val chunkManager: ChunkManager) : ChunkLoadingTicket {

    private val set = LongOpenHashSet()

    private inline fun <R> withLock(fn: () -> R): R = synchronized(this.set, fn)

    override val world: World
        get() = this.chunkManager.world

    override val chunks: Set<ChunkPosition>
        get() = this.withLock { this.set.asSequence().map { ChunkPosition(it) }.toImmutableSet() }

    override val isEmpty: Boolean
        get() = this.withLock { this.set.isEmpty() }

    override fun acquire(position: ChunkPosition): Boolean {
        this.withLock {
            if (this.set.add(position.packed)) {
                this.chunkManager.acquireReference(position)
                if (this.set.size == 1)
                    this.chunkManager.add(this)
                return true
            }
            return false
        }
    }

    private fun release0(position: ChunkPosition): Boolean {
        if (this.set.remove(position.packed)) {
            this.chunkManager.releaseReference(position)
            if (this.set.isEmpty())
                this.chunkManager.remove(this)
            return true
        }
        return false
    }

    override fun release(position: ChunkPosition): Boolean {
        this.withLock {
            return this.release0(position)
        }
    }

    override fun releaseAll(positions: Iterable<ChunkPosition>): Boolean {
        this.withLock {
            var change = false
            if (positions is ChunkPositionCollection) {
                val itr = positions.iterator()
                while (itr.hasNext())
                    change = this.release0(itr.nextPosition()) || change
            } else {
                for (position in positions)
                    change = this.release0(position) || change
            }
            return change
        }
    }

    override fun releaseAll() {
        this.withLock {
            this.set.forEachLong { packed ->
                val position = ChunkPosition(packed)
                this.chunkManager.releaseReference(position)
            }
            this.set.clear()
            this.chunkManager.remove(this)
        }
    }
}
