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

import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.first
import org.spongepowered.plugin.PluginContainer
import org.spongepowered.api.scheduler.ScheduledTask
import org.spongepowered.api.scheduler.Task
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer

class LanternTaskBuilder : Task.Builder {

    private var consumer: Consumer<ScheduledTask>? = null
    private var name: String? = null
    private var delay: Duration = Duration.ZERO
    private var interval: Duration = Duration.ZERO
    private var plugin: PluginContainer? = null

    override fun reset(): LanternTaskBuilder = apply {
        this.name = null
        this.consumer = null
        this.delay = Duration.ZERO
        this.interval = Duration.ZERO
    }

    override fun execute(consumer: Consumer<ScheduledTask>): LanternTaskBuilder = apply { this.consumer = consumer }
    override fun delay(delay: Duration): LanternTaskBuilder = apply { this.delay = delay }
    override fun interval(interval: Duration): LanternTaskBuilder = apply { this.interval = interval }
    override fun name(name: String): LanternTaskBuilder = apply { this.name = name }
    override fun plugin(plugin: PluginContainer): LanternTaskBuilder = apply { this.plugin = plugin }

    override fun build(): Task {
        val consumer = checkNotNull(this.consumer) { "The consumer is not set" }
        val plugin = this.plugin ?: CauseStack.currentOrEmpty().first() ?: error("No PluginContainer found in the CauseStack.")
        val name = this.name ?: run {
            val number = taskCounter.incrementAndGet()
            String.format("%s-%s", plugin.metadata.id, number)
        }
        val delay = this.delay.toNanos()
        val interval = this.interval.toNanos()
        return LanternTask(consumer, name, plugin, delay, interval)
    }

    override fun from(value: Task): LanternTaskBuilder = apply {
        val task = value as LanternTask
        this.delay = task.delay
        this.interval = task.interval
        this.consumer = task.consumer
        this.name = task.name
        this.plugin = null
    }

    companion object {
        private val taskCounter = AtomicInteger()
    }
}
