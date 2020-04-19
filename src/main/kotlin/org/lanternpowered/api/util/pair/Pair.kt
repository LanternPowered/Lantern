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
@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")

package org.lanternpowered.api.util.pair

import org.lanternpowered.api.util.Tuple

fun <K, V> Pair<K, V>.toTuple() = Tuple(first, second)
