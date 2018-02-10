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
package org.lanternpowered.server.shards.dependency;

import org.lanternpowered.server.shards.Opt;
import org.lanternpowered.server.shards.Shard;
import org.lanternpowered.server.shards.ShardHolder;

import javax.annotation.Nullable;

/**
 * Represents how strict a dependency {@link Shard} type should be present
 * on a {@link ShardHolder} for the depending {@link Shard} to function.
 */
public enum Requirement {

    /**
     * The dependency {@link Shard} is optionally available for
     * the target {@link ShardHolder} in order for the depending
     * {@link Shard} to function.
     * <p>
     * The equivalent of this by using annotations is specifying a
     * {@link Opt} with a specific {@link Shard} type, for example:
     * <pre>
     * {@code
     *     @Inject Opt<FooComponent> optFooComponent;
     * }
     * </pre>
     * Another option is to mark the field or parameter
     * with {@link Nullable}, for example:
     * <pre>
     * {@code
     *     @Inject @Nullable FooComponent optFooComponent;
     * }
     * </pre>
     */
    OPTIONAL,

    /**
     * The dependency {@link Shard} must be available for the
     * the target {@link ShardHolder} in order for the depending
     * {@link Shard} to function. This also blocks the given
     * {@link Shard} from being removed from a {@link ShardHolder}.
     * <p>
     * The equivalent of this by using annotations is specifying a
     * a specific {@link Shard} type, for example:
     * <pre>
     * {@code
     *     @Inject FooComponent fooComponent;
     * }
     * </pre>
     */
    REQUIRED,
}
