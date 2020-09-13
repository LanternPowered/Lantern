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
package org.lanternpowered.server.world.update

import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
import org.lanternpowered.api.world.World
import org.lanternpowered.api.world.chunk.ChunkPosition
import org.lanternpowered.api.world.scheduler.ScheduledUpdate
import org.lanternpowered.api.world.scheduler.UpdatePriority
import org.lanternpowered.server.world.chunk.Chunks

@Suppress("UNCHECKED_CAST")
class ChunkScheduledUpdateList<T : Any>(
        val world: World,
        private val position: ChunkPosition
) : AbstractScheduledUpdateList<T>() {

    private var updateCounter = 0L

    private val updatesLookup = Short2ObjectOpenHashMap<LanternScheduledUpdate<T>>()
    private val sortedUpdates = ObjectRBTreeSet<LanternScheduledUpdate<T>>()

    override fun schedule(x: Int, y: Int, z: Int, target: T, delay: Long, priority: UpdatePriority): ScheduledUpdate<T> {
        priority as LanternUpdatePriority

        val index = Chunks.localIndex(x, y, z).toShort()
        var update = this.updatesLookup[index]

        val scheduledTime = System.currentTimeMillis() + delay

        // If there's already a update scheduled, only update if
        // the new one has a higher priority then the previous one
        if (update != null) {
            // Always create a new task if the target has changed
            if (target == update.target) {
                val value = update.compareTo(priority, update.updateId, scheduledTime)
                // The old update has a higher priority, return that one instead
                if (value <= 0)
                    return update
            }
            this.sortedUpdates.remove(update)
        }

        val updateId = this.updateCounter++
        val blockPos = Chunks.toGlobal(this.position, x, y, z)

        update = LanternScheduledUpdate(this, blockPos, target, priority, updateId, delay)

        this.updatesLookup[index] = update
        this.sortedUpdates.add(update)

        return update
    }

    override fun isScheduled(x: Int, y: Int, z: Int, target: T): Boolean {
        return this.updatesLookup[Chunks.localIndex(x, y, z).toShort()]?.target == target
    }

    override fun getScheduledAt(x: Int, y: Int, z: Int): Collection<ScheduledUpdate<T>> {
        val update = this.updatesLookup[Chunks.localIndex(x, y, z).toShort()] as? ScheduledUpdate<T>
        return if (update == null) emptyList() else listOf(update)
    }
}
