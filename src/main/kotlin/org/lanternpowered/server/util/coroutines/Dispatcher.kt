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
package org.lanternpowered.server.util.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.lanternpowered.server.util.executor.LanternScheduledExecutorService
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Delayed
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.delay as delayCoroutine

/**
 * Gets the [CoroutineDispatcher] as a [ScheduledExecutorService].
 */
fun CoroutineDispatcher.asScheduledExecutorService(): LanternScheduledExecutorService = ScheduledDispatcherService(this)

@Suppress("EXPERIMENTAL_API_USAGE")
private class ScheduledDispatcherService(private val dispatcher: CoroutineDispatcher) : LanternScheduledExecutorService {

    private class ScheduledDeferredFuture<V>(
            val deferred: Deferred<V>, val delayMillis: Long
    ) : ScheduledFuture<V> {

        override fun isDone(): Boolean = this.deferred.isCompleted
        override fun isCancelled(): Boolean = this.deferred.isCancelled

        override fun get(): V = runBlocking { deferred.await() }

        override fun get(timeout: Long, unit: TimeUnit): V = runBlocking {
            withTimeout(unit.toMillis(timeout)) {
                deferred.await()
            }
        }

        override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
            if (mayInterruptIfRunning || !this.deferred.isActive) {
                this.deferred.cancel()
                return true
            }
            return false
        }

        override fun compareTo(other: Delayed): Int = this.delayMillis.compareTo(other.getDelay(TimeUnit.MILLISECONDS))
        override fun getDelay(unit: TimeUnit): Long = unit.convert(this.delayMillis, TimeUnit.MILLISECONDS)
    }

    override fun schedule(command: Runnable, delay: Long, unit: TimeUnit): ScheduledFuture<Unit> {
        val millis = unit.toMillis(delay)
        val start = System.currentTimeMillis()
        val deferred = GlobalScope.async(this.dispatcher) {
            delayCoroutine(millis - (System.currentTimeMillis() - start))
            command.run()
        }
        return ScheduledDeferredFuture(deferred, millis)
    }

    override fun <V> schedule(callable: Callable<V>, delay: Long, unit: TimeUnit): ScheduledFuture<V> {
        val millis = unit.toMillis(delay)
        val start = System.currentTimeMillis()
        val deferred = GlobalScope.async(this.dispatcher) {
            delayCoroutine(millis - (System.currentTimeMillis() - start))
            callable.call()
        }
        return ScheduledDeferredFuture(deferred, millis)
    }

    override fun <T> submit(task: Callable<T>): CompletableFuture<T> {
        val future = CompletableFuture<T>()
        execute {
            try {
                future.complete(task.call())
            } catch (ex: Throwable) {
                future.completeExceptionally(ex)
            }
        }
        return future
    }

    override fun <T> submit(task: Runnable, result: T): CompletableFuture<T> {
        val future = CompletableFuture<T>()
        execute {
            try {
                task.run()
                future.complete(result)
            } catch (ex: Throwable) {
                future.completeExceptionally(ex)
            }
        }
        return future
    }

    override fun submit(task: Runnable): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        execute {
            try {
                task.run()
                future.complete(Unit)
            } catch (ex: Throwable) {
                future.completeExceptionally(ex)
            }
        }
        return future
    }

    override fun <T> invokeAny(tasks: Collection<Callable<T>>): T =
            runBlocking(this.dispatcher) {
                tasks.map { task -> async { task.call() } }.awaitAny<T>()
            }

    override fun <T> invokeAny(tasks: Collection<Callable<T>>, timeout: Long, unit: TimeUnit): T =
            runBlocking(this.dispatcher) {
                withTimeout(unit.toMillis(timeout)) {
                    tasks.map { task -> async { task.call() } }.awaitAny<T>()
                }
            }

    override fun <T> invokeAll(tasks: Collection<Callable<T>>): List<Future<T>> =
            tasks.asSequence()
                    .map { task ->
                        GlobalScope.async(this.dispatcher) { task.call() }.asCompletableFuture()
                    }
                    .toList()

    override fun <T> invokeAll(tasks: Collection<Callable<T>>, timeout: Long, unit: TimeUnit): List<Future<T>> =
            // TODO: Timeout
            tasks.asSequence()
                    .map { task ->
                        GlobalScope.async(this.dispatcher) { task.call() }.asCompletableFuture()
                    }
                    .toList()

    override fun scheduleAtFixedRate(command: Runnable, initialDelay: Long, period: Long, unit: TimeUnit): ScheduledFuture<Unit> {
        val initialDelayMillis = unit.toMillis(initialDelay)
        val initialStart = System.currentTimeMillis()
        val periodMillis = unit.toMillis(period)
        val deferred = GlobalScope.async(this.dispatcher) {
            try {
                delayCoroutine(initialDelayMillis - (System.currentTimeMillis() - initialStart))
                var start = System.currentTimeMillis()
                while (true) {
                    command.run()
                    val end = System.currentTimeMillis()
                    // Reduce the delay by the millis it took to execute the command
                    val delayMillis = periodMillis - (end - start)
                    delayCoroutine(delayMillis)
                    start = end
                }
            } catch (ex: Throwable) {

            }
        }
        return ScheduledDeferredFuture(deferred, initialDelayMillis)
    }

    override fun scheduleWithFixedDelay(command: Runnable, initialDelay: Long, delay: Long, unit: TimeUnit): ScheduledFuture<Unit> {
        val initialDelayMillis = unit.toMillis(initialDelay)
        val initialStart = System.currentTimeMillis()
        val delayMillis = unit.toMillis(delay)
        val deferred = GlobalScope.async(this.dispatcher) {
            delayCoroutine(initialDelayMillis - (System.currentTimeMillis() - initialStart))
            while (true) {
                command.run()
                delayCoroutine(delayMillis)
            }
        }
        return ScheduledDeferredFuture(deferred, initialDelayMillis)
    }

    override fun execute(command: Runnable) {
        // Directly dispatch the task, avoids creating coroutine instances so
        // this is preferred if you don't need any of the coroutine functions
        this.dispatcher.dispatch(EmptyCoroutineContext, command)
    }

    override fun shutdown() {}
    override fun shutdownNow(): List<Runnable> = emptyList()
    override fun isShutdown(): Boolean = !this.dispatcher.isActive
    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean = false
    override fun isTerminated(): Boolean = !this.dispatcher.isActive
}
