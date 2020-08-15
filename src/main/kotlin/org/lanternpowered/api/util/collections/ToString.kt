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
 * Converts the elements of the iterable into a string.
 */
fun <T> Iterable<T>.contentToString(): String =
        this.joinToString(separator = ", ", prefix = "[", postfix = "]")

/**
 * Converts the elements of the sequence into a string.
 */
fun <T> Sequence<T>.contentToString(): String =
        this.joinToString(separator = ", ", prefix = "[", postfix = "]")
