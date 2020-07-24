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

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.channels.Channel

@Suppress("EXPERIMENTAL_API_USAGE")
suspend fun <T> Collection<Deferred<T>>.awaitAny(): T {
    check(isNotEmpty())
    val channel = Channel<Result<T>>(this.size)
    val disposables = ArrayList<Pair<Deferred<T>, DisposableHandle>>(this.size)
    try {
        for (deferred in this) {
            disposables += deferred to deferred.invokeOnCompletion { throwable ->
                if (throwable != null) {
                    channel.offer(Result.failure(throwable))
                } else {
                    channel.offer(Result.success(deferred.getCompleted()))
                }
            }
        }
        var result: Result<T>? = null
        for (i in this.indices) {
            result = channel.receive()
            if (result.isSuccess)
                return result.getOrThrow()
        }
        checkNotNull(result)
        throw result.exceptionOrNull()!!
    } finally {
        for ((deferred, disposable) in disposables) {
            if (!deferred.isCompleted)
                deferred.cancel()
            disposable.dispose()
        }
    }
}
