@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")

package org.lanternpowered.api.util

inline fun <T> Any?.uncheckedCast(): T = this as T
