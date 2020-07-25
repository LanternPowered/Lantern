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
package org.lanternpowered.server.util.executor

import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService

interface LanternExecutorService : ExecutorService {

    override fun <T> submit(task: Callable<T>): CompletableFuture<T>

    override fun <T> submit(task: Runnable, result: T): CompletableFuture<T>

    @Deprecated(message = "Use the submit method with Callable.", level = DeprecationLevel.HIDDEN)
    override fun submit(task: Runnable): CompletableFuture<Unit>
}

interface LanternScheduledExecutorService : LanternExecutorService, ScheduledExecutorService

/**
 * Gets the [ExecutorService] as a [LanternExecutorService].
 */
fun ExecutorService.asLanternExecutorService(): LanternExecutorService =
        when (this) {
            is ScheduledExecutorService -> asLanternExecutorService()
            is LanternExecutorService -> this
            else -> LanternExecutorServiceImpl(this)
        }

/**
 * Gets the [ScheduledExecutorService] as a [LanternScheduledExecutorService].
 */
fun ScheduledExecutorService.asLanternExecutorService(): LanternScheduledExecutorService =
        if (this is LanternScheduledExecutorService) this else LanternScheduledExecutorServiceImpl(this)

private class LanternCompletableFuture<T> : CompletableFuture<T>() {

    lateinit var future: Future<*>

    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        if (this.future.cancel(mayInterruptIfRunning))
            return super.cancel(mayInterruptIfRunning)
        return false
    }
}

private fun <T> ExecutorService.submitCompletable(task: Callable<T>): CompletableFuture<T> {
    val completableFuture = LanternCompletableFuture<T>()
    completableFuture.future = this.submit {
        try {
            completableFuture.complete(task.call())
        } catch (ex: Throwable) {
            completableFuture.completeExceptionally(ex)
        }
    }
    return completableFuture
}

private fun <T> ExecutorService.submitCompletable(task: Runnable, result: T): CompletableFuture<T> {
    val completableFuture = LanternCompletableFuture<T>()
    completableFuture.future = this.submit {
        try {
            task.run()
            completableFuture.complete(result)
        } catch (ex: Throwable) {
            completableFuture.completeExceptionally(ex)
        }
    }
    return completableFuture
}

private class LanternExecutorServiceImpl(private val service: ExecutorService) :
        LanternExecutorService, ExecutorService by service {

    override fun <T> submit(task: Callable<T>): CompletableFuture<T> = submitCompletable(task)
    override fun <T> submit(task: Runnable, result: T): CompletableFuture<T> = submitCompletable(task, result)
    override fun submit(task: Runnable): CompletableFuture<Unit> = submitCompletable(task, Unit)
}

private class LanternScheduledExecutorServiceImpl(private val service: ScheduledExecutorService) :
        LanternScheduledExecutorService, ScheduledExecutorService by service {

    override fun <T> submit(task: Callable<T>): CompletableFuture<T> = submitCompletable(task)
    override fun <T> submit(task: Runnable, result: T): CompletableFuture<T> = submitCompletable(task, result)
    override fun submit(task: Runnable): CompletableFuture<Unit> = submitCompletable(task, Unit)
}
