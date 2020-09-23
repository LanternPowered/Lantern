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
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.MergeFunction
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.data.value.ValueContainer
import java.util.Optional
import java.util.function.Function

fun <I : DataHolder.Immutable<I>> I.mergeWith(container: ValueContainer): I =
        this.mergeWith(container, MergeFunction.REPLACEMENT_PREFERRED)

fun <I : DataHolder.Immutable<I>> I.mergeWithAndGetResult(container: ValueContainer, function: MergeFunction): Pair<I, DataTransactionResult> {
    val result = DataTransactionResult.builder()
    return this.mergeWithAndCollectResult0(container, function, result) to result.build()
}

fun <I : DataHolder.Immutable<I>> I.mergeWith(container: ValueContainer, function: MergeFunction): I =
        this.mergeWithAndCollectResult0(container, function, null)

fun <I : DataHolder.Immutable<I>> I.mergeWithAndCollectResult(
        container: ValueContainer, builder: DataTransactionResult.Builder
): I = this.mergeWithAndCollectResult(container, MergeFunction.REPLACEMENT_PREFERRED, builder)

fun <I : DataHolder.Immutable<I>> I.mergeWithAndCollectResult(
        container: ValueContainer, function: MergeFunction, builder: DataTransactionResult.Builder
): I = this.mergeWithAndCollectResult0(container, function, builder)

private fun <I : DataHolder.Immutable<I>> I.mergeWithAndCollectResult0(
        container: ValueContainer, function: MergeFunction, builder: DataTransactionResult.Builder?
): I {
    var result = this
    if (function == MergeFunction.REPLACEMENT_PREFERRED) {
        // There's no need to get old values here, everything
        // is replaced, if possible
        for (value in container.values) {
            val merged = result.with(value).orNull()
            if (merged != null) {
                if (builder != null) {
                    val old = result.getValue(value.key as Key<Value<Any>>).orNull()
                    if (old != null)
                        builder.replace(old.asImmutable())
                    builder.success(value)
                }
                result = merged
            } else {
                builder?.reject(value)
            }
        }
    } else {
        for (value in container.values) {
            value as Value<Any>
            val old = result.getValue(value.key as Key<Value<Any>>).orNull()
            val merged = result.with(checkNotNull(function.merge(old, value))).orNull()
            if (merged != null) {
                if (builder != null) {
                    if (old != null)
                        builder.replace(old.asImmutable())
                    builder.success(value)
                }
                result = merged
            } else {
                builder?.reject(value)
            }
        }
    }
    return result
}

interface ImmutableDataHolder<I : DataHolder.Immutable<I>> : DataHolderBase, DataHolder.Immutable<I> {

    @JvmDefault
    override fun <E : Any> transform(key: Key<out Value<E>>, function: Function<E, E>): Optional<I> {
        return this.get(key).map { value -> with(key, function.apply(value)) }.orElse(emptyOptional())
    }

    @JvmDefault
    override fun with(value: Value<*>): Optional<I> {
        val key = value.key

        // Check for a global registration
        val globalRegistration = GlobalKeyRegistry[key.uncheckedCast<Key<Value<Any>>>()]
        if (globalRegistration != null)
            return globalRegistration.dataProvider<Value<Any>, Any>().withValue(uncheckedCast(), value.uncheckedCast())

        return emptyOptional()
    }

    @JvmDefault
    override fun <E : Any> with(key: Key<out Value<E>>, value: E): Optional<I> {
        // Check for a global registration
        val globalRegistration = GlobalKeyRegistry[key]
        if (globalRegistration != null)
            return globalRegistration.dataProvider<Value<E>, E>().with(uncheckedCast(), value)

        return emptyOptional()
    }

    @JvmDefault
    override fun without(value: Value<*>): Optional<I> = super.without(value)

    @JvmDefault
    override fun without(key: Key<*>): Optional<I> {
        // Check for a global registration
        val globalRegistration = GlobalKeyRegistry[key.uncheckedCast<Key<Value<Any>>>()]
        if (globalRegistration != null)
            return globalRegistration.dataProvider<Value<Any>, Any>().without(uncheckedCast())

        return emptyOptional()
    }

    @JvmDefault
    override fun mergeWith(that: I, function: MergeFunction): I =
            (this as I).mergeWith(that as ValueContainer, function)
}
