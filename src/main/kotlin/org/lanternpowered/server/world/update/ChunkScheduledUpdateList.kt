/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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
package org.lanternpowered.server.world.update

import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
import org.lanternpowered.api.world.BlockPos
import org.lanternpowered.api.world.World
import org.spongepowered.api.scheduler.ScheduledUpdate
import org.spongepowered.api.scheduler.TaskPriority

@Suppress("UNCHECKED_CAST")
class ChunkScheduledUpdateList<T>(val world: World, private val chunkX: Int, private val chunkZ: Int) : AbstractScheduledUpdateList<T>() {

    private var updateCounter = 0L

    private val updatesLookup = Short2ObjectOpenHashMap<LanternScheduledUpdate<T>>()
    private val sortedUpdates = ObjectRBTreeSet<LanternScheduledUpdate<T>>()

    override fun schedule(x: Int, y: Int, z: Int, target: T, delay: Long, priority: TaskPriority): ScheduledUpdate<T> {
        priority as LanternTaskPriority

        val index = index(x, y, z)
        var update = this.updatesLookup[index]

        val scheduledTime = System.currentTimeMillis() + delay

        // If there's already a update scheduled, only update if
        // the new one has a higher priority then the previous one
        if (update != null) {
            // Always create a new task if the target has changed
            if (target == update.target) {
                val value = update.compareTo(priority, update.updateId, scheduledTime)
                // The old update has a higher priority, return that one instead
                if (value <= 0) {
                    return update
                }
            }
            this.sortedUpdates.remove(update)
        }

        val updateId = this.updateCounter++
        val blockPos = BlockPos((this.chunkX shl 4) or x, y, (this.chunkZ shl 4) or z)

        update = LanternScheduledUpdate(this, blockPos, target, priority, updateId, delay)

        this.updatesLookup[index] = update
        this.sortedUpdates.add(update)

        return update
    }

    override fun isScheduled(x: Int, y: Int, z: Int, target: T): Boolean {
        return this.updatesLookup[index(x, y, z)]?.target == target
    }

    override fun getScheduledAt(x: Int, y: Int, z: Int): Collection<ScheduledUpdate<T>> {
        val update = this.updatesLookup[index(x, y, z)] as? ScheduledUpdate<T>
        return if (update == null) emptyList() else listOf(update)
    }

    fun index(x: Int, y: Int, z: Int) = ((y shl 8) or (z shl 4) or x).toShort()
}
