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
@file:Suppress("UNCHECKED_CAST")

package org.lanternpowered.server.scheduler

import com.google.common.collect.ImmutableList
import org.spongepowered.api.scheduler.ScheduledTask
import org.spongepowered.api.scheduler.ScheduledTaskFuture
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.scheduler.TaskExecutorService
import org.spongepowered.api.scheduler.TaskFuture
import java.time.temporal.TemporalUnit
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.Callable
import java.util.concurrent.Delayed
import java.util.concurrent.Future
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit

internal class LanternTaskExecutorService(
        private val taskBuilderProvider: () -> Task.Builder,
        private val scheduler: LanternScheduler
) : AbstractExecutorService(), TaskExecutorService {

    override fun shutdown() {
        // Since this class is delegating its work to SchedulerService
        // and we have no way to stopping execution without keeping
        // track of all the submitted tasks, it makes sense that
        // this ExecutionService cannot be shut down.

        // While it is technically possible to cancel all tasks for
        // a plugin through the SchedulerService, we have no way to
        // ensure those tasks were created through this interface.
    }

    override fun shutdownNow(): List<Runnable> = ImmutableList.of()
    override fun isShutdown(): Boolean = false
    override fun isTerminated(): Boolean = false
    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean = false

    override fun execute(command: Runnable) {
        this.scheduler.submit(createTask(command).build())
    }

    override fun submit(command: Runnable): TaskFuture<*> = submit<Any?>(command, null)

    override fun <T> submit(command: Callable<T>): TaskFuture<T> {
        val runnable = FutureTask(command)
        val task = createTask(runnable).build()
        return LanternTaskFuture<T, Future<*>>(this.scheduler.submit(task), runnable)
    }

    override fun <T> submit(command: Runnable, result: T?): TaskFuture<T> {
        val runnable = FutureTask(command, result as T)
        val task = createTask(runnable).build()
        return LanternTaskFuture<T, Future<*>>(this.scheduler.submit(task), runnable)
    }

    private fun submitScheduledTask(task: Task): LanternScheduledTask {
        return this.scheduler.submit(task) { executor, scheduledTask, runnable ->
            val delay = scheduledTask.task.delayNanos
            val interval = scheduledTask.task.intervalNanos
            if (interval != 0L) {
                return@submit executor.scheduleAtFixedRate(runnable, delay, interval, TimeUnit.NANOSECONDS)
            } else {
                return@submit executor.schedule(runnable, delay, TimeUnit.NANOSECONDS)
            }
        }
    }

    override fun schedule(command: Runnable, delay: Long, unit: TemporalUnit): ScheduledTaskFuture<*> {
        val task = createTask(command).delay(delay, unit).build()
        return LanternScheduledTaskFuture<Any>(submitScheduledTask(task))
    }

    override fun schedule(command: Runnable, delay: Long, unit: TimeUnit): ScheduledTaskFuture<*> {
        val task = createTask(command).delay(delay, unit).build()
        return LanternScheduledTaskFuture<Any>(submitScheduledTask(task))
    }

    override fun <V> schedule(callable: Callable<V>, delay: Long, unit: TemporalUnit): ScheduledTaskFuture<V> {
        val runnable = FutureTask(callable)
        val task = createTask(runnable).delay(delay, unit).build()
        return LanternScheduledTaskFuture(submitScheduledTask(task), runnable)
    }

    override fun <V> schedule(callable: Callable<V>, delay: Long, unit: TimeUnit): ScheduledTaskFuture<V> {
        val runnable = FutureTask(callable)
        val task = createTask(runnable).delay(delay, unit).build()
        return LanternScheduledTaskFuture(submitScheduledTask(task), runnable)
    }

    override fun scheduleAtFixedRate(command: Runnable, initialDelay: Long, period: Long, unit: TemporalUnit): ScheduledTaskFuture<*> {
        val task = createTask(command).delay(initialDelay, unit).interval(period, unit).build()
        return LanternScheduledTaskFuture<Any>(submitScheduledTask(task))
    }

    override fun scheduleAtFixedRate(command: Runnable, initialDelay: Long, period: Long, unit: TimeUnit): ScheduledTaskFuture<*> {
        val task = createTask(command).delay(initialDelay, unit).interval(period, unit).build()
        return LanternScheduledTaskFuture<Any>(submitScheduledTask(task))
    }

    private fun submitTaskWithFixedDelay(task: Task): LanternScheduledTask {
        return this.scheduler.submit(task) { executor, scheduledTask, runnable ->
            val delay = scheduledTask.task.delayNanos
            val interval = scheduledTask.task.intervalNanos
            executor.scheduleWithFixedDelay(runnable, delay, interval, TimeUnit.NANOSECONDS)
        }
    }

    override fun scheduleWithFixedDelay(command: Runnable, initialDelay: Long, delay: Long, unit: TemporalUnit): ScheduledTaskFuture<*> {
        val task = createTask(command).delay(initialDelay, unit).interval(delay, unit).build()
        return LanternScheduledTaskFuture<Any>(submitTaskWithFixedDelay(task))
    }

    override fun scheduleWithFixedDelay(command: Runnable, initialDelay: Long, delay: Long, unit: TimeUnit): ScheduledTaskFuture<*> {
        val task = createTask(command).delay(initialDelay, unit).interval(delay, unit).build()
        return LanternScheduledTaskFuture<Any>(submitTaskWithFixedDelay(task))
    }

    private fun createTask(command: Runnable): Task.Builder = this.taskBuilderProvider().execute(command)

    private open class LanternTaskFuture<V, F : Future<*>>(
            val task: LanternScheduledTask,
            val resultFuture: Future<V>
    ) : TaskFuture<V> {

        val future: F get() = this.resultFuture as F

        override fun getTask(): ScheduledTask = this.task
        override fun cancel(mayInterruptIfRunning: Boolean): Boolean = this.task.cancel(mayInterruptIfRunning)
        override fun isCancelled(): Boolean = this.future.isCancelled
        override fun isDone(): Boolean = this.future.isDone
        override fun get(): V = this.resultFuture.get()
        override fun get(timeout: Long, unit: TimeUnit): V = this.resultFuture.get(timeout, unit)
    }

    private class LanternScheduledTaskFuture<V> : LanternTaskFuture<V, ScheduledTaskFuture<*>>, ScheduledTaskFuture<V> {

        constructor(task: LanternScheduledTask, resultFuture: Future<V>) : super(task, resultFuture)
        constructor(task: LanternScheduledTask) : super(task, task.future as Future<V>)

        override fun isPeriodic(): Boolean = this.future.isPeriodic
        override fun getDelay(unit: TimeUnit): Long = this.future.getDelay(unit)
        override fun compareTo(other: Delayed): Int = this.future.compareTo(other)
        override fun run() = this.future.run()
    }
}
