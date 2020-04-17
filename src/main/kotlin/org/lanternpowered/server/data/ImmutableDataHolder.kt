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
@file:Suppress("UNCHECKED_CAST")

package org.lanternpowered.server.data

import org.lanternpowered.api.ext.emptyOptional
import org.lanternpowered.api.ext.orNull
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.MergeFunction
import org.spongepowered.api.data.value.Value
import java.util.Optional
import java.util.function.Function

interface ImmutableDataHolder<I : DataHolder.Immutable<I>> : DataHolderBase, DataHolder.Immutable<I> {

    @JvmDefault
    override fun <E : Any> transform(key: Key<out Value<E>>, function: Function<E, E>): Optional<I> {
        return get(key).map { value -> with(key, function.apply(value)) }.orElse(emptyOptional())
    }

    @JvmDefault
    override fun with(value: Value<*>): Optional<I> {
        val key = value.key

        // Check for a global registration
        val globalRegistration = GlobalKeyRegistry[key.uncheckedCast<Key<Value<Any>>>()]
        if (globalRegistration != null) {
            return globalRegistration.dataProvider<Value<Any>, Any>().withValue(uncheckedCast(), value.uncheckedCast())
        }

        return emptyOptional()
    }

    @JvmDefault
    override fun <E : Any> with(key: Key<out Value<E>>, value: E): Optional<I> {
        // Check for a global registration
        val globalRegistration = GlobalKeyRegistry[key]
        if (globalRegistration != null) {
            return globalRegistration.dataProvider<Value<E>, E>().with(uncheckedCast(), value)
        }

        return emptyOptional()
    }

    @JvmDefault
    override fun without(value: Value<*>): Optional<I> = super.without(value)

    @JvmDefault
    override fun without(key: Key<*>): Optional<I> {
        // Check for a global registration
        val globalRegistration = GlobalKeyRegistry[key.uncheckedCast<Key<Value<Any>>>()]
        if (globalRegistration != null) {
            return globalRegistration.dataProvider<Value<Any>, Any>().without(uncheckedCast())
        }

        return emptyOptional()
    }

    @JvmDefault
    override fun mergeWith(that: I, function: MergeFunction): I {
        var temp = this as I
        if (function == MergeFunction.REPLACEMENT_PREFERRED) {
            // There's no need to get old values here, everything
            // is replaced, if possible
            for (value in that.values) {
                val merged = temp.with(value).orNull()
                if (merged != null) {
                    temp = merged
                }
            }
        } else {
            for (value in that.values) {
                value as Value<Any>
                val old = temp.getValue(value.key as Key<Value<Any>>).orNull()
                val merged = temp.with(checkNotNull(function.merge(old, value))).orNull()
                if (merged != null) {
                    temp = merged
                }
            }
        }
        return temp
    }
}
