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
package org.lanternpowered.server.data

import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.DirectionRelativeDataHolder
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.util.Direction

/**
 * A builder to simplify the construction of [IDataProvider]s.
 */
@LocalDataDsl
interface LocalDataProviderBuilder<V : Value<E>, E : Any, H : DataHolder> {

    /**
     * Sets that async access is supported for every [DataHolder].
     */
    fun allowAsyncAccess(): LocalDataProviderBuilder<V, E, H>

    /**
     * Set the tester that will check whether the target [DataHolder] is supported.
     *
     * @param tester The supported tester
     * @return This builder, for chaining
     */
    fun supportedBy(tester: H.() -> Boolean): LocalDataProviderBuilder<V, E, H>

    /**
     * Sets the remove handler of this value processor, it will be called when someone attempts
     * to remove a value from the [DataHolder].
     *
     * @param handler The remove handler
     * @return This builder, for chaining
     */
    fun <H : DataHolder.Mutable> LocalDataProviderBuilder<V, E, H>.remove(
            handler: H.() -> Unit): LocalDataProviderBuilder<V, E, H>

    /**
     * Sets the remove handler of this value processor, it will be called when someone attempts
     * to remove a value from the [DataHolder].
     *
     * @param handler The remove handler
     * @return This builder, for chaining
     */
    fun <H : DataHolder.Mutable> LocalDataProviderBuilder<V, E, H>.removeAnd(
            handler: H.() -> DataTransactionResult): LocalDataProviderBuilder<V, E, H>

    /**
     * Sets the remove handler of this value processor, it will be called when someone attempts
     * to remove a value from the [DataHolder].
     *
     * @param handler The remove handler
     * @return This builder, for chaining
     */
    // TODO: Rename to "remove" -> https://youtrack.jetbrains.com/issue/KT-22119
    fun <H : DataHolder.Mutable> LocalDataProviderBuilder<V, E, H>.removeFastAnd(
            handler: H.() -> Boolean): LocalDataProviderBuilder<V, E, H>

    /**
     * Sets the offer handler of this value processor that will pass a [Value] through, it
     * will be called when someone attempts to add or update a value to the [DataHolder].
     *
     * @param handler The offer handler
     * @return This builder, for chaining
     */
    fun <H : DataHolder.Mutable> LocalDataProviderBuilder<V, E, H>.setValueAnd(
            handler: H.(value: V) -> DataTransactionResult): LocalDataProviderBuilder<V, E, H>

    /**
     * Sets the offer handler of this value processor that will pass a [Value] through, it
     * will be called when someone attempts to add or update a value to the
     * mutable [DataHolder].
     *
     * @param handler The offer handler
     * @return This builder, for chaining
     */
    // TODO: Rename to "offerValue" -> https://youtrack.jetbrains.com/issue/KT-22119
    fun <H : DataHolder.Mutable> LocalDataProviderBuilder<V, E, H>.setValueFastAnd(
            handler: H.(value: V) -> Boolean): LocalDataProviderBuilder<V, E, H>

    /**
     * Sets the offer handler of this value processor, it will be called when someone attempts
     * to add or update a value to the mutable [DataHolder].
     *
     * @param handler The offer handler
     * @return This builder, for chaining
     */
    fun <H : DataHolder.Mutable> LocalDataProviderBuilder<V, E, H>.set(
            handler: H.(element: E) -> Unit): LocalDataProviderBuilder<V, E, H>

    /**
     * Sets the offer handler of this value processor, it will be called when someone attempts
     * to add or update a value to the mutable [DataHolder].
     *
     * @param handler The offer handler
     * @return This builder, for chaining
     */
    fun <H : DataHolder.Mutable> LocalDataProviderBuilder<V, E, H>.setAnd(
            handler: H.(element: E) -> DataTransactionResult): LocalDataProviderBuilder<V, E, H>

    /**
     * Sets the offer handler of this value processor, it will be called when someone attempts
     * to add or update a value to the mutable [DataHolder].
     *
     * @param handler The offer handler
     * @return This builder, for chaining
     */
    // TODO: Rename to "offer" -> https://youtrack.jetbrains.com/issue/KT-22119
    fun <H : DataHolder.Mutable> LocalDataProviderBuilder<V, E, H>.setFastAnd(
            handler: H.(element: E) -> Boolean): LocalDataProviderBuilder<V, E, H>

    /**
     * Sets the with handler (immutable offer handler) of this value processor, it will be
     * called when someone attempts to add or update a value to the immutable [DataHolder].
     *
     * @param handler The with handler
     * @return This builder, for chaining
     */
    fun <H : DataHolder.Immutable<H>> LocalDataProviderBuilder<V, E, H>.with(
            handler: H.(element: E) -> H?): LocalDataProviderBuilder<V, E, H>

    /**
     * Sets the with value handler (immutable value offer handler) of this value processor, it will be
     * called when someone attempts to add or update a value to the immutable [DataHolder].
     *
     * @param handler The with value handler
     * @return This builder, for chaining
     */
    fun <H : DataHolder.Immutable<H>> LocalDataProviderBuilder<V, E, H>.withValue(
            handler: H.(element: V) -> H?): LocalDataProviderBuilder<V, E, H>

    /**
     * Sets the without handler (immutable remove handler) of this value processor, it will be
     * called when someone attempts to add or update a value to the immutable [DataHolder].
     *
     * @param handler The without handler
     * @return This builder, for chaining
     */
    fun <H : DataHolder.Immutable<H>> LocalDataProviderBuilder<V, E, H>.without(
            handler: H.() -> H?): LocalDataProviderBuilder<V, E, H>

    /**
     * Sets the get handler of this value processor, it will be called when someone attempts
     * to get a element from the [DataHolder].
     *
     * @param handler The get handler
     * @return This builder, for chaining
     */
    fun get(handler: H.() -> E?): LocalDataProviderBuilder<V, E, H>

    /**
     * Sets the directional get handler of this value processor, it will be called when someone attempts
     * to get a element from the [DataHolder].
     *
     * @param handler The get directional handler
     * @return This builder, for chaining
     */
    fun <H : DirectionRelativeDataHolder> LocalDataProviderBuilder<V, E, H>.getDirectional(
            handler: H.(direction: Direction) -> E?): LocalDataProviderBuilder<V, E, H>

    /**
     * Sets the get handler of this value processor, it will be called when someone attempts
     * to get a [Value] from the [DataHolder].
     *
     * @param handler The retrieve handler
     * @return This builder, for chaining
     */
    fun getValue(handler: H.() -> V?): LocalDataProviderBuilder<V, E, H>

    /**
     * Sets the get handler of this value processor, it will be called when someone attempts
     * to get a [Value] from the [DataHolder].
     *
     * @param handler The retrieve handler
     * @return This builder, for chaining
     */
    fun <H : DirectionRelativeDataHolder> LocalDataProviderBuilder<V, E, H>.getValueDirectional(
            handler: H.(direction: Direction) -> V?): LocalDataProviderBuilder<V, E, H>
}
