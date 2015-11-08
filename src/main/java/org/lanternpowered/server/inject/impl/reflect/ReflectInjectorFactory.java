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
package org.lanternpowered.server.inject.impl.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.lanternpowered.server.inject.FieldInfo;
import org.lanternpowered.server.inject.Inject;
import org.lanternpowered.server.inject.Injector;
import org.lanternpowered.server.inject.InjectorFactory;
import org.lanternpowered.server.inject.Module;
import org.lanternpowered.server.inject.ObjectSupplier;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;

public final class ReflectInjectorFactory implements InjectorFactory, ObjectSupplier {

    private final static ReflectInjectorFactory instance = new ReflectInjectorFactory();

    public static ReflectInjectorFactory instance() {
        return instance;
    }

    private final Map<Module, Set<CacheKey>> cacheKeys = new MapMaker().weakKeys().makeMap();
    private final LoadingCache<CacheKey, ReflectInjector> cache =
            CacheBuilder.newBuilder().weakKeys().build(new CacheLoader<CacheKey, ReflectInjector>() {
                @Override
                public ReflectInjector load(CacheKey key) throws Exception {
                    return createInjector(key);
                }
            });

    private static class CacheKey {

        private final Module module;
        private final Class<?> type;

        public CacheKey(Module module, Class<?> type) {
            this.module = module;
            this.type = type;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(this.module)
                    .append(this.type)
                    .toHashCode();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private ReflectInjector createInjector(CacheKey key) {
        this.cacheKeys.computeIfAbsent(key.module, module -> Sets.newConcurrentHashSet()).add(key);
        ImmutableList.Builder<ReflectFieldInfo<?>> injectFieldsBuilder = ImmutableList.builder();
        for (Field field : key.type.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                injectFieldsBuilder.add(new ReflectFieldInfo(field, field.getType()));
            }
        }
        try {
            return new ReflectInjectorImpl(key.type, key.module, injectFieldsBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ReflectInjector create(Class<?> objectType, Module module) {
        try {
            return this.cache.get(new CacheKey(module, objectType));
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Class<T> objectType, Module module) {
        try {
            return (T) this.cache.get(new CacheKey(module, objectType)).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public interface ReflectInjector extends Injector, Supplier<Object> {
        void injectFields(Object targetObject, Map<String, Object> parameters, Predicate<ReflectFieldInfo<?>> predicate);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private class ReflectInjectorImpl implements ReflectInjector {

        private final List<ReflectFieldInfo<?>> injectFields;
        private final Class<?> superType;
        private final Module module;
        private final Supplier<?> supplier;

        public ReflectInjectorImpl(Class<?> objectType, Module module, List<ReflectFieldInfo<?>> injectFields) throws Exception {
            final Class<?> superType = objectType.getSuperclass();
            this.superType = superType.equals(Object.class) ? null : superType;
            Supplier<?> supplier = module.getSupplier(objectType).orElse(null);
            if (supplier == null) {
                supplier = new ReflectSupplier(objectType);
            }
            this.supplier = supplier;
            this.injectFields = injectFields;
            this.module = module;
        }

        private class ReflectSupplier<T> implements Supplier<T> {

            private final Constructor<?> constr;

            public ReflectSupplier(Class<?> type) throws Exception {
                this.constr = type.getDeclaredConstructor();
                this.constr.setAccessible(true);
            }

            @Override
            public T get() {
                try {
                    return (T) this.constr.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

        @Override
        public void injectFields(Object targetObject, Map<String, Object> parameters, Class<?> objectType) {
            this.injectFields(targetObject, ImmutableMap.copyOf(parameters), info -> objectType.isAssignableFrom(info.getType()));
        }

        @Override
        public void injectFields(Object targetObject, Map<String, Object> parameters) {
            this.injectFields(targetObject, ImmutableMap.copyOf(parameters), info -> true);
        }

        @Override
        public void injectFields(Object targetObject, Map<String, Object> parameters, Predicate<ReflectFieldInfo<?>> predicate) {
            for (ReflectFieldInfo<?> info : this.injectFields) {
                if (predicate.test(info)) {
                    try {
                        info.field.set(targetObject, this.module.getBinding(info.type, info.getAnnotationTypes())
                                .getProvider().get(targetObject, parameters, (FieldInfo) info));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (this.superType != null) {
                ReflectInjectorFactory.this.create(this.superType,
                        this.module).injectFields(targetObject, parameters, predicate);
            }
        }

        @Override
        public Object get() {
            return this.supplier.get();
        }
    }

    private static class ReflectFieldInfo<T> implements FieldInfo<T> {

        private final Field field;
        private final Class<?> type;
        private final List<Class<? extends Annotation>> annotationTypes;
        private final List<Annotation> annotations;

        public ReflectFieldInfo(Field field, Class<?> type) {
            this.field = field;
            this.type = type;
            this.annotations = ImmutableList.copyOf(field.getAnnotations());
            this.annotationTypes = ImmutableList.copyOf(Collections2.transform(
                    this.annotations, anno -> anno.getClass()));
        }

        @SuppressWarnings("unchecked")
        @Override
        public Class<T> getType() {
            return (Class<T>) this.type;
        }

        @Override
        public <A extends Annotation> A getAnnotation(Class<A> type) {
            return this.field.getAnnotation(type);
        }

        @Override
        public List<Annotation> getAnnotations() {
            return this.annotations;
        }

        @Override
        public List<Class<? extends Annotation>> getAnnotationTypes() {
            return this.annotationTypes;
        }
    }
}
