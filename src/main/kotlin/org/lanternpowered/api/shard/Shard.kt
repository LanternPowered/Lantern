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
@file:Suppress("unused")

package org.lanternpowered.api.shard

/**
 * Represents a shard (component) that can be attached to
 * a [ShardHolder]. For every direct subclass of this
 * class will only be one instance allowed per [ShardHolder].
 * Meaning that for example only object of type {code TestComponent}
 * (direct subclass) can be present, even when extending the
 * {code TestComponent} is this not allowed. However, these components
 * can be swapped out with [ShardHolder.replaceShard].
 * If you want multiple [Shard] to implement a specific type,
 * use a interface instead, there are also utilities in the [ShardHolder]
 * to get all the instances of this type.
 *
 * @param T The type of the shard, this should
 *          always be the direct subclass of shard.
 */
abstract class Shard<T : Shard<T>> {

    /**
     * Provides access to the [ShardHolder] of this [Shard],
     * will throw [IllegalStateException] if there is no holder.
     */
    val holder: ShardHolder
        get() = this.theHolder ?: throw IllegalStateException("No holder is available.")

    // Useful properties to access shards of the shard holder

    /**
     * Provides a read only property that provides the holder as the given type. A
     * cast [Exception] will be thrown if the holder is of the wrong type.
     *
     * This property will also cause the component attachment to fail early when
     * the holder isn't of the required type.
     */
    protected inline fun <reified T : Any> requireHolderOfType() = RequiredHolderOfTypeProperty(T::class)

    /**
     * Provides a read only property that provides the first [Shard] that is of the given type.
     *                   Will only be attached when the shard is requested
     *                   through this property.This
     * type doesn't have to be a shard type, it can be any available interface. A [ShardNotAvailableException]
     * will be thrown if no shard was found.
     */
    protected inline fun <reified T : Any> requiredShardOfType() = RequiredFirstShardOfTypeProperty(T::class)

    /**
     * Provides a read only property that provides the first [Shard] that is of the given type. This
     * type doesn't have to be a shard type, it can be any available interface. A null will be returned
     * if no shard was found.
     */
    protected inline fun <reified T : Any> optionalShardOfType() = OptionalFirstShardOfTypeProperty(T::class)

    /**
     * Provides a read only property that provides the [Shard]s that are of the given type. This
     * type doesn't have to be a shard type, it can be any available interface.
     */
    protected inline fun <reified T : Any> shardsOfType() = ShardsOfTypeProperty(T::class)

    /**
     * Provides a read only property that provides the [Shard] of the given shard type.
     * A [ShardNotAvailableException] will be thrown if no shard was found.
     *
     * @param autoAttach The auto attach feature that should be used if the
     *                   given shard isn't present on the shard holder.
     *                   Will only be attached when the shard is requested
     *                   through this property.
     */
    protected inline fun <reified T : Shard<T>> requiredShard(autoAttach: AutoAttach<T> = AutoAttach.disabled()) =
            RequiredShardProperty(T::class, autoAttach)

    /**
     * Provides a read only property that provides the [Shard] of the given shard type.
     * A null will be returned if no shard was found.
     *
     * @param autoAttach The auto attach feature that should be used if the
     *                   given shard isn't present on the shard holder.
     *                   Will only be attached when the shard is requested
     *                   through this property.
     */
    protected inline fun <reified T : Shard<T>> optionalShard(autoAttach: AutoAttach<T> = AutoAttach.disabled()) =
            OptionalShardProperty(T::class, autoAttach)

    // The following fields have only internal access only, DO NOT MODIFY!

    /**
     * The holder of this [Shard], used to
     * prevent multiple holders per shard.
     */
    private val theHolder: ShardHolder? = null

    /**
     * The lock of this [Shard].
     */
    private val lock = Any()
}
