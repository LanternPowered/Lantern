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

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import org.lanternpowered.api.shard.Shard;
import org.lanternpowered.api.shard.ShardHolder;
import org.lanternpowered.server.shards.InjectionRegistry;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Represents the dependency of a specific {@link Shard}.
 */
@Target({})
@Retention(RUNTIME)
public @interface Dependency {

    /**
     * Gets the {@link Shard} type of the dependency.
     *
     * @return The component type
     */
    Class<? extends Shard> value();

    /**
     * Gets the {@link Requirement} of the dependency.
     *
     * @return The dependency type
     */
    Requirement type() default Requirement.REQUIRED;

    /**
     * Whether the dependency can be automatically attached to
     * a {@link ShardHolder}.
     * <p>
     * This expects that when this method returns {@code true} and the {@link Shard}
     * is abstract, a default implementation is provided through
     * the {@link InjectionRegistry}.
     *
     * @return Auto attach
     */
    boolean autoAttach() default false;
}
