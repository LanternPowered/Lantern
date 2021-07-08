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
package org.lanternpowered.api.shard

import kotlin.reflect.KClass

interface ShardRegistry {

    /**
     * Gets the [ShardType] for the given shard class.
     *
     * The generic [Shard] type must be resolvable, otherwise is the
     * given shard class invalid to have a [ShardType], and a
     * [IllegalArgumentException] will be thrown.
     */
    fun <T : Shard<T>> getType(shardClass: KClass<out T>) = getType(shardClass.java)

    /**
     * Gets the [ShardType] for the given shard class.
     *
     * The generic [Shard] type must be resolvable, otherwise is the
     * given shard class invalid to have a [ShardType], and a
     * [IllegalArgumentException] will be thrown.
     */
    fun <T : Shard<T>> getType(shardClass: Class<out T>): ShardType<T>

    /**
     * Registers the default implementation for the [ShardType]. Multiple default
     * implementations can be registered, the first applicable one will
     * be used when a default one is requested.
     */
    fun <T : Shard<T>, I : T> registerDefault(shardType: ShardType<T>, defaultImpl: Class<I>)

    /**
     * Registers the default implementation for the [ShardType]. Multiple default
     * implementations can be registered, the first applicable one will
     * be used when a default one is requested.
     */
    fun <T : Shard<T>, I : T> registerDefault(shardType: ShardType<T>, defaultImpl: KClass<I>) {
        registerDefault(shardType, defaultImpl.java)
    }

    /**
     * Registers the default implementation for the [Shard] type. Multiple default
     * implementations can be registered, the first applicable one will
     * be used when a default one is requested.
     */
    fun <T : Shard<T>, I : T> registerDefault(shardType: KClass<T>, defaultImpl: KClass<I>) {
        registerDefault(getType(shardType), defaultImpl)
    }

    /**
     * Registers the default implementation for the [Shard] type. Multiple default
     * implementations can be registered, the first applicable one will
     * be used when a default one is requested.
     */
    fun <T : Shard<T>, I : T> registerDefault(shardType: Class<T>, defaultImpl: Class<I>) {
        registerDefault(getType(shardType), defaultImpl)
    }
}
