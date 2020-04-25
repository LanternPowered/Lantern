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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.server.util.future

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.function.Function

inline fun <T, U> CompletableFuture<T>.thenAsync(executor: Executor, noinline fn: (T) -> U): CompletableFuture<U> =
        thenApplyAsync(Function(fn), executor)

inline fun <T, U> CompletableFuture<T>.then(noinline fn: (T) -> U): CompletableFuture<U> =
        thenApply(Function(fn))
