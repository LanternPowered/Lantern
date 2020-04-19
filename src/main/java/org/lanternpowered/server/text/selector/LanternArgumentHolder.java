/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.text.selector;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import org.lanternpowered.lmbda.LambdaFactory;
import org.spongepowered.api.text.selector.ArgumentHolder;
import org.spongepowered.api.text.selector.ArgumentType;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3f;
import org.spongepowered.math.vector.Vector3i;
import org.spongepowered.math.vector.Vector3l;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class LanternArgumentHolder<T extends ArgumentHolder<?>> implements ArgumentHolder<T> {

    private final Set<T> subtypes;

    @SuppressWarnings("unchecked")
    protected LanternArgumentHolder() {
        if (this instanceof ArgumentType) {
            this.subtypes = ImmutableSet.of((T) this);
        } else {
            throw new IllegalStateException("Must provide subtypes if not ArgumentType");
        }
    }

    public LanternArgumentHolder(Set<T> subtypes) {
        this.subtypes = ImmutableSet.copyOf(subtypes);
    }

    @Override
    public int getCount() {
        return this.subtypes.size();
    }

    @Override
    public Set<T> getTypes() {
        return this.subtypes;
    }

    public static class LanternVector3<V, T> extends LanternArgumentHolder<ArgumentType<T>> implements ArgumentHolder.Vector3<V, T> {

        private static final SetMultimap<Class<?>, Function<?, ?>> extractFunctionSets = HashMultimap.create();

        static {
            final List<Class<?>> vectors = Arrays.asList(Vector3i.class, Vector3l.class, Vector3f.class, Vector3d.class);
            for (Class<?> vec : vectors) {
                final Set<Function<?, ?>> set = Sets.newLinkedHashSet();
                try {
                    final MethodHandles.Lookup lookup = MethodHandles.publicLookup();
                    set.add(LambdaFactory.createFunction(lookup.unreflect(vec.getMethod("getX"))));
                    set.add(LambdaFactory.createFunction(lookup.unreflect(vec.getMethod("getY"))));
                    set.add(LambdaFactory.createFunction(lookup.unreflect(vec.getMethod("getZ"))));
                } catch (Exception e) {
                    // should support getX/Y/Z
                    throw new AssertionError("Bad vector3 type: " + vec.getName());
                }
                extractFunctionSets.putAll(vec, set);
            }
        }

        private final ArgumentType<T> x;
        private final ArgumentType<T> y;
        private final ArgumentType<T> z;
        private final Set<Function<V, T>> extractFunctionSet;

        @SuppressWarnings("unchecked")
        public LanternVector3(ArgumentType<T> x, ArgumentType<T> y, ArgumentType<T> z, Class<V> vectorType) {
            super(ImmutableSet.of(x, y, z));
            this.x = x;
            this.y = y;
            this.z = z;
            this.extractFunctionSet = (Set<Function<V, T>>) (Object) extractFunctionSets.get(vectorType);
            if (this.extractFunctionSet.isEmpty()) {
                throw new IllegalStateException("Unknown vector type " + vectorType);
            }
        }

        @Override
        public ArgumentType<T> x() {
            return this.x;
        }

        @Override
        public ArgumentType<T> y() {
            return this.y;
        }

        @Override
        public ArgumentType<T> z() {
            return this.z;
        }

        protected Set<Function<V, T>> extractFunctions() {
            return this.extractFunctionSet;
        }
    }

    public static class LanternLimit<T extends ArgumentHolder<?>> extends LanternArgumentHolder<T> implements ArgumentHolder.Limit<T> {

        private final T min;
        private final T max;

        public LanternLimit(T min, T max) {
            super(ImmutableSet.of(min, max));
            this.min = min;
            this.max = max;
        }

        @Override
        public T minimum() {
            return this.min;
        }

        @Override
        public T maximum() {
            return this.max;
        }
    }
}
