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
import org.spongepowered.api.data.DirectionRelativeDataHolder
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.util.Direction

/**
 * A alternative of [LocalDataProviderBuilder] for Java interoperability.
 */
interface LocalJDataProviderBuilder<V : Value<E>, E : Any, H : DataHolder> {

    /**
     * Sets that async access is supported for every [DataHolder].
     */
    fun allowAsyncAccess(): LocalJDataProviderBuilder<V, E, H>

    /**
     * Set the tester that will check whether the target [DataHolder] is supported.
     *
     * @param tester The supported tester
     * @return This builder, for chaining
     */
    fun supportedBy(tester: H.() -> Boolean): LocalJDataProviderBuilder<V, E, H>

    /**
     * Sets the remove handler of this value processor, it will be called when someone attempts
     * to remove a value from the [DataHolder].
     *
     * @param handler The remove handler
     * @return This builder, for chaining
     */
    fun remove(handler: H.() -> DataTransactionResult): LocalJDataProviderBuilder<V, E, H>

    /**
     * Sets the remove handler of this value processor, it will be called when someone attempts
     * to remove a value from the [DataHolder].
     *
     * @param handler The remove handler
     * @return This builder, for chaining
     */
    fun removeFast(handler: H.() -> Boolean): LocalJDataProviderBuilder<V, E, H>

    /**
     * Sets the offer handler of this value processor that will pass a [Value] through, it
     * will be called when someone attempts to add or update a value to the [DataHolder].
     *
     * @param handler The offer handler
     * @return This builder, for chaining
     */
    fun offerValue(handler: H.(value: V) -> DataTransactionResult): LocalJDataProviderBuilder<V, E, H>

    /**
     * Sets the offer handler of this value processor that will pass a [Value] through, it
     * will be called when someone attempts to add or update a value to the [DataHolder].
     *
     * @param handler The offer handler
     * @return This builder, for chaining
     */
    fun offerValueFast(handler: H.(value: V) -> Boolean): LocalJDataProviderBuilder<V, E, H>

    /**
     * Sets the offer handler of this value processor, it will be called when someone attempts
     * to add or update a value to the [DataHolder].
     *
     * @param handler The offer handler
     * @return This builder, for chaining
     */
    fun offer(handler: H.(element: E) -> DataTransactionResult): LocalJDataProviderBuilder<V, E, H>

    /**
     * Sets the offer handler of this value processor, it will be called when someone attempts
     * to add or update a value to the [DataHolder].
     *
     * @param handler The offer handler
     * @return This builder, for chaining
     */
    fun offerFast(handler: H.(element: E) -> Boolean): LocalJDataProviderBuilder<V, E, H>

    /**
     * Sets the get handler of this value processor, it will be called when someone attempts
     * to get a element from the [DataHolder].
     *
     * @param handler The get handler
     * @return This builder, for chaining
     */
    fun get(handler: H.() -> E?): LocalJDataProviderBuilder<V, E, H>

    /**
     * Sets the directional get handler of this value processor, it will be called when someone attempts
     * to get a element from the [DataHolder].
     *
     * @param handler The get handler
     * @return This builder, for chaining
     */
    fun getDirectional(handler: H.(direction: Direction) -> E?): LocalJDataProviderBuilder<V, E, H>

    /**
     * Sets the retrieve retrieve handler of this value processor, it will be called when someone attempts
     * to get a [Value] from the [DataHolder].
     *
     * @param handler The get handler
     * @return This builder, for chaining
     */
    fun getValue(handler: H.() -> V?): LocalJDataProviderBuilder<V, E, H>

    /**
     * Sets the get handler of this value processor, it will be called when someone attempts
     * to get a [Value] from the [DataHolder].
     *
     * @param handler The get handler
     * @return This builder, for chaining
     */
    fun getValueDirectional(handler: H.(direction: Direction) -> V?): LocalJDataProviderBuilder<V, E, H>
}
