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

fun <E : Any> MutableList<E>.asNonNullList(): MutableList<E> = asCheckedList { element ->
    @Suppress("SENSELESS_COMPARISON")
    if (element == null)
        throw IllegalStateException("This list doesn't allow null elements.")
}
