/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.inject.impl;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Supplier;

import org.lanternpowered.server.inject.BindingBuilder;
import org.lanternpowered.server.inject.ModuleBuilder;
import org.lanternpowered.server.inject.Provider;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

final class SimpleBindingBuilder<T> implements BindingBuilder<T> {

    private final SimpleModuleBuilder builder;
    private final Class<T> type;

    private List<Class<? extends Annotation>> annotations = Lists.newArrayList();
    private Provider<T> provider = (target, params, info) -> { return null; };

    public SimpleBindingBuilder(Class<T> type, SimpleModuleBuilder builder) {
        this.builder = builder;
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    @Override
    public BindingBuilder<T> annotatedWith(Class<? extends Annotation>... annotations) {
        this.annotations = Lists.newArrayList(annotations);
        return this;
    }

    @Override
    public ModuleBuilder toSupplier(Supplier<? extends T> supplier) {
        this.provider = (target, params, info) -> {
            return supplier.get();
        };
        return this.build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ModuleBuilder toProvider(Provider<? extends T> provider) {
        this.provider = (Provider<T>) provider;
        return this.build();
    }

    @Override
    public ModuleBuilder toInstance(T instance) {
        this.provider = (target, params, info) -> {
            return instance;
        };
        return this.build();
    }

    private ModuleBuilder build() {
        this.builder.bindings.add(new SimpleBinding<>(this.type,
                this.provider, ImmutableList.copyOf(this.annotations)));
        return this.builder;
    }
}
