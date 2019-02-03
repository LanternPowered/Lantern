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

import org.lanternpowered.api.world.BlockPosition
import org.lanternpowered.server.util.ToStringHelper
import org.spongepowered.api.scheduler.ScheduledUpdate
import org.spongepowered.api.world.Location
import java.time.Duration

class LanternScheduledUpdate<T>(
        private val list: LanternScheduledUpdateList<T>,
        private val position: BlockPosition,
        private val target: T,
        private val priority: LanternTaskPriority,
        private val updateId: Long,
        delay: Long
) : ScheduledUpdate<T>, Comparable<LanternScheduledUpdate<T>> {

    // The time this update is scheduled at
    private val scheduledTime: Long = System.currentTimeMillis() + delay
    private val theLocation by lazy { Location(this.list.world, this.position.toVector3i()) }

    // The state of the scheduled task
    private var state = ScheduledUpdate.State.WAITING

    override fun getLocation() = this.theLocation
    override fun getTarget() = this.target
    override fun getPriority() = this.priority
    override fun getState() = this.state

    override fun getDelay(): Duration {
        var diff = System.currentTimeMillis() - this.scheduledTime
        if (diff < 0) {
            diff = 0
        }
        return Duration.ofMillis(diff)
    }

    override fun cancel(): Boolean {
        if (this.state != ScheduledUpdate.State.WAITING) {
            return false
        }
        this.state = ScheduledUpdate.State.CANCELLED
        return true
    }

    /**
     * Updates this [ScheduledUpdate]
     *
     * @param time The current system time
     * @return The next state
     */
    fun update(time: Long): ScheduledUpdate.State {
        if (this.state != ScheduledUpdate.State.CANCELLED && time >= this.scheduledTime) {
            this.state = ScheduledUpdate.State.FINISHED
        }
        return this.state
    }

    override fun compareTo(other: LanternScheduledUpdate<T>): Int {
        return when {
            this.scheduledTime < other.scheduledTime -> -1
            this.scheduledTime > other.scheduledTime -> 1
            this.priority != other.priority -> this.priority.value - other.priority.value
            this.updateId < other.updateId -> -1
            this.updateId > other.updateId -> 1
            else -> 0
        }
    }

    override fun hashCode(): Int {
        return this.position.hashCode()
    }

    override fun toString() = ToStringHelper("ScheduledUpdate")
            .add("world", this.world.uniqueId)
            .add("position", this.position)
            .add("target", this.target)
            .add("priority", this.priority.key)
            .add("updateId", this.updateId)
            .add("scheduledTime", this.scheduledTime)
            .toString()
}
