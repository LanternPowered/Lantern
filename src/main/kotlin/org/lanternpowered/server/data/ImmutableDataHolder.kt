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
@file:Suppress("UNCHECKED_CAST")

package org.lanternpowered.server.data

import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.api.util.optional.orNull
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
