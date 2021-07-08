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

import kotlin.reflect.KClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lanternpowered.api.shard.Shard;
import org.lanternpowered.api.shard.ShardHolder;
import org.lanternpowered.api.shard.ShardType;
import org.lanternpowered.api.shard.event.ShardeventBus;
import org.lanternpowered.server.shards.internal.ComponentContainer;

import java.util.Collection;
import java.util.Optional;

public class AbstractComponentHolder implements ShardHolder {

    private final ComponentContainer componentContainer = new ComponentContainer(this);

    @Override
    public ShardeventBus getShardeventBus() {
        return this.componentContainer.getShardeventBus();
    }

    @NotNull @Override public <T extends Shard<T>> T addShard(@NotNull ShardType<T> type) {
        return null;
    }

    @Override public boolean addShard(@NotNull Shard<?> shard) throws IllegalArgumentException {
        return false;
    }

    @Override public <T extends Shard<T>, I extends T> boolean replaceShard(@NotNull Class<T> type, @NotNull I component)
            throws IllegalArgumentException {
        return false;
    }

    @Override public <T extends Shard<T>, I extends T> boolean replaceShard(@NotNull KClass<T> type, @NotNull I component)
            throws IllegalArgumentException {
        return false;
    }

    @NotNull @Override public <T extends Shard<T>, I extends T> Optional<I> replaceShard(@NotNull Class<T> type, @NotNull Class<I> component)
            throws IllegalArgumentException {
        return Optional.empty();
    }

    @Nullable @Override public <T extends Shard<T>, I extends T> I replaceShard(@NotNull KClass<T> type, @NotNull Class<I> component)
            throws IllegalArgumentException {
        return null;
    }

    @NotNull @Override public <T extends Shard<T>> Optional<T> getShard(@NotNull Class<T> type) {
        return Optional.empty();
    }

    @Nullable @Override public <T extends Shard<T>> T getShard(@NotNull KClass<T> type) {
        return null;
    }

    @NotNull @Override public <T extends Shard<T>> Optional<T> removeShard(@NotNull Class<T> type) {
        return Optional.empty();
    }

    @NotNull @Override public <T> Optional<T> getShardOfType(@NotNull Class<T> type) {
        return Optional.empty();
    }

    @Nullable @Override public <T> T getShardOfType(@NotNull KClass<T> type) {
        return null;
    }

    @NotNull @Override public <T> Collection<T> getShardsOfType(@NotNull Class<T> type) {
        return null;
    }

    @NotNull @Override public <T> Collection<T> getShardsOfType(@NotNull KClass<T> type) {
        return null;
    }

    @NotNull @Override public <T extends Shard<T>> Optional<T> addShard(@NotNull Class<T> type) {
        return Optional.empty();
    }

    @Nullable @Override public <T extends Shard<T>> T addShard(@NotNull KClass<T> type) {
        return null;
    }
}
