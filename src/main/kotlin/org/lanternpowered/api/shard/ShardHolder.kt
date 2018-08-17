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

import org.lanternpowered.server.shards.InjectionRegistry
import org.lanternpowered.api.shard.event.ShardeventBus
import java.util.Optional
import kotlin.reflect.KClass

interface ShardHolder {

    /**
     * The [ShardeventBus] from this [ShardHolder].
     */
    val shardeventBus: ShardeventBus

    /**
     * Attempts to attach a [Shard] of the given type to this [ShardHolder],
     * if there is already a shard of the given type, then that instance will be returned.
     *
     * This method expects that when the [Shard] is either abstract or an interface,
     * a default implementation is provided through the [InjectionRegistry].
     *
     * @param type The shard type to attach
     * @return The shard instance
     */
    fun <T : Shard> addShard(type: Class<T>): Optional<T>

    /**
     * Attempts to attach a [Shard] of the given type to this [ShardHolder],
     * if there is already a shard of the given type, then that instance will be returned.
     *
     * This method expects that when the [Shard] is either abstract or an interface,
     * a default implementation is provided through the [InjectionRegistry].
     *
     * @param type The shard type to attach
     * @return The shard instance
     */
    fun <T : Shard> addShard(type: KClass<T>): Optional<T>

    /**
     * Attempts to attach the given [Shard] to this [ShardHolder]. The method
     * will return `true` if it was successful, adding a shard will be successful if there
     * isn't a [Shard] with the same type.
     *
     * @param shard The shard to attach
     * @return Whether the attachment was successful
     * @throws IllegalArgumentException If the given shard instance is already attached
     */
    @Throws(IllegalArgumentException::class)
    fun addShard(shard: Shard): Boolean

    /**
     * Attempts to replace the [Shard] attached to the given type. If there are multiple
     * ones found, only the first possible one will be replaced. If there were none found, the
     * [Shard] will just be attached to this holder.
     *
     * @param type The shard type to replace
     * @param component The new shard instance
     * @return Whether the replacement was successful
     * @throws IllegalArgumentException If the given shard instance is already attached
     */
    @Throws(IllegalArgumentException::class)
    fun <T : Shard, I : T> replaceShard(type: Class<T>, component: I): Boolean

    /**
     * Attempts to replace the [Shard] attached to the given type. If there are multiple
     * ones found, only the first possible one will be replaced. If there were none found, the
     * [Shard] will just be attached to this holder.
     *
     * @param type The shard type to replace
     * @param component The new shard instance
     * @return Whether the replacement was successful
     * @throws IllegalArgumentException If the given shard instance is already attached
     */
    @Throws(IllegalArgumentException::class)
    fun <T : Shard, I : T> replaceShard(type: KClass<T>, component: I): Boolean

    /**
     * Attempts to replace the [Shard] attached to the given type. If there are multiple
     * ones found, only the first possible one will be replaced. If there were none found, the
     * [Shard] will just be attached to this holder.
     *
     * @param type The shard type to replace
     * @param component The new shard type
     * @return The newly attached shard
     * @throws IllegalArgumentException If the given shard instance is already attached
     */
    @Throws(IllegalArgumentException::class)
    fun <T : Shard, I : T> replaceShard(type: Class<T>, component: Class<I>): Optional<I>

    /**
     * Attempts to replace the [Shard] attached to the given type. If there are multiple
     * ones found, only the first possible one will be replaced. If there were none found, the
     * [Shard] will just be attached to this holder.
     *
     * @param type The shard type to replace
     * @param component The new shard type
     * @return The newly attached shard
     * @throws IllegalArgumentException If the given shard instance is already attached
     */
    @Throws(IllegalArgumentException::class)
    fun <T : Shard, I : T> replaceShard(type: KClass<T>, component: Class<I>): I?

    /**
     * Gets the [Shard] of the given type if present, otherwise [Optional.empty].
     *
     * Only the first [Shard] will be returned if there
     * are multiple ones for the given type.
     *
     * @param type The shard type
     * @return The shard instance if present
     */
    fun <T : Shard> getShard(type: Class<T>): Optional<T>

    /**
     * Gets the [Shard] of the given type if present, otherwise [Optional.empty].
     *
     * Only the first [Shard] will be returned if there
     * are multiple ones for the given type.
     *
     * @param type The shard type
     * @return The shard instance if present
     */
    fun <T : Shard> getShard(type: KClass<T>): T?

    /**
     * Attempts to remove all the [Shard]s that match the given type, all the shards
     * that were removed will be present in the result [Collection].
     *
     * @param type The shard type
     * @return A collection with the removed shards
     */
    fun <T : Shard> removeShard(type: Class<T>): Optional<T>

    /**
     * Gets the first [Shard] of the given type.
     *
     * @param type The type
     * @return A collection with the components
     */
    fun <T : Any> getShardOfType(type: Class<T>): Optional<T>

    /**
     * Gets the first [Shard] of the given type.
     *
     * @param type The type
     * @return A collection with the components
     */
    fun <T : Any> getShardOfType(type: KClass<T>): T?

    /**
     * Gets a [Collection] with all the [Shard]s of the given type.
     *
     * @param type The type
     * @return A collection with the components
     */
    fun <T : Any> getShardsOfType(type: Class<T>): Collection<T>

    /**
     * Gets a [Collection] with all the [Shard]s of the given type.
     *
     * @param type The type
     * @return A collection with the components
     */
    fun <T : Any> getShardsOfType(type: KClass<T>): Collection<T>
}
