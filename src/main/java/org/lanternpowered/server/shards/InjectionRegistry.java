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

import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

import java.util.Optional;
import java.util.function.Supplier;

public interface InjectionRegistry {

    /**
     * Creates a {@link AnnotatedBindingBuilder} for
     * the given object {@link Class}.
     *
     * @param objectType The object type
     * @param <T> The object type
     * @return The binding builder
     */
    default <T> AnnotatedBindingBuilder<T> bind(Class<T> objectType) {
        return bind(TypeToken.of(objectType));
    }

    /**
     * Creates a {@link AnnotatedBindingBuilder} for
     * the given object {@link TypeToken}.
     *
     * @param objectType The object type
     * @param <T> The object type
     * @return The binding builder
     */
    <T> AnnotatedBindingBuilder<T> bind(TypeToken<T> objectType);

    /**
     * Binds a global {@link SupplierFactory}.
     *
     * @param supplierFactory The supplier factory
     */
    void bindFactory(SupplierFactory<?> supplierFactory);

    /**
     * Gets a {@link Supplier} for the given
     * object {@link Class}.
     *
     * @param objectType The object type
     * @param <T> The object type
     * @return The supplier
     */
    default <T> Optional<Supplier<T>> getSupplier(Class<T> objectType) {
        return getSupplier(TypeToken.of(objectType));
    }

    /**
     * Gets a {@link Supplier} for the given
     * object {@link TypeToken}.
     *
     * @param objectType The object type
     * @param <T> The object type
     * @return The supplier
     */
    <T> Optional<Supplier<T>> getSupplier(TypeToken<T> objectType);

    /**
     * Gets a {@link Supplier} for the given
     * {@link InjectableType}.
     *
     * @param injectableType The injectable type
     * @param <T> The object type
     * @return The supplier
     */
    <T> Optional<Supplier<T>> getSupplier(InjectableType<T> injectableType);

    /**
     * Installs the guice {@link Module} to provide
     * additional injections.
     *
     * @param module The module
     */
    void install(Module module);

    /**
     * Creates a new child {@link InjectionRegistry}
     * from this registry.
     *
     * @return The child injection registry
     */
    InjectionRegistry newChild();
}
