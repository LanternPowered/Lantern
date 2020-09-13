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

import org.lanternpowered.api.world.World
import org.lanternpowered.api.world.scheduler.ScheduledUpdate
import org.lanternpowered.api.world.scheduler.UpdatePriority
import org.lanternpowered.server.world.chunk.Chunks
import org.spongepowered.math.vector.Vector3i

class WorldScheduledUpdateList<T : Any>(
        val world: World,
        val lookup: (Int, Int, Int) -> AbstractScheduledUpdateList<T>
) : AbstractScheduledUpdateList<T>() {

    override fun isScheduled(pos: Vector3i, target: T): Boolean =
            this.isScheduled(pos.x, pos.y, pos.z, target)

    override fun getScheduledAt(pos: Vector3i): Collection<ScheduledUpdate<T>> =
            this.getScheduledAt(pos.x, pos.y, pos.z)

    override fun schedule(x: Int, y: Int, z: Int, target: T, delay: Long, priority: UpdatePriority): ScheduledUpdate<T> {
        val chunkX = Chunks.toChunk(x)
        val chunkY = Chunks.toChunk(y)
        val chunkZ = Chunks.toChunk(z)
        val localX = Chunks.toLocal(x)
        val localY = Chunks.toLocal(y)
        val localZ = Chunks.toLocal(z)
        return this.lookup(chunkX, chunkY, chunkZ).schedule(localX, localY, localZ, target, delay, priority)
    }

    override fun isScheduled(x: Int, y: Int, z: Int, target: T): Boolean {
        val chunkX = Chunks.toChunk(x)
        val chunkY = Chunks.toChunk(y)
        val chunkZ = Chunks.toChunk(z)
        val localX = Chunks.toLocal(x)
        val localY = Chunks.toLocal(y)
        val localZ = Chunks.toLocal(z)
        return this.lookup(chunkX, chunkY, chunkZ).isScheduled(localX, localY, localZ, target)
    }

    override fun getScheduledAt(x: Int, y: Int, z: Int): Collection<ScheduledUpdate<T>> {
        val chunkX = Chunks.toChunk(x)
        val chunkY = Chunks.toChunk(y)
        val chunkZ = Chunks.toChunk(z)
        val localX = Chunks.toLocal(x)
        val localY = Chunks.toLocal(y)
        val localZ = Chunks.toLocal(z)
        return this.lookup(chunkX, chunkY, chunkZ).getScheduledAt(localX, localY, localZ)
    }
}
