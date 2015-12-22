/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.lanternpowered.server.inject.Inject;
import org.lanternpowered.server.inject.Injector;
import org.lanternpowered.server.inject.MethodSpec;
import org.lanternpowered.server.inject.Module;
import org.lanternpowered.server.inject.ParameterInfo;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

final class ReflectInjector implements Injector {

    private final Module module;
    private final LoadingCache<Class<?>, TypeInjector> cache =
            CacheBuilder.newBuilder().weakKeys().build(new CacheLoader<Class<?>, TypeInjector>() {
                @Override
                public TypeInjector load(Class<?> key) throws Exception {
                    return new TypeInjector(key);
                }
            });

    public ReflectInjector(Module module) {
        this.module = module;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T instantiate(Class<T> objectType) {
        return (T) getInjector(objectType).supplier.get();
    }

    private TypeInjector getInjector(Class<?> key) {
        try {
            return this.cache.get(key);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void injectObjects(Object targetObject, Map<String, Object> parameters, Predicate<ParameterInfo<?>> predicate) {
        this.getInjector(targetObject.getClass()).injectObjects(targetObject, parameters, predicate, null);
    }

    @Override
    public void injectObjects(Object targetObject, Map<String, Object> parameters, Class<?> objectType) {
        this.injectObjects(targetObject, parameters, info -> info.getType().isAssignableFrom(objectType));
    }

    @Override
    public void injectObjects(Object targetObject, Map<String, Object> parameters) {
        this.injectObjects(targetObject, parameters, info -> true);
    }

    @Override
    public <T> List<T> injectMethod(Object targetObject, MethodSpec<T> spec, Object... parameters) {
        return this.getInjector(targetObject.getClass()).injectMethods(targetObject, spec, parameters);
    }

    private final class TypeInjector {

        private final Map<MethodSpec<?>, List<Method>> methodBindings;
        private final List<ReflectParameterInfo<?,?>> injectObjectParameterInfos;
        private final Set<IgnoreEntry> ignoreMethods;

        private final class IgnoreEntry {

            private final Method method;
            @Nullable private final MethodSpec<?> spec;

            public IgnoreEntry(Method method, @Nullable MethodSpec<?> spec) {
                this.method = method;
                this.spec = spec;
            }

            @Override
            public boolean equals(Object other) {
                if (other == null || other.getClass() != this.getClass()) {
                    return false;
                }
                IgnoreEntry o = (IgnoreEntry) other;
                return o.method.equals(this.method) && Objects.equals(o.spec, this.spec);
            }

            @Override
            public int hashCode() {
                return Objects.hash(this.method, this.spec);
            }

            @Override
            public String toString() {
                return new ToStringBuilder(this)
                        .append(this.method)
                        .append(this.spec)
                        .build();
            }
        }

        private final Class<?> superType;
        private final Supplier<?> supplier;

        @SuppressWarnings({"unchecked"})
        public TypeInjector(Class<?> objectType) {
            final Class<?> superType = objectType.getSuperclass();
            this.superType = Object.class == superType ? null : superType;
            final Supplier<?> supplier = module.getSupplier(objectType).orElse(null);
            this.supplier = supplier == null ? new ReflectSupplier(objectType) : supplier;
            ImmutableList.Builder<ReflectParameterInfo<?,?>> injectBuilder = ImmutableList.builder();
            ImmutableSet.Builder<IgnoreEntry> ignoreBuilder = ImmutableSet.builder();
            for (Field field : objectType.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) && field.isAnnotationPresent(Inject.class)) {
                    field.setAccessible(true);
                    injectBuilder.add(new ReflectParameterInfo<Object, Field>(field, field,
                            (Class<Object>) field.getType()));
                }
            }
            Map<MethodSpec<?>, ImmutableList.Builder<Method>> methodBindings = Maps.newHashMap();
            for (Method method : objectType.getDeclaredMethods()) {
                int modifiers = method.getModifiers();
                if (Modifier.isStatic(modifiers)) {
                    continue;
                }
                if (method.isAnnotationPresent(Inject.class) && method.getParameterCount() == 1) {
                    method.setAccessible(true);
                    ReflectParameterInfo<Object, Method> info = new ReflectParameterInfo<Object, Method>(
                            method.getParameters()[0], method, (Class<Object>) method.getParameterTypes()[0]);
                    injectBuilder.add(info);
                    this.matchSuperMethods(null, objectType, method, ignoreBuilder);
                }
                for (MethodSpec<?> binding0 : module.getMethodBindings()) {
                    if (!Arrays.equals(method.getParameterTypes(), binding0.getParameterTypes().toArray(
                            new Class[0]))) {
                        continue;
                    }
                    List<Class<? extends Annotation>> annoTypes = Lists.newArrayList(Collections2.transform(
                            Arrays.asList(method.getAnnotations()), anno -> anno.annotationType()));
                    if (annoTypes.containsAll(binding0.getAnnotationTypes())) {
                        method.setAccessible(true);
                        methodBindings.computeIfAbsent(binding0, binding1 -> ImmutableList.builder()).add(method);
                        this.matchSuperMethods(binding0, objectType, method, ignoreBuilder);
                    }
                }
            }
            this.methodBindings = ImmutableMap.copyOf(Maps.transformValues(methodBindings,
                    value -> value.build()));
            this.injectObjectParameterInfos = injectBuilder.build();
            this.ignoreMethods = ignoreBuilder.build();
        }

        private void matchSuperMethods(@Nullable MethodSpec<?> spec, Class<?> objectType, Method method,
                ImmutableSet.Builder<IgnoreEntry> ignoreBuilder) {
            if (!Modifier.isPrivate(method.getModifiers())) {
                Class<?> superClass = objectType;
                while ((superClass = superClass.getSuperclass()) != null) {
                    try {
                        Method superMethod = superClass.getDeclaredMethod(
                                method.getName(), method.getParameterTypes());
                        int superModifiers = superMethod.getModifiers();
                        if (!Modifier.isPrivate(superModifiers) &&
                                !Modifier.isStatic(superModifiers)) {
                            ignoreBuilder.add(new IgnoreEntry(superMethod, spec));
                        }
                    } catch (NoSuchMethodException e) {
                        // Ignore
                    }
                }
            }
        }

        private final class ReflectSupplier implements Supplier<Object> {

            private final Constructor<?> constr;

            public ReflectSupplier(Class<?> type) {
                try {
                    this.constr = type.getDeclaredConstructor();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                this.constr.setAccessible(true);
            }

            @Override
            public Object get() {
                try {
                    return this.constr.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public <T> List<T> injectMethods(Object targetObject, MethodSpec<T> spec, Object[] parameters) {
            ImmutableList.Builder<T> results = ImmutableList.builder();
            this.injectMethods(targetObject, spec, parameters, results, null);
            return results.build();
        }

        @SuppressWarnings("unchecked")
        public <T> void injectMethods(Object targetObject, MethodSpec<T> spec, Object[] parameters,
                ImmutableList.Builder<T> results, Set<IgnoreEntry> ignore) {
            List<Method> methods = this.methodBindings.get(spec);
            if (methods != null) {
                for (Method method : methods) {
                    if (ignore != null && ignore.contains(new IgnoreEntry(method, spec))) {
                        continue;
                    }
                    try {
                        T result = (T) method.invoke(targetObject, parameters);
                        if (result != null) {
                            results.add(result);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (this.superType != null) {
                if (ignore == null) {
                    ignore = this.ignoreMethods;
                }
                getInjector(this.superType).injectMethods(targetObject, spec, parameters, results, ignore);
            }
        }
        
        public void injectObjects(Object targetObject, Map<String, Object> parameters,
                Predicate<ParameterInfo<?>> predicate, Set<IgnoreEntry> ignore) {
            for (ReflectParameterInfo<?,?> info : this.injectObjectParameterInfos) {
                if (predicate.test(info) && (ignore == null || info.accessor instanceof Field ||
                        !ignore.contains(new IgnoreEntry((Method) info.accessor, null)))) {
                    @SuppressWarnings({"rawtypes", "unchecked"})
                    Object result = module.getBinding(info.spec).getProvider().get(
                            targetObject, parameters, (ParameterInfo) info);
                    try {
                        if (info.accessor instanceof Field) {
                            ((Field) info.accessor).set(targetObject, result);
                        } else if (info.accessor instanceof Method) {
                            ((Method) info.accessor).invoke(targetObject, result);
                        }
                    } catch (InvocationTargetException e) {
                        throw new IllegalStateException(e);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (this.superType != null) {
                if (ignore == null) {
                    ignore = this.ignoreMethods;
                }
                getInjector(this.superType).injectObjects(targetObject, parameters, predicate, ignore);
            }
        }
    }
}
