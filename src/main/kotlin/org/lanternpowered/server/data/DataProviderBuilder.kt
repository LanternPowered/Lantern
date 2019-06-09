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
package org.lanternpowered.server.data

import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value

/**
 * A builder to simplify the construction of [IDataProvider]s.
 */
interface DataProviderBuilder<V : Value<E>, E : Any> {

    companion object {

        /**
         * Creates a new [DataProviderBuilder] for the given [Key].
         */
        fun <V : Value<E>, E : Any> of(key: Key<V>, fn: DataProviderBuilder<V, E>.(key: Key<V>) -> Unit = {}): DataProviderBuilder<V, E> {
            return LanternDataProviderBuilder(key).apply { fn(key) }
        }
    }

    /**
     * Sets the tester that will check whether async access is supported for the [DataHolder].
     */
    fun allowAsyncAccess(tester: DataHolder.() -> Boolean): DataProviderBuilder<V, E>

    /**
     * Sets that async access is supported for every [DataHolder].
     */
    fun allowAsyncAccess() = allowAsyncAccess { true }

    /**
     * Set the tester that will check whether the target [Key] is supported for the [DataHolder].
     *
     * @param tester The supported tester
     * @return This builder, for chaining
     */
    fun supportedBy(tester: DataHolder.() -> Boolean): DataProviderBuilder<V, E>

    /**
     * Sets the remove handler of this value processor, it will be called when someone attempts
     * to remove a value from the [DataHolder].
     *
     * @param handler The remove handler
     * @return This builder, for chaining
     */
    fun remove(handler: DataHolder.Mutable.() -> DataTransactionResult): DataProviderBuilder<V, E>

    /**
     * Sets the remove handler of this value processor, it will be called when someone attempts
     * to remove a value from the [DataHolder].
     *
     * @param handler The remove handler
     * @return This builder, for chaining
     */
    fun removeFast(handler: DataHolder.Mutable.() -> Boolean): DataProviderBuilder<V, E>

    /**
     * Sets the offer handler of this value processor that will pass a [Value] through, it
     * will be called when someone attempts to add or update a value to the [DataHolder].
     *
     * @param handler The offer handler
     * @return This builder, for chaining
     */
    fun offerValue(handler: DataHolder.Mutable.(value: V) -> DataTransactionResult): DataProviderBuilder<V, E>

    /**
     * Sets the offer handler of this value processor that will pass a [Value] through, it
     * will be called when someone attempts to add or update a value to the [DataHolder].
     *
     * @param handler The offer handler
     * @return This builder, for chaining
     */
    fun offerValueFast(handler: DataHolder.Mutable.(value: V) -> Boolean): DataProviderBuilder<V, E>

    /**
     * Sets the offer handler of this value processor, it will be called when someone attempts
     * to add or update a value to the [DataHolder].
     *
     * @param handler The offer handler
     * @return This builder, for chaining
     */
    fun offer(handler: DataHolder.Mutable.(element: E) -> DataTransactionResult): DataProviderBuilder<V, E>

    /**
     * Sets the offer handler of this value processor, it will be called when someone attempts
     * to add or update a value to the [DataHolder].
     *
     * @param handler The offer handler
     * @return This builder, for chaining
     */
    fun offerFast(handler: DataHolder.Mutable.(element: E) -> Boolean): DataProviderBuilder<V, E>

    /**
     * Sets the retrieve handler of this value processor, it will be called when someone attempts
     * to get a element from the [DataHolder].
     *
     * @param handler The get handler
     * @return This builder, for chaining
     */
    fun get(handler: DataHolder.() -> E?): DataProviderBuilder<V, E>

    /**
     * Sets the retrieve retrieve handler of this value processor, it will be called when someone attempts
     * to get a [Value] from the [DataHolder].
     *
     * @param handler The get value handler
     * @return This builder, for chaining
     */
    fun getValue(handler: DataHolder.() -> V?): DataProviderBuilder<V, E>

    /**
     * Sets the offer handler of this value processor that will pass a [Value] through, it
     * will be called when someone attempts to add or update a value to the [DataHolder].
     *
     * @param handler The offer handler
     * @return This builder, for chaining
     */
    fun <I : DataHolder.Immutable<I>> withValue(handler: I.(value: V) -> I?): DataProviderBuilder<V, E>

    /**
     * Sets the offer handler of this value processor, it will be called when someone attempts
     * to add or update a value to the [DataHolder].
     *
     * @param handler The offer handler
     * @return This builder, for chaining
     */
    fun <I : DataHolder.Immutable<I>> with(handler: I.(element: E) -> I?): DataProviderBuilder<V, E>

    /**
     * Sets the offer handler of this value processor, it will be called when someone attempts
     * to remove the key from the [DataHolder].
     *
     * @param handler The offer handler
     * @return This builder, for chaining
     */
    fun <I : DataHolder.Immutable<I>> without(handler: I.() -> I?): DataProviderBuilder<V, E>

    /**
     * Builds the [IDataProvider].
     *
     * @return The data provider
     */
    fun build(): IDataProvider<V, E>
}
