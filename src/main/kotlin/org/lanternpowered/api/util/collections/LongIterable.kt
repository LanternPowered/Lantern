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

import it.unimi.dsi.fastutil.longs.LongIterable

inline fun LongIterable.forEachLong(fn: (long: Long) -> Unit) {
    val itr = iterator()
    while (itr.hasNext())
        fn(itr.nextLong())
}
