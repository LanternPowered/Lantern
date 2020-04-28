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

import org.lanternpowered.api.world.scheduler.ScheduledUpdate
import org.lanternpowered.api.world.scheduler.ScheduledUpdateList
import org.lanternpowered.api.world.scheduler.UpdatePriority
import java.time.Duration
import java.time.temporal.TemporalUnit

abstract class AbstractScheduledUpdateList<T : Any> : ScheduledUpdateList<T> {

    override fun schedule(x: Int, y: Int, z: Int, target: T, delay: Int, temporalUnit: TemporalUnit, priority: UpdatePriority): ScheduledUpdate<T> {
        return schedule(x, y, z, target, temporalUnit.duration.toMillis() * delay, priority)
    }

    override fun schedule(x: Int, y: Int, z: Int, target: T, delay: Duration, priority: UpdatePriority): ScheduledUpdate<T> {
        return schedule(x, y, z, target, delay.toMillis(), priority)
    }

    abstract fun schedule(x: Int, y: Int, z: Int, target: T, delay: Long, priority: UpdatePriority): ScheduledUpdate<T>
}
