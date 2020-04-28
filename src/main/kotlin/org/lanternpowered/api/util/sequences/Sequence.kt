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
package org.lanternpowered.api.util.sequences

import java.util.stream.Collector

/**
 * Collects the [Sequence] with the given collector.
 */
fun <T, R, A> Sequence<T>.collect(collector: Collector<in T, A, R>): R {
    val container = collector.supplier().get()
    for (t in this)
        collector.accumulator().accept(container, t)
    return collector.finisher().apply(container)
}
