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

import org.lanternpowered.api.world.BlockPos
import org.lanternpowered.api.world.World
import org.spongepowered.api.scheduler.ScheduledUpdate
import org.spongepowered.api.scheduler.ScheduledUpdateList
import org.spongepowered.api.scheduler.TaskPriority
import java.time.Duration
import java.time.temporal.TemporalUnit
import java.util.concurrent.ConcurrentSkipListSet

class LanternScheduledUpdateList<T>(val world: World) : ScheduledUpdateList<T> {

    private var updateCounter = 0L
    private var updates = ConcurrentSkipListSet<LanternScheduledUpdate<T>>()

    override fun schedule(x: Int, y: Int, z: Int, target: T, delay: Int, temporalUnit: TemporalUnit, priority: TaskPriority): ScheduledUpdate<T> {
        return schedule(x, y, z, target, temporalUnit.duration.toMillis() * delay, priority)
    }

    override fun schedule(x: Int, y: Int, z: Int, target: T, delay: Duration, priority: TaskPriority): ScheduledUpdate<T> {
        return schedule(x, y, z, target, delay.toMillis(), priority)
    }

    private fun schedule(x: Int, y: Int, z: Int, target: T, delay: Long, priority: TaskPriority): ScheduledUpdate<T> {
        priority as LanternTaskPriority

        val updateId = this.updateCounter++
        val blockPos = BlockPos(x, y, z)

        val update = LanternScheduledUpdate(this, blockPos, target, priority, updateId, delay)
        this.updates.add(update)

        return update
    }

    override fun isScheduled(x: Int, y: Int, z: Int, target: T): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getScheduledAt(x: Int, y: Int, z: Int): MutableCollection<ScheduledUpdate<T>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}