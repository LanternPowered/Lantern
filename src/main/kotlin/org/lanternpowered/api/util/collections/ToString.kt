package org.lanternpowered.api.util.collections

fun <T> Iterable<T>.contentToString(): String =
        joinToString(separator = ", ", prefix = "[", postfix = "]")
