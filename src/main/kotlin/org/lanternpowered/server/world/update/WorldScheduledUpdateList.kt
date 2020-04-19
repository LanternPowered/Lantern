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
import org.spongepowered.api.scheduler.ScheduledUpdate
import org.spongepowered.api.scheduler.TaskPriority

class WorldScheduledUpdateList<T>(val world: World, val lookup: (Int, Int) -> AbstractScheduledUpdateList<T>) : AbstractScheduledUpdateList<T>() {

    override fun schedule(x: Int, y: Int, z: Int, target: T, delay: Long, priority: TaskPriority): ScheduledUpdate<T> {
        val chunkX = x shr 4
        val chunkZ = z shr 4
        val localX = x and 0xf
        val localZ = z and 0xf
        return this.lookup(chunkX, chunkZ).schedule(localX, y, localZ, target, delay, priority)
    }

    override fun isScheduled(x: Int, y: Int, z: Int, target: T): Boolean {
        val chunkX = x shr 4
        val chunkZ = z shr 4
        val localX = x and 0xf
        val localZ = z and 0xf
        return this.lookup(chunkX, chunkZ).isScheduled(localX, y, localZ, target)
    }

    override fun getScheduledAt(x: Int, y: Int, z: Int): Collection<ScheduledUpdate<T>> {
        val chunkX = x shr 4
        val chunkZ = z shr 4
        val localX = x and 0xf
        val localZ = z and 0xf
        return this.lookup(chunkX, chunkZ).getScheduledAt(localX, y, localZ)
    }
}
