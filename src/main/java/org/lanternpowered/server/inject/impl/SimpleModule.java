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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.lanternpowered.server.inject.Binding;
import org.lanternpowered.server.inject.Module;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public final class SimpleModule implements Module {

    private final Map<Class<?>, Supplier<?>> suppliers;
    private final Map<Class<?>, List<Binding<?>>> bindings;
    private final LoadingCache<CacheKey, Binding<?>> cache =
            CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES)
                    .build(new CacheLoader<CacheKey, Binding<?>>() {
                @Override
                public Binding<?> load(CacheKey key) throws Exception {
                    Binding<?> binding = null;
                    Class<?> clazz = key.type;
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

    private Binding<?> match(List<Binding<?>> bindings, CacheKey key) {
        Binding<?> binding = null;
        int mostAnnos = 0;
        for (Binding<?> binding0 : bindings) {
            int annos = 0;
            for (Class<?> anno : key.annotations) {
                if (binding0.getAnnotations().contains(anno)) {
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

    private static class CacheKey {

        private final Class<?> type;
        private final List<Class<? extends Annotation>> annotations;

        public CacheKey(Class<?> type, ImmutableList<Class<? extends Annotation>> annotations) {
            this.annotations = annotations;
            this.type = type;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(this.annotations)
                    .append(this.type)
                    .toHashCode();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public SimpleModule(List<Binding<?>> bindings, Map<Class<?>, Supplier<?>> suppliers) {
        Map<Class<?>, List<Binding<?>>> map = Maps.newConcurrentMap();
        for (Binding<?> binding : bindings) {
            map.computeIfAbsent(binding.getType(), type ->
                Lists.newCopyOnWriteArrayList()).add(binding);
        }
        this.bindings = (Map) ImmutableMap.copyOf(Maps.transformValues(map,
                value -> ImmutableList.copyOf(value)));
        this.suppliers = ImmutableMap.copyOf(suppliers);
    }

    @Override
    public <T> Binding<T> getBinding(Class<? extends T> type) {
        return this.getBinding(type, Lists.newArrayList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Binding<T> getBinding(Class<? extends T> type, Iterable<Class<? extends Annotation>> annotationTypes) {
        try {
            List<Class<? extends Annotation>> annotationTypes0 =
                    Lists.newArrayList(annotationTypes);
            // Order doesn't really matter, must just be the same for all keys
            Collections.sort(annotationTypes0, (o1, o2) -> o1.hashCode() - o2.hashCode());
            return (Binding<T>) this.cache.get(new CacheKey(type, ImmutableList.copyOf(annotationTypes0)));
        } catch (ExecutionException e) {
            throw new RuntimeException();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public <T> Optional<Supplier<T>> getSupplier(Class<T> type) {
        return Optional.ofNullable((Supplier) this.suppliers.get(type));
    }
}
