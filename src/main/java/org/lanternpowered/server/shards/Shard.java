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

import javax.annotation.Nullable;

/**
 * Represents a shard (component) that can be attached to
 * a {@link ShardHolder}. For every direct subclass of this
 * class will only be one instance allowed per {@link ShardHolder}.
 * Meaning that for example only object of type {code TestComponent}
 * (direct subclass) can be present, even when extending the
 * {code TestComponent} is this not allowed. However, these components
 * can be swapped out with {@link ShardHolder#replaceShard(Class, Shard)}.
 * If you want multiple {@link Shard} to implement a specific type,
 * use a interface instead, there are also utilities in the {@link ShardHolder}
 * to get all the instances of this type.
 */
public abstract class Shard {

    // The following fields have only internal access only, DO NOT MODIFY!

    /**
     * The holder of this {@link Shard}, used to
     * prevent multiple holders per shard.
     */
    @Nullable private ShardHolder holder;

    /**
     * The lock of this {@link Shard}.
     */
    private final Object lock = new Object();
}
