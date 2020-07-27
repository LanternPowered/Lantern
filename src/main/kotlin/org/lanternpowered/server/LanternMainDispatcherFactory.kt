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
package org.lanternpowered.server

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Delay
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.internal.MainDispatcherFactory
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext

@InternalCoroutinesApi
internal class LanternMainDispatcherFactory : MainDispatcherFactory {

    override val loadPriority: Int = Int.MAX_VALUE // Highest priority, we need to win this!

    override fun createDispatcher(allFactories: List<MainDispatcherFactory>): MainCoroutineDispatcher =
            LanternMainCoroutineDispatcher(LanternServerLaunch.mainExecutor.asCoroutineDispatcher())
}

@InternalCoroutinesApi
private class LanternMainCoroutineDispatcher(
        private val dispatcher: CoroutineDispatcher,
        private val invokeImmediately: Boolean = false
) : MainCoroutineDispatcher(), Delay {

    private val delay = this.dispatcher as Delay

    override val immediate: MainCoroutineDispatcher =
            if (this.invokeImmediately) this else LanternMainCoroutineDispatcher(this.dispatcher, true)

    override fun isDispatchNeeded(context: CoroutineContext): Boolean =
            !this.invokeImmediately || this.dispatcher.isDispatchNeeded(context)

    override fun dispatchYield(context: CoroutineContext, block: Runnable) =
            this.dispatcher.dispatchYield(context, block)

    override fun releaseInterceptedContinuation(continuation: Continuation<*>) =
            this.dispatcher.releaseInterceptedContinuation(continuation)

    override fun dispatch(context: CoroutineContext, block: Runnable) =
            this.dispatcher.dispatch(context, block)

    override suspend fun delay(time: Long) =
            this.delay.delay(time)

    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) =
            this.delay.scheduleResumeAfterDelay(timeMillis, continuation)

    override fun invokeOnTimeout(timeMillis: Long, block: Runnable): DisposableHandle =
            this.delay.invokeOnTimeout(timeMillis, block)
}
