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

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.lanternpowered.server.inject.Binding;
import org.lanternpowered.server.inject.BindingBuilder;
import org.lanternpowered.server.inject.Module;
import org.lanternpowered.server.inject.ModuleBuilder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public final class SimpleModuleBuilder implements ModuleBuilder {

    final List<Binding<?>> bindings = Lists.newArrayList();
    final Map<Class<?>, Supplier<?>> suppliers = Maps.newHashMap();

    @Override
    public <T> BindingBuilder<T> bind(Class<T> type) {
        return new SimpleBindingBuilder<T>(type, this);
    }

    @Override
    public Module build() {
        return new SimpleModule(this.bindings, this.suppliers);
    }

    @Override
    public <T> ModuleBuilder bindInstantiator(Class<T> type, Supplier<? extends T> supplier) {
        this.suppliers.put(type, supplier);
        return this;
    }
}
