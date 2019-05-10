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
import org.lanternpowered.server.world.LanternLocation
import org.spongepowered.api.scheduler.ScheduledUpdate
import java.time.Duration
import java.util.Objects

class LanternScheduledUpdate<T>(
        private val list: ChunkScheduledUpdateList<T>,
        private val position: BlockPosition,
        private val target: T,
        private val priority: LanternTaskPriority,
        val updateId: Long,
        private val scheduledTime: Long
) : ScheduledUpdate<T>, Comparable<LanternScheduledUpdate<T>> {

    private val theLocation by lazy { LanternLocation(this.list.world, this.position.toVector3i()) }

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
        return compareTo(other.priority, other.updateId, other.scheduledTime)
    }

    internal fun compareTo(priority: LanternTaskPriority, updateId: Long, scheduledTime: Long): Int {
        return when {
            this.scheduledTime < scheduledTime -> -1
            this.scheduledTime > scheduledTime -> 1
            this.priority !== priority -> this.priority.value - priority.value
            this.updateId < updateId -> -1
            this.updateId > updateId -> 1
            else -> 0
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is LanternScheduledUpdate<*>) return false
        return other.target == this.target &&
                other.position == this.position &&
                other.scheduledTime == this.scheduledTime &&
                other.priority == this.priority &&
                other.updateId == this.updateId
    }

    override fun hashCode() = Objects.hash(this.target, this.position, this.scheduledTime, this.priority, this.updateId)

    override fun toString() = ToStringHelper("ScheduledUpdate")
            .add("world", this.world.uniqueId)
            .add("position", this.position)
            .add("target", this.target)
            .add("priority", this.priority.key)
            .add("updateId", this.updateId)
            .add("scheduledTime", this.scheduledTime)
            .toString()
}
