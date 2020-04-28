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

import org.lanternpowered.api.util.math.toBlockPosition
import org.lanternpowered.api.world.BlockPosition
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

    fun getScheduledAt(position: BlockPosition): Collection<ScheduledUpdate<T>> = getScheduledAt(position.x, position.y, position.z)

    fun isScheduled(position: BlockPosition, target: T): Boolean = isScheduled(position.x, position.y, position.z, target)

    fun schedule(position: BlockPosition, target: T, delay: Duration):
            ScheduledUpdate<T> = schedule(position.x, position.y, position.z, target, delay)

    fun schedule(position: BlockPosition, target: T, delay: Duration, priority: UpdatePriority):
            ScheduledUpdate<T> = schedule(position.x, position.y, position.z, target, delay, priority)

    fun schedule(position: BlockPosition, target: T, delay: Duration, priority: Supplier<out UpdatePriority>):
            ScheduledUpdate<T> = schedule(position.x, position.y, position.z, target, delay, priority.get())

    @JvmDefault
    override fun isScheduled(x: Int, y: Int, z: Int, target: T): Boolean

    @JvmDefault
    override fun getScheduledAt(x: Int, y: Int, z: Int): Collection<ScheduledUpdate<T>>

    @JvmDefault
    override fun schedule(x: Int, y: Int, z: Int, target: T, delay: Int, temporalUnit: TemporalUnit, priority: Supplier<out TaskPriority>):
            ScheduledUpdate<T> = schedule(BlockPosition(x, y, z), target, Duration.of(delay.toLong(), temporalUnit), priority.get())

    @JvmDefault
    override fun schedule(x: Int, y: Int, z: Int, target: T, delay: Duration):
            ScheduledUpdate<T> = schedule(BlockPosition(x, y, z), target, delay)

    @JvmDefault
    override fun schedule(x: Int, y: Int, z: Int, target: T, delay: Duration, priority: Supplier<out TaskPriority>):
            ScheduledUpdate<T> = schedule(BlockPosition(x, y, z), target, delay, priority)

    @JvmDefault
    override fun schedule(x: Int, y: Int, z: Int, target: T, delay: Int, temporalUnit: TemporalUnit):
            ScheduledUpdate<T> = schedule(BlockPosition(x, y, z), target, Duration.of(delay.toLong(), temporalUnit))

    @JvmDefault
    override fun schedule(x: Int, y: Int, z: Int, target: T, delay: Duration, priority: UpdatePriority):
            ScheduledUpdate<T> = schedule(BlockPosition(x, y, z), target, delay, priority)

    @JvmDefault
    override fun schedule(pos: Vector3i, target: T, delay: Duration, priority: TaskPriority):
            ScheduledUpdate<T> = schedule(pos.toBlockPosition(), target, delay, priority)

    @JvmDefault
    override fun schedule(pos: Vector3i, target: T, delay: Duration, priority: Supplier<out TaskPriority>):
            ScheduledUpdate<T> = schedule(pos, target, delay, priority.get())

    @JvmDefault
    override fun schedule(pos: Vector3i, target: T, delay: Duration):
            ScheduledUpdate<T> = schedule(pos.toBlockPosition(), target, delay)

    @JvmDefault
    override fun schedule(pos: Vector3i, target: T, delay: Int, temporalUnit: TemporalUnit):
            ScheduledUpdate<T> = schedule(pos.toBlockPosition(), target, Duration.of(delay.toLong(), temporalUnit))

    @JvmDefault
    override fun schedule(pos: Vector3i, target: T, delay: Int, temporalUnit: TemporalUnit, priority: TaskPriority):
            ScheduledUpdate<T> = schedule(pos.toBlockPosition(), target, Duration.of(delay.toLong(), temporalUnit), priority)

    @JvmDefault
    override fun schedule(pos: Vector3i, target: T, delay: Int, temporalUnit: TemporalUnit, priority: Supplier<out TaskPriority>):
            ScheduledUpdate<T> = schedule(pos.toBlockPosition(), target, Duration.of(delay.toLong(), temporalUnit), priority.get())

    @JvmDefault
    override fun isScheduled(pos: Vector3i, target: T): Boolean = isScheduled(pos.toBlockPosition(), target)

    @JvmDefault
    override fun getScheduledAt(pos: Vector3i): Collection<ScheduledUpdate<T>> = getScheduledAt(pos.toBlockPosition())
}
