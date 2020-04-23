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
package org.lanternpowered.server.scheduler

import org.lanternpowered.api.util.ToStringHelper
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.scheduler.ScheduledTask
import org.spongepowered.api.scheduler.Task
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer

/**
 * An internal representation of a [Task] created by a plugin.
 */
class LanternTask internal constructor(
        private val executor: Consumer<ScheduledTask>,
        private val name: String,
        private val owner: PluginContainer,
        val delayNanos: Long,
        val intervalNanos: Long
) : Task {

    private val toString: String by lazy {
        ToStringHelper("Task")
                .add("name", this.name)
                .add("delay", this.delayNanos)
                .add("interval", this.intervalNanos)
                .add("owner", this.owner)
                .toString()
    }

    // The amount of times this task has been scheduled
    @JvmField val scheduledCounter = AtomicInteger()

    override fun getOwner(): PluginContainer = this.owner
    override fun getDelay(): Duration = Duration.ofNanos(this.delayNanos)
    override fun getInterval(): Duration = Duration.ofNanos(this.intervalNanos)
    override fun getConsumer(): Consumer<ScheduledTask> = this.executor
    override fun getName(): String = this.name
    override fun toString(): String = this.toString
}
