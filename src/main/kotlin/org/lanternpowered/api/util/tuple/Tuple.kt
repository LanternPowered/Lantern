package org.lanternpowered.api.util.tuple

import org.lanternpowered.api.util.Tuple

// Deconstructing declaration support for tuples
operator fun <K, V> Tuple<K, V>.component1(): K = this.first
operator fun <K, V> Tuple<K, V>.component2(): V = this.second

fun <K, V> Tuple<K, V>.toPair() = Pair(this.first, this.second)
