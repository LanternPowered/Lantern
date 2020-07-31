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
package org.lanternpowered.api.data.holder

import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import java.util.function.Function
import java.util.function.Supplier

fun <E> DataHolder.Mutable.transform(key: Key<out Value<E>>, function: (E) -> E): DataTransactionResult =
        this.transform(key, Function(function))

fun <E> DataHolder.Mutable.transform(key: Supplier<out Key<out Value<E>>>, function: (E) -> E): DataTransactionResult =
        this.transform(key, Function(function))
