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
import org.lanternpowered.api.cause.withFrame
import org.lanternpowered.api.util.collections.toImmutableSet
import org.lanternpowered.api.util.optional.optional
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.scheduler.ScheduledTask
import org.spongepowered.api.scheduler.Scheduler
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.scheduler.TaskExecutorService
import org.spongepowered.api.util.Functional
import java.util.Optional
import java.util.UUID
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class LanternScheduler(val service: ScheduledExecutorService) : Scheduler {

    private val tasksByUniqueId: MutableMap<UUID, LanternScheduledTask> = ConcurrentHashMap()

    fun shutdown(timeout: Long, unit: TimeUnit) {
        for (task in this.tasksByUniqueId.values)
            task.cancel()
        try {
            this.service.shutdown()
            if (!this.service.awaitTermination(timeout, unit))
                this.service.shutdownNow()
        } catch (ignored: InterruptedException) {
        }
    }

    override fun getTaskById(id: UUID): Optional<ScheduledTask> = this.tasksByUniqueId[id].optional()

    override fun getTasksByName(pattern: String): Set<ScheduledTask> {
        val searchPattern = Pattern.compile(pattern)
        return this.tasksByUniqueId.values.stream()
                .filter { task -> searchPattern.matcher(task.name).matches() }
                .toImmutableSet()
    }

    override fun getTasks(): Set<ScheduledTask> = this.tasksByUniqueId.values.toImmutableSet()

    override fun getTasksByPlugin(plugin: PluginContainer): Set<ScheduledTask> = this.tasksByUniqueId.values.stream()
            .filter { task: LanternScheduledTask -> task.owner == plugin }
            .toImmutableSet()

    override fun createExecutor(plugin: PluginContainer): TaskExecutorService =
            LanternTaskExecutorService({ LanternTaskBuilder().plugin(plugin) }, this)

    /**
     * Removes the stored [LanternScheduledTask].
     *
     * @param task The scheduled task
     */
    fun remove(task: LanternScheduledTask) {
        this.tasksByUniqueId.remove(task.uniqueId)
    }

    override fun submit(task: Task): LanternScheduledTask {
        return submit(task) { executor, scheduledTask, runnable ->
            val delay = scheduledTask.task.delayNanos
            val interval = scheduledTask.task.intervalNanos
            when {
                interval != 0L -> executor.scheduleAtFixedRate(runnable, delay, interval, TimeUnit.NANOSECONDS)
                delay != 0L -> executor.schedule(runnable, delay, TimeUnit.NANOSECONDS)
                else -> executor.submit(runnable)
            }
        }
    }

    fun submit(task: Task, submitFunction: (ScheduledExecutorService, LanternScheduledTask, Runnable) -> Future<*>): LanternScheduledTask {
        val scheduledTask = LanternScheduledTask(task as LanternTask, this)
        val runnable = Runnable {
            val causeStack = CauseStack.currentOrEmpty()
            causeStack.pushCause(task.owner)
            causeStack.pushCause(task)
            try {
                causeStack.withFrame { task.consumer.accept(scheduledTask) }
            } catch (throwable: Throwable) {
                task.owner.logger.error("Error while handling task: {}", task.name, throwable)
            }
            causeStack.popCauses(2)
            // Remove the scheduled task once it's done,
            // only do this if it's not a repeated task
            if (scheduledTask.task.intervalNanos == 0L || scheduledTask.scheduledRemoval)
                remove(scheduledTask)
        }
        scheduledTask.future = submitFunction(this.service, scheduledTask, runnable)
        this.tasksByUniqueId[scheduledTask.uniqueId] = scheduledTask
        return scheduledTask
    }

    fun <T> submit(callable: Callable<T>): CompletableFuture<T> = Functional.asyncFailableFuture(callable, this.service)
    fun <R> submit(callable: () -> R): CompletableFuture<R> = submit(Callable<R> { callable() })

    fun submit(runnable: Runnable): CompletableFuture<Unit> = submit(Callable<Unit> { runnable.run() })
}
