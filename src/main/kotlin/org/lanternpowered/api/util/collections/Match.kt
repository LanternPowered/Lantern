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
package org.lanternpowered.api.util.collections

/**
 * Gets a sequence that groups the sequence by matched lists.
 *
 * For example the following set of strings: [AA, A, BA, C, B]
 * If you would match by two string by the requirement that they
 * contain A you'll end up with. [[AA, A, BA], C, B].
 * If you would match by the first character you'll end up with
 * [[AA, A],[BA, B], C]
 */
fun <T> Sequence<T>.match(matcher: (first: T, second: T) -> Boolean): Sequence<Sequence<T>> =
        this.asIterable().match(matcher).asSequence().map { it.asSequence() }

/**
 * Gets a sequence that groups the sequence by matched lists.
 *
 * For example the following set of strings: [AA, A, BA, C, B]
 * If you would match by two string by the requirement that they
 * contain A you'll end up with. [[AA, A, BA], C, B].
 * If you would match by the first character you'll end up with
 * [[AA, A],[BA, B], C]
 */
fun <T> Iterable<T>.match(matcher: (first: T, second: T) -> Boolean): List<List<T>> {
    val matches = mutableListOf<MutableList<T>>()
    outer@ for (element in this) {
        for (list in matches) {
            if (matcher(list.first(), element)) {
                list += element
                continue@outer
            }
        }
        matches += mutableListOf(element)
    }
    return matches
}
