package org.lanternpowered.api.util.collections

import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap

fun <T> Iterable<IndexedValue<T>>.toMap(): Int2ObjectMap<T> {
    val map = Int2ObjectOpenHashMap<T>()
    for ((index, value) in this)
        map[index] = value
    return Int2ObjectMaps.unmodifiable(map)
}
