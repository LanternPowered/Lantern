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
package org.lanternpowered.api.world.scheduler

import org.spongepowered.api.scheduler.TaskPriority
import org.spongepowered.math.vector.Vector3i
import java.time.Duration
import java.time.temporal.TemporalUnit
import java.util.function.Supplier

/**
 * A time based priority scheduled list targeting specific types of
 * objects that need to be updated. In common cases, there's either
 * a {@link BlockType} or {@link FluidType} being updated.
 *
 * @property T The type of update objects that are being scheduled
 */
interface ScheduledUpdateList<T : Any> : org.spongepowered.api.scheduler.ScheduledUpdateList<T> {

    @JvmDefault
    override fun schedule(x: Int, y: Int, z: Int, target: T, delay: Duration, priority: UpdatePriority): ScheduledUpdate<T>

    @JvmDefault
    override fun schedule(pos: Vector3i, target: T, delay: Duration, priority: TaskPriority): ScheduledUpdate<T>

    @JvmDefault
    override fun isScheduled(x: Int, y: Int, z: Int, target: T): Boolean

    @JvmDefault
    override fun getScheduledAt(x: Int, y: Int, z: Int): Collection<ScheduledUpdate<T>>

    @JvmDefault
    override fun schedule(
            x: Int, y: Int, z: Int, target: T, delay: Int, temporalUnit: TemporalUnit, priority: Supplier<out UpdatePriority>
    ): ScheduledUpdate<T> = this.schedule(x, y, z, target, Duration.of(delay.toLong(), temporalUnit), priority.get())

    @JvmDefault
    override fun schedule(x: Int, y: Int, z: Int, target: T, delay: Duration): ScheduledUpdate<T> =
            this.schedule(Vector3i(x, y, z), target, delay, UpdatePriorities.NORMAL)

    @JvmDefault
    override fun schedule(x: Int, y: Int, z: Int, target: T, delay: Duration, priority: Supplier<out UpdatePriority>): ScheduledUpdate<T> =
            this.schedule(x, y, z, target, delay, priority.get())

    @JvmDefault
    override fun schedule(x: Int, y: Int, z: Int, target: T, delay: Int, temporalUnit: TemporalUnit): ScheduledUpdate<T> =
            this.schedule(x, y, z, target, Duration.of(delay.toLong(), temporalUnit))

    @JvmDefault
    override fun schedule(pos: Vector3i, target: T, delay: Duration, priority: Supplier<out TaskPriority>): ScheduledUpdate<T> =
            this.schedule(pos, target, delay, priority.get())

    @JvmDefault
    override fun schedule(pos: Vector3i, target: T, delay: Duration): ScheduledUpdate<T> =
            this.schedule(pos, target, delay, UpdatePriorities.NORMAL)

    @JvmDefault
    override fun schedule(pos: Vector3i, target: T, delay: Int, temporalUnit: TemporalUnit): ScheduledUpdate<T> =
            this.schedule(pos, target, Duration.of(delay.toLong(), temporalUnit))

    @JvmDefault
    override fun schedule(pos: Vector3i, target: T, delay: Int, temporalUnit: TemporalUnit, priority: TaskPriority): ScheduledUpdate<T> =
            this.schedule(pos, target, Duration.of(delay.toLong(), temporalUnit), priority)

    @JvmDefault
    override fun schedule(
            pos: Vector3i, target: T, delay: Int, temporalUnit: TemporalUnit, priority: Supplier<out TaskPriority>
    ): ScheduledUpdate<T> = this.schedule(pos, target, Duration.of(delay.toLong(), temporalUnit), priority.get())
}
