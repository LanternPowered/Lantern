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
package org.lanternpowered.api.data.immutable

import org.lanternpowered.api.data.Key
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.value.Value
import java.util.Optional
import java.util.function.Function
import java.util.function.Supplier

fun <I : DataHolder.Immutable<I>, E : Any> Optional<I>.with(key: Key<out Value<E>>, value: E): Optional<I> =
        flatMap { holder -> holder.with(key, value) }

fun <I : DataHolder.Immutable<I>, E : Any> Optional<I>.with(key: Supplier<out Key<out Value<E>>>, value: E): Optional<I> =
        flatMap { holder -> holder.with(key, value) }

fun <I : DataHolder.Immutable<I>, E : Any> Optional<I>.transform(key: Key<out Value<E>>, function: (E) -> E): Optional<I> =
        flatMap { holder -> holder.transform(key, Function(function)) }!!

fun <I : DataHolder.Immutable<I>, E : Any> Optional<I>.transform(key: Supplier<out Key<out Value<E>>>, function: (E) -> E): Optional<I> =
        flatMap { holder -> holder.transform(key, Function(function)) }!!
