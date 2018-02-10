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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.function.Supplier;

/**
 * This annotation can be applied to a {@link Shard}
 * subclass to provide a default implementation.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface DefaultSupplier {

    /**
     * Applies a {@link Supplier} class that should be used to
     * provide a implementation for the target {@link Shard}. The class
     * must have a empty constructor.
     * <p>If set to {@code ComponentProvider.class}, the default behavior will
     * be used and a empty constructor of the {@link Shard} will be used.</p>
     * <p>If the {@link DefaultSupplier} annotated class is also annotated with
     * {@link ConfigSerializable} a custom {@link Supplier} will be generate
     * from {@link Setting} annotated fields from the given class, the contents of fields
     * in the generated class will be transferred to the every constructed
     * {@link Shard}.</p>
     *
     * @return The component provider class
     */
    Class<? extends Supplier> provider() default Supplier.class;
}
