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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.lanternpowered.server.inject.ParameterInfo;
import org.lanternpowered.server.inject.Inject;
import org.lanternpowered.server.inject.Injector;
import org.lanternpowered.server.inject.InjectorFactory;
import org.lanternpowered.server.inject.MethodInfo;
import org.lanternpowered.server.inject.Module;
import org.lanternpowered.server.inject.ObjectSupplier;
import org.lanternpowered.server.util.cache.CacheBuilderHelper;

import com.google.common.base.Equivalence;
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
            CacheBuilderHelper.keyEquivalence(CacheBuilder.newBuilder().weakKeys(), Equivalence.equals())
                    .build(new CacheLoader<CacheKey, ReflectInjector>() {
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

        @Override
        public boolean equals(Object other) {
            if (other == null || other.getClass() != this.getClass()) {
                return false;
            }
            CacheKey o = (CacheKey) other;
            return o.module == this.module && o.type == this.type;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private ReflectInjector createInjector(CacheKey key) {
        System.out.println(key.type.getName());
        this.cacheKeys.computeIfAbsent(key.module, module -> Sets.newConcurrentHashSet()).add(key);
        ImmutableList.Builder<ReflectAbstractInfo<?>> injectInfoBuilder = ImmutableList.builder();
        for (Field field : key.type.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) && field.isAnnotationPresent(Inject.class)) {
                injectInfoBuilder.add(new ReflectFieldInfo(field.getType(), field));
            }
        }
        for (Method method : key.type.getDeclaredMethods()) {
            if (!Modifier.isStatic(method.getModifiers()) && method.isAnnotationPresent(Inject.class)
                    && method.getParameterCount() == 1) {
                injectInfoBuilder.add(new ReflectMethodInfo(method.getParameterTypes()[0],
                        method.getParameters()[0], method));
            }
        }
        try {
            return new ReflectInjectorImpl(key.type, key.module, injectInfoBuilder.build());
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
        void injectObjects(Object targetObject, Map<String, Object> parameters, Predicate<ReflectAbstractInfo<?>> predicate);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private class ReflectInjectorImpl implements ReflectInjector {

        private final List<ReflectAbstractInfo<?>> injectObjects;

        private final Class<?> superType;
        private final Module module;
        private final Supplier<?> supplier;

        public ReflectInjectorImpl(Class<?> objectType, Module module, List<ReflectAbstractInfo<?>> injectObjects) throws Exception {
            final Class<?> superType = objectType.getSuperclass();
            this.superType = superType.equals(Object.class) ? null : superType;
            Supplier<?> supplier = module.getSupplier(objectType).orElse(null);
            if (supplier == null) {
                supplier = new ReflectSupplier(objectType);
            }
            this.supplier = supplier;
            this.injectObjects = injectObjects;
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
        public void injectObjects(Object targetObject, Map<String, Object> parameters, Class<?> objectType) {
            this.injectObjects(targetObject, ImmutableMap.copyOf(parameters), info -> objectType.isAssignableFrom(info.getType()));
        }

        @Override
        public void injectObjects(Object targetObject, Map<String, Object> parameters) {
            this.injectObjects(targetObject, ImmutableMap.copyOf(parameters), info -> true);
        }

        @Override
        public void injectObjects(Object targetObject, Map<String, Object> parameters, Predicate<ReflectAbstractInfo<?>> predicate) {
            for (ReflectAbstractInfo<?> info : this.injectObjects) {
                if (predicate.test(info)) {
                    Object result = this.module.getBinding(info.getType(), info.getAnnotationTypes())
                            .getProvider().get(targetObject, parameters, (ParameterInfo) info);
                    try {
                        if (info instanceof ReflectFieldInfo) {
                            ((ReflectFieldInfo) info).field.set(targetObject, result);
                        } else if (info instanceof ReflectMethodInfo) {
                            ((ReflectMethodInfo) info).method.invoke(targetObject, result);
                        }
                    } catch (InvocationTargetException e) {
                        throw new IllegalStateException(e);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (this.superType != null) {
                ReflectInjectorFactory.this.create(this.superType,
                        this.module).injectObjects(targetObject, parameters, predicate);
            }
        }

        @Override
        public Object get() {
            return this.supplier.get();
        }

        @Override
        public <T> T injectMethod(Object targetObject, MethodInfo<T> methodInfo, Object... params) {
            return null;
        }
    }

    private static class ReflectMethodInfo<T> extends ReflectAbstractInfo<T> {

        private final Parameter parameter;
        private final Method method;

        public ReflectMethodInfo(Class<?> type, Parameter parameter, Method method) {
            super(type, parameter.getAnnotations());
            this.parameter = parameter;
            this.method = method;
            this.method.setAccessible(true);
        }

        @Override
        public <A extends Annotation> A getAnnotation(Class<A> type) {
            return this.parameter.getAnnotation(type);
        }
    }

    private static class ReflectFieldInfo<T> extends ReflectAbstractInfo<T> {

        private final Field field;

        public ReflectFieldInfo(Class<?> type, Field field) {
            super(type, field.getAnnotations());
            this.field = field;
            this.field.setAccessible(true);
        }

        @Override
        public <A extends Annotation> A getAnnotation(Class<A> type) {
            return this.field.getAnnotation(type);
        }
    }

    private static abstract class ReflectAbstractInfo<T> implements ParameterInfo<T> {

        private final Class<?> type;
        private final List<Class<? extends Annotation>> annotationTypes;
        private final List<Annotation> annotations;

        ReflectAbstractInfo(Class<?> type, Annotation[] annotations) {
            this.type = type;
            this.annotations = ImmutableList.copyOf(annotations);
            this.annotationTypes = ImmutableList.copyOf(Collections2.transform(
                    this.annotations, anno -> anno.getClass()));
        }

        @SuppressWarnings("unchecked")
        @Override
        public Class<T> getType() {
            return (Class<T>) this.type;
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
