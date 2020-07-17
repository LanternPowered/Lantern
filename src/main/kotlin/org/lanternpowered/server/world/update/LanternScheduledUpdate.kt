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

import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.api.world.BlockPosition
import org.lanternpowered.api.world.Location
import org.lanternpowered.api.world.World
import org.lanternpowered.api.world.scheduler.ScheduledUpdate
import org.lanternpowered.api.world.scheduler.ScheduledUpdateState
import org.lanternpowered.api.world.scheduler.UpdatePriority
import org.lanternpowered.server.world.LanternLocation
import java.time.Duration
import java.util.Objects

class LanternScheduledUpdate<T : Any>(
        private val list: ChunkScheduledUpdateList<T>,
        override val position: BlockPosition,
        private val target: T,
        private val priority: LanternUpdatePriority,
        val updateId: Long,
        private val scheduledTime: Long
) : ScheduledUpdate<T>, Comparable<LanternScheduledUpdate<T>> {

    private val theLocation by lazy { LanternLocation(this.list.world, this.position.toVector3i()) }

    // The state of the scheduled task
    private var state = ScheduledUpdateState.WAITING

    override fun getWorld(): World = this.list.world
    override fun getLocation(): Location = this.theLocation
    override fun getTarget(): T = this.target
    override fun getPriority(): UpdatePriority = this.priority
    override fun getState(): ScheduledUpdateState = this.state

    override fun getDelay(): Duration {
        var diff = System.currentTimeMillis() - this.scheduledTime
        if (diff < 0) {
            diff = 0
        }
        return Duration.ofMillis(diff)
    }

    override fun cancel(): Boolean {
        if (this.state != ScheduledUpdateState.WAITING) {
            return false
        }
        this.state = ScheduledUpdateState.CANCELLED
        return true
    }

    /**
     * Updates this [ScheduledUpdate]
     *
     * @param time The current system time
     * @return The next state
     */
    fun update(time: Long): ScheduledUpdateState {
        if (this.state != ScheduledUpdateState.CANCELLED && time >= this.scheduledTime) {
            this.state = ScheduledUpdateState.FINISHED
        }
        return this.state
    }

    override fun compareTo(other: LanternScheduledUpdate<T>): Int {
        return compareTo(other.priority, other.updateId, other.scheduledTime)
    }

    internal fun compareTo(priority: LanternUpdatePriority, updateId: Long, scheduledTime: Long): Int {
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
