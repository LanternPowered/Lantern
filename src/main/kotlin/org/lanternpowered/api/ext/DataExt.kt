/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.ext

import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.value.ValueContainer
import org.spongepowered.api.data.value.mutable.CompositeValueStore
import kotlin.reflect.KClass

inline fun <H : ValueContainer<*>, V: H> CompositeValueStore<*, H>.get(containerClass: KClass<V>): H? = !get(containerClass.java)
inline fun <H : ValueContainer<*>, reified V: H> CompositeValueStore<*, H>.get(): H? = !get(V::class.java)
inline fun <H : ValueContainer<*>, V: H> CompositeValueStore<*, H>.getOrCreate(containerClass: KClass<V>): H? = !getOrCreate(containerClass.java)
inline fun <H : ValueContainer<*>, reified V: H> CompositeValueStore<*, H>.getOrCreate(): H? = !getOrCreate(V::class.java)
inline fun <H : ValueContainer<*>, V: H> CompositeValueStore<*, H>.remove(containerClass: KClass<V>): DataTransactionResult = remove(containerClass.java)
inline fun <H : ValueContainer<*>, reified V: H> CompositeValueStore<*, H>.remove(): DataTransactionResult = remove(V::class.java)
inline fun <H : ValueContainer<*>, V: H> CompositeValueStore<*, H>.require(containerClass: KClass<V>): H = require(containerClass.java)
inline fun <H : ValueContainer<*>, reified V: H> CompositeValueStore<*, H>.require(): H = require(V::class.java)
inline fun <H : ValueContainer<*>, V: H> CompositeValueStore<*, H>.supports(containerClass: KClass<V>): Boolean = supports(containerClass.java)
inline fun <H : ValueContainer<*>, reified V: H> CompositeValueStore<*, H>.supports(): Boolean = supports(V::class.java)
