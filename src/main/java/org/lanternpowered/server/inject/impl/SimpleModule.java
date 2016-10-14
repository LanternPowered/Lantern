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
package org.lanternpowered.server.inject.impl;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.lanternpowered.server.inject.Binding;
import org.lanternpowered.server.inject.MethodSpec;
import org.lanternpowered.server.inject.Module;
import org.lanternpowered.server.inject.ParameterSpec;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

final class SimpleModule implements Module {

    private final List<MethodSpec<?>> methodBindings;
    private final Map<Class<?>, Supplier<?>> suppliers;
    private final Map<Class<?>, List<Binding<?>>> bindings;
    private final LoadingCache<ParameterSpec<?>, Binding<?>> cache =
            Caffeine.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES)
                    .build(new CacheLoader<ParameterSpec<?>, Binding<?>>() {
                @Override
                public Binding<?> load(ParameterSpec<?> key) throws Exception {
                    Binding<?> binding = null;
                    Class<?> clazz = key.getType();
                    while (binding == null && clazz != Object.class) {
                        List<Binding<?>> bindings0 = bindings.get(clazz);
                        if (bindings0 != null) {
                            binding = match(bindings0, key);
                        }
                        if (binding == null) {
                            for (Class<?> interf : clazz.getInterfaces()) {
                                bindings0 = bindings.get(interf);
                                if (bindings0 != null) {
                                    binding = match(bindings0, key);
                                    break;
                                }
                            }
                        }
                        clazz = clazz.getSuperclass();
                    }
                    return binding;
                }
            });

    private Binding<?> match(List<Binding<?>> bindings, ParameterSpec<?> key) {
        Binding<?> binding = null;
        int mostAnnos = 0;
        for (Binding<?> binding0 : bindings) {
            int annos = 0;
            for (Class<? extends Annotation> anno : key.getAnnotationTypes()) {
                if (binding0.getParameterSpec().getAnnotationTypes().contains(anno)) {
                    annos++;
                }
            }
            if (binding == null || annos > mostAnnos) {
                binding = binding0;
                mostAnnos = annos;
            }
        }
        return binding;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public SimpleModule(List<Binding<?>> bindings, Map<Class<?>, Supplier<?>> suppliers,
            List<MethodSpec<?>> methodBindings) {
        Map<Class<?>, List<Binding<?>>> map = Maps.newConcurrentMap();
        for (Binding<?> binding : bindings) {
            map.computeIfAbsent(binding.getParameterSpec().getType(), type ->
                Lists.newCopyOnWriteArrayList()).add(binding);
        }
        this.methodBindings = ImmutableList.copyOf(methodBindings);
        this.bindings = (Map) ImmutableMap.copyOf(Maps.transformValues(map, ImmutableList::copyOf));
        this.suppliers = ImmutableMap.copyOf(suppliers);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Binding<T> getBinding(ParameterSpec<? extends T> spec) {
        return (Binding<T>) this.cache.get(spec);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public <T> Optional<Supplier<T>> getSupplier(Class<T> type) {
        return Optional.ofNullable((Supplier) this.suppliers.get(type));
    }

    @Override
    public List<MethodSpec<?>> getMethodBindings() {
        return this.methodBindings;
    }
}
