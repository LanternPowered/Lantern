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
package org.lanternpowered.server.shards.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.base.MoreObjects;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import org.lanternpowered.server.shards.Holder;
import org.lanternpowered.server.shards.InjectableType;
import org.lanternpowered.server.shards.Shard;
import org.lanternpowered.server.shards.dependency.AutoAttach;
import org.lanternpowered.server.shards.dependency.Dependencies;
import org.lanternpowered.server.shards.dependency.Dependency;
import org.lanternpowered.server.shards.dependency.Requirement;
import org.lanternpowered.server.shards.internal.inject.InjectionEntry;
import org.lanternpowered.server.shards.internal.inject.LanternInjectableType;
import org.lanternpowered.server.util.FieldAccessFactory;
import org.lanternpowered.server.util.LambdaFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public final class ComponentType<T extends Shard> {

    /**
     * Gets the {@link ComponentType} for the
     * given {@link Shard} class.
     *
     * @param componentType The component class
     * @param <T> The component type
     * @return The component type
     */
    public static <T extends Shard> ComponentType<T> get(Class<T> componentType) {
        checkNotNull(componentType, "componentType");
        return cache.get(componentType);
    }

    private static final LoadingCache<Class<? extends Shard>, ComponentType> cache =
            Caffeine.newBuilder().build(ComponentType::load);

    /**
     * Creates the {@link ComponentType} for the given {@link Shard} {@link TypeToken}.
     *
     * @param key The component type token
     * @param <T> The component type
     * @return The component type
     */
    private static <T extends Shard> ComponentType<T> load(Class<T> key) {
        checkNotNull(key, "key");
        // Collect all the dependencies of the ComponentType
        final CollectionContext ctx = new CollectionContext();
        collect(key, ctx);
        return new ComponentType<>(key, ctx.dependencies, ctx.injectionEntries,
                ctx.holderInjectionEntries.stream()
                        .map(e -> e.getValueType().getType().getRawType())
                        .collect(Collectors.toList()));
    }

    private static final class CollectionContext {

        private final Set<Class<?>> processed = new HashSet<>();
        private final Set<MethodKey> methodKeys = new HashSet<>();

        private final List<DependencySpec> dependencies = new ArrayList<>();

        private final List<InjectionEntry> injectionEntries = new ArrayList<>();
        private final List<InjectionEntry> holderInjectionEntries = new ArrayList<>();
    }

    /**
     * Collects the {@link DependencySpec}s and
     * injection fields from the given class type.
     *
     * @param key The key
     */
    private static void collect(Class<?> key, CollectionContext ctx) {
        if (!ctx.processed.add(key)) { // Was already processed, skip it
            return;
        }
        final Dependencies dependenciesAnno = key.getAnnotation(Dependencies.class);
        if (dependenciesAnno != null) {
            for (Dependency dependency : dependenciesAnno.value()) {
                DependencySpec.mergeAndAdd(ctx.dependencies, dependency.value(), dependency.type(), dependency.autoAttach());
            }
        }
        for (Field field : key.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getAnnotation(Inject.class) == null) {
                continue;
            }
            final TypeToken<?> typeToken = TypeToken.of(field.getGenericType());
            final Annotation[] annotations = field.getAnnotations();

            final LanternInjectableType<?> injectableType = new LanternInjectableType<>(typeToken, annotations);
            addInjectionEntry(injectableType, FieldAccessFactory.createSetter(field), ctx);

            // Component dependency handling
            addDependency(ctx.dependencies, injectableType);
        }
        for (Method method : key.getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.getAnnotation(Inject.class) == null) {
                continue;
            }
            // Skip private methods, public methods were generated in the ShardsClassTransformer
            if (Modifier.isPrivate(method.getModifiers())) {
                continue;
            }
            final Type[] paramTypes = method.getGenericParameterTypes();
            if (paramTypes.length != 1) {
                throw new IllegalStateException("Expected one argument for the method: " + method.getName() + " in " + key.getName());
            }
            if (!ctx.methodKeys.add(new MethodKey(method))) {
                continue;
            }
            final TypeToken<?> typeToken = TypeToken.of(paramTypes[0]);
            final Annotation[] annotations = method.getParameterAnnotations()[0];

            final LanternInjectableType<?> injectableType = new LanternInjectableType<>(typeToken, annotations);
            addInjectionEntry(injectableType, LambdaFactory.createBiConsumer(method), ctx);

            // Component dependency handling
            addDependency(ctx.dependencies, injectableType);
        }
        for (Class<?> interf : key.getInterfaces()) {
            collect(interf, ctx);
        }
        final Class<?> superClass = key.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            collect(superClass, ctx);
        }
    }

    private static void addInjectionEntry(LanternInjectableType<?> injectableType,
            BiConsumer<Object, Object> setter, CollectionContext ctx) {
        final InjectionEntry injectionEntry = new InjectionEntry(injectableType, setter);
        ctx.injectionEntries.add(injectionEntry);
        // All the holder types must be checked if they are
        // compatible, and throw a exception if otherwise
        if (injectableType.getAnnotation(Holder.class) == null) {
            return;
        }
        final Class<?> type = injectableType.getType().getRawType();
        for (InjectionEntry otherEntry : ctx.holderInjectionEntries) {
            final Class<?> otherType = otherEntry.getValueType().getType().getRawType();
            // Check if it's a sub class or super class, they are both allowed
            if (type.isAssignableFrom(otherType) &&
                    otherType.isAssignableFrom(type)) {
                continue;
            }
            // Check if it's not a final class and a interface could be implemented
            if ((!Modifier.isFinal(type.getModifiers()) && otherType.isInterface()) ||
                    (!Modifier.isFinal(otherType.getModifiers()) && type.isInterface())) {
                continue;
            }
            throw new IllegalStateException("Found incompatible holder types within the dependencies: \"" +
                    type.getName() + "\" and \"" + otherType.getName() + "\"");
        }
        ctx.holderInjectionEntries.add(injectionEntry);
    }

    private static void addDependency(List<DependencySpec> dependencies, InjectableType<?> injectableType) {
        final TypeToken<?> typeToken = injectableType.getType();
        final Class<?> raw = typeToken.getRawType();
        // Component dependency handling
        Requirement dependencyType = null;
        Class<?> componentClass = null;
        if (Shard.class.isAssignableFrom(raw)) {
            dependencyType = Requirement.REQUIRED;
            componentClass = raw;
        } else {
            /*
            if (Dyn.class.isAssignableFrom(raw)) {
                dependencyType = Requirement.REQUIRED_DYNAMIC;
                componentClass = typeToken.resolveType(Dyn.class.getTypeParameters()[0]).getRawType();
            } else if (Opt.class.isAssignableFrom(raw)) {
                dependencyType = Requirement.OPTIONAL;
                componentClass = typeToken.resolveType(Opt.class.getTypeParameters()[0]).getRawType();
            }
            if (componentClass == null || !Shard.class.isAssignableFrom(componentClass)) {
                return;
            }*/
        }
        final boolean autoAttach = injectableType.getAnnotation(AutoAttach.class) != null;
        DependencySpec.mergeAndAdd(dependencies, (Class<? extends Shard>) componentClass, dependencyType, autoAttach);
    }

    /**
     * A key which ignores the declaring class to check equality.
     */
    private static final class MethodKey {

        private final Method method;

        private MethodKey(Method method) {
            this.method = method;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != getClass()) {
                return false;
            }
            final MethodKey key = (MethodKey) obj;
            return key.method.getName().equals(this.method.getName()) &&
                    Arrays.equals(key.method.getParameterTypes(), this.method.getParameterTypes());
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.method.getName(), this.method.getParameterTypes());
        }
    }

    private final Class<T> componentClass;
    private final List<DependencySpec> dependencies;
    private final List<InjectionEntry> injectionEntries;
    private final List<Class<?>> holderRequirements;

    private ComponentType(Class<T> componentClass, List<DependencySpec> dependencies,
            List<InjectionEntry> injectionEntries, List<Class<?>> holderRequirements) {
        this.componentClass = componentClass;
        this.dependencies = dependencies;
        this.injectionEntries = injectionEntries;
        this.holderRequirements = holderRequirements;
    }

    public Class<T> getComponentClass() {
        return this.componentClass;
    }

    public List<DependencySpec> getDependencies() {
        return this.dependencies;
    }

    public List<Class<?>> getHolderRequirements() {
        return this.holderRequirements;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass().getSimpleName())
                .add("component", this.componentClass.getName())
                .add("dependencies", this.dependencies)
                .add("injectionEntries", this.injectionEntries)
                .toString();
    }
}
