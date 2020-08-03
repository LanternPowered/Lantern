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

package org.lanternpowered.api.util.collections

import com.google.common.collect.ImmutableCollection
import com.google.common.collect.ImmutableSet

/**
 * Converts this [Iterable] into an [ImmutableSet].
 */
inline fun <T> Iterable<T>.toImmutableCollection(): ImmutableCollection<T> =
        this as? ImmutableCollection<T> ?: this.toImmutableList()

/**
 * Converts this [Array] into an [ImmutableSet].
 */
inline fun <T> Array<T>.toImmutableCollection(): ImmutableCollection<T> = this.toImmutableList()
