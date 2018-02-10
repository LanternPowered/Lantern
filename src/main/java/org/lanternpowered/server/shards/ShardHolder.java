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
package org.lanternpowered.server.shards;

import org.lanternpowered.server.shards.event.ShardeventBus;

import java.util.Collection;
import java.util.Optional;

public interface ShardHolder {

    /**
     * Gets the {@link ShardeventBus} from this
     * {@link ShardHolder}.
     *
     * @return The shardevent bus
     */
    ShardeventBus getShardeventBus();

    /**
     * Attempts to attach a {@link Shard} of the given type to this {@link ShardHolder},
     * if there is already a shard of the given type, then that instance will be returned.
     * <p>
     * This method expects that when the {@link Shard} is either abstract or an interface,
     * a default implementation is provided through the {@link InjectionRegistry}.
     *
     * @param type The shard type to attach
     * @return The shard instance
     */
    <T extends Shard> Optional<T> addShard(Class<T> type);

    /**
     * Attempts to attach the given {@link Shard} to this {@link ShardHolder}. The method
     * will return {@code true} if it was successful, adding a shard will be successful if there
     * isn't a {@link Shard} with the same type.
     *
     * @param shard The shard to attach
     * @return Whether the attachment was successful
     * @throws IllegalArgumentException If the given shard instance is already attached
     */
    boolean addShard(Shard shard) throws IllegalArgumentException;

    /**
     * Attempts to replace the {@link Shard} attached to the given type. If there are multiple
     * ones found, only the first possible one will be replaced. If there were none found, the
     * {@link Shard} will just be attached to this holder.
     *
     * @param type The shard type to replace
     * @param component The new shard instance
     * @return Whether the replacement was successful
     * @throws IllegalArgumentException If the given shard instance is already attached
     */
    <T extends Shard, I extends T> boolean replaceShard(Class<T> type, I component) throws IllegalArgumentException;

    /**
     * Attempts to replace the {@link Shard} attached to the given type. If there are multiple
     * ones found, only the first possible one will be replaced. If there were none found, the
     * {@link Shard} will just be attached to this holder.
     *
     * @param type The shard type to replace
     * @param component The new shard type
     * @return The newly attached shard
     * @throws IllegalArgumentException If the given shard instance is already attached
     */
    <T extends Shard, I extends T> Optional<I> replaceShard(Class<T> type, Class<I> component) throws IllegalArgumentException;

    /**
     * Gets the {@link Shard} of the given type if present, otherwise {@link Optional#empty()}.
     * <p>
     * Only the first {@link Shard} will be returned if there
     * are multiple ones for the given type.
     *
     * @param type The shard type
     * @return The shard instance if present
     */
    <T extends Shard> Optional<T> getShard(Class<T> type);

    /**
     * Attempts to remove all the {@link Shard}s that match the given type, all the shards
     * that were removed will be present in the result {@link Collection}.
     *
     * @param type The shard type
     * @return A collection with the removed shards
     */
    <T extends Shard> Optional<T> removeShard(Class<T> type);

    /**
     * Gets a {@link Collection} with all the {@link Shard}s
     * of the given type.
     *
     * @param type The component type
     * @return A collection with the components
     */
    <T> Collection<T> getShardsOfType(Class<T> type);
}
