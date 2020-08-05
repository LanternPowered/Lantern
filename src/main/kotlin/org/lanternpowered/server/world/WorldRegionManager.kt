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
package org.lanternpowered.server.world

import org.lanternpowered.api.util.math.toBlockPosition
import org.lanternpowered.api.world.World
import org.lanternpowered.api.world.chunk.ChunkPosition
import org.lanternpowered.server.entity.player.LanternPlayer
import org.lanternpowered.server.world.chunk.ChunkPositionSet
import org.lanternpowered.server.world.chunk.MergedChunkPositionCollection

// TODO: Move to the player
/**
 * Gets the coordinates of the chunks that
 * will be viewed by the player.
 */
fun LanternPlayer.getViewedChunks(): ChunkPositionSet {
    val radius = this.actualViewDistance
    val position = this.position.toBlockPosition().chunkPosition

    val minX = position.x - radius
    val maxX = position.x + radius
    val minZ = position.z - radius
    val maxZ = position.z + radius

    val set = ChunkPositionSet()
    for (x in minX..maxX) {
        for (z in minZ..maxZ) {
            set += ChunkPosition(x, z)
        }
    }
    return set
}

/**
 * Manages all the [WorldRegion]s of a [World].
 */
class WorldRegionManager(private val world: World) {

    private val queueLock = Any()
    private val queuedToRemove = mutableSetOf<LanternChunk>()
    private val queuedToAdd = mutableSetOf<LanternChunk>()

    private val regions = mutableSetOf<WorldRegion>()

    // TODO: Track which chunks are tracked by a player, those should
    //       also be joined into the same region.

    /**
     * Adds a new chunk, is called when a chunk gets loaded.
     *
     * The chunk will be queued to be added a region in the next update
     * cycle, that's when the new chunk regions will be determined, if
     * they need any updates.
     *
     * @param chunk The chunk
     */
    fun addChunk(chunk: LanternChunk) {
        synchronized(this.queueLock) {
            this.queuedToAdd.add(chunk)
        }
    }

    /**
     * Queues a chunk to be removed at the next cycle. Usually called when
     * a chunk is requested to be unloaded, which needs to wait until the next
     * cycle.
     */
    fun removeChunk(chunk: LanternChunk) {
        synchronized(this.queueLock) {
            this.queuedToRemove.remove(chunk)
        }
    }

    /**
     * Recalculates the regions.
     */
    private fun recalculate() {
        val players = this.world.players as Collection<LanternPlayer>

        // Calculate overlapping areas based on the chunks that are viewed
        // by players, then create a set that merges all the sets. If player
        // chunk regions overlap, they must be already be merged, even before
        // the chunks are loaded.
        var lastSet: ChunkPositionSet? = null
        var group = 0
        val playerGroupSets = players
                .map { player -> player.getViewedChunks() }
                .sortedWith(Comparator { set1, set2 ->
                    if (set1.containsAny(set2)) -1 else 1
                })
                // Group by different overlapping areas, by sorting should all
                // overlapping areas be next to each other, so we check when they
                // don't overlap and that's when a new group starts
                .groupBy { set ->
                    try {
                        if (lastSet != null && !lastSet!!.containsAny(set)) ++group else group
                    } finally {
                        lastSet = set
                    }
                }
                // Merge the entries
                .map { (_, entries) -> MergedChunkPositionCollection(entries) }

        val queuedToAdd: Set<LanternChunk>
        val queuedToRemove: Set<LanternChunk>
        synchronized(this.queueLock) {
            queuedToAdd = this.queuedToAdd.toSet()
            this.queuedToAdd.clear()
            queuedToRemove = this.queuedToRemove.toSet()
            this.queuedToRemove.clear()
        }
        for (removed in queuedToRemove) {
            val region = removed.region!!
            region.remove(removed)
            if (region.chunks.isEmpty())
                this.regions.remove(region)
        }
        // TODO: Can this be optimized by ordering?
        for (added in queuedToAdd) {
            // Check if there already exists a region we can just add a chunk
            val existingRegion = this.regions.firstOrNull { it.hasNeighbor(added.position) }
            if (existingRegion != null) {
                existingRegion.add(added)
            } else {
                // Otherwise, create a new region
                val region = WorldRegion()
                region.add(added)
                this.regions += region
            }
        }
    }

    /**
     * Trigger updates across all the chunk regions. These update tasks
     * will be posted to a thread pool. This thread pool will be common
     * with other worlds, and each update cycle will be common with other
     * worlds, to keep them in sync and don't give a world priority over
     * an other one.
     *
     * Each world will get a certain amount of threads to start with to
     * execute its region update tasks, if another world finishes its
     * updates, the threads will become available to the other worlds.
     *
     * Once all the worlds have finished updating, then a new update cycle
     * will start and the process will be repeated.
     */
    fun update(collector: (() -> Unit) -> Unit) {
        recalculate()

        // Queue region tasks, bound to the world
        for (region in this.regions) {
            collector {
                region.update()
            }
        }
    }
}
