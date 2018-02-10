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
import com.google.common.base.Objects;
import com.google.common.reflect.TypeToken;
import com.google.inject.BindingAnnotation;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import org.lanternpowered.server.shards.AnnotatedBindingBuilder;
import org.lanternpowered.server.shards.BindingBuilder;
import org.lanternpowered.server.shards.InjectableType;
import org.lanternpowered.server.shards.InjectionRegistry;
import org.lanternpowered.server.shards.Opt;
import org.lanternpowered.server.shards.SupplierFactory;
import org.lanternpowered.server.shards.internal.inject.LanternInjectableType;
import org.lanternpowered.server.util.SystemProperties;
import org.lanternpowered.server.util.TypeTokenHelper;
import org.lanternpowered.server.util.supplier.ObjectSupplierGenerator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class LanternInjectionRegistry implements InjectionRegistry {

    public static final boolean SHARDS_DEBUG_MODE = SystemProperties.get().getBooleanProperty("lantern.debug-shards");

    private static final LanternInjectionRegistry instance = new LanternInjectionRegistry(null);

    public static LanternInjectionRegistry get() {
        return instance;
    }

    private static final class FactoryEntry<T> {

        @Nullable private final TypeToken<T> typeToken;
        private final SupplierFactory<T> factory;

        private FactoryEntry(@Nullable TypeToken<T> typeToken, SupplierFactory<T> factory) {
            this.typeToken = typeToken;
            this.factory = factory;
        }
    }

    /**
     * The {@link TypeVariable} of the {@link Opt} class.
     */
    private final TypeVariable<?> optTypeVariable = Opt.class.getTypeParameters()[0];

    /**
     * All the registered {@link SupplierFactory}s.
     */
    private final List<FactoryEntry<?>> supplierFactories = new ArrayList<>();

    /**
     * A cache for all the {@link Supplier}s.
     */
    private final LoadingCache<InjectableType<?>, Optional<Supplier<?>>> supplierCache =
            Caffeine.newBuilder().build(type -> (Optional) loadSupplier(type));

    /**
     * The cached internal {@link Injector}.
     */
    @Nullable private Injector internalGuiceInjector;

    /**
     * All the installed guice modules.
     */
    private final List<Module> guiceModules = new ArrayList<>();

    @Nullable private final LanternInjectionRegistry parent;

    private LanternInjectionRegistry(@Nullable LanternInjectionRegistry parent) {
        this.parent = parent;
        // Register some inbuilt factories, only on the root registry
        if (parent == null) {
            bind(Opt.class).to((registry, injectableType) -> {
                // Extract the generic type from the Opt, and lookup a factory for that one
                final TypeToken<?> newTypeToken = injectableType.getType().resolveType(this.optTypeVariable);
                final InjectableType<?> newInjectableType = new LanternInjectableType<>(newTypeToken, injectableType.getAnnotations());
                final Optional<? extends Supplier<?>> optSupplier = registry.getSupplier(newInjectableType);
                if (!optSupplier.isPresent()) {
                    return Optional.of(Opt::empty);
                }
                final Supplier<?> supplier = optSupplier.get();
                return Optional.of(() -> Opt.of(supplier.get()));
            });
        }
    }

    /**
     * Finds a {@link Supplier} for the given {@link InjectableType}.
     *
     * @param type The injectable type
     * @param <T> The object type
     * @return The supplier, if found
     */
    private <T> Optional<Supplier<T>> loadSupplier(InjectableType<T> type) {
        for (FactoryEntry entry : this.supplierFactories) {
            // Check if the factory type is assignable to the requested type
            if (entry.typeToken != null && !TypeTokenHelper.isAssignable(entry.typeToken, type.getType())) {
                continue;
            }
            final Optional<Supplier<T>> optSupplier = ((SupplierFactory<T>) entry.factory).get(this, type);
            if (optSupplier.isPresent()) {
                return optSupplier;
            }
        }
        // Fallback to guice
        if (this.internalGuiceInjector == null) {
            if (this.guiceModules.isEmpty()) {
                return this.parent == null ? Optional.empty() : this.parent.getSupplier(type);
            }
            // Create a new injector
            this.internalGuiceInjector = Guice.createInjector(this.guiceModules);
        }
        Annotation bindAnnotation = null;
        // Try to find a @BindingAnnotation
        for (Annotation annotation : type.getAnnotations()) {
            if (annotation.annotationType().getAnnotation(BindingAnnotation.class) != null) {
                if (bindAnnotation != null) {
                    throw new IllegalStateException("Found multiple BindingAnnotations on: " + type);
                }
                bindAnnotation = annotation;
            }
        }
        final Type genericType = type.getType().getType();
        final Key<T> key = (Key<T>) (bindAnnotation == null ? Key.get(genericType) : Key.get(genericType, bindAnnotation));
        try {
            final Provider<T> provider = this.internalGuiceInjector.getProvider(key);
            return Optional.of(provider::get);
        } catch (ConfigurationException e) {
            return this.parent == null ? Optional.empty() : this.parent.getSupplier(type);
        }
    }

    @Override
    public <T> AnnotatedBindingBuilder<T> bind(TypeToken<T> objectType) {
        checkNotNull(objectType, "objectType");
        return new LanternBindingBuilder<>(objectType);
    }

    @Override
    public void bindFactory(SupplierFactory<?> supplierFactory) {
        checkNotNull(supplierFactory, "supplierFactory");
        this.supplierFactories.add(new FactoryEntry<>(null, supplierFactory));
    }

    @Override
    public <T> Optional<Supplier<T>> getSupplier(TypeToken<T> objectType) {
        checkNotNull(objectType, "objectType");
        return getSupplier(new LanternInjectableType<>(objectType));
    }

    @Override
    public <T> Optional<Supplier<T>> getSupplier(InjectableType<T> injectableType) {
        return (Optional) this.supplierCache.get(injectableType);
    }

    @Override
    public void install(Module module) {
        checkNotNull(module, "module");
        // Add the guice module
        this.guiceModules.add(module);
        // Force the guice injector to rebuild
        this.internalGuiceInjector = null;
    }

    @Override
    public InjectionRegistry newChild() {
        return new LanternInjectionRegistry(this);
    }

    private final class LanternBindingBuilder<T> implements AnnotatedBindingBuilder<T> {

        private final TypeToken<T> typeToken;

        @Nullable private Annotation annotation;
        @Nullable private Class<? extends Annotation> annotationType;

        private LanternBindingBuilder(TypeToken<T> typeToken) {
            this.typeToken = typeToken;
        }

        @Override
        public BindingBuilder<T> annotatedWith(Class<? extends Annotation> annotationType) {
            checkNotNull(annotationType, "annotationType");
            this.annotationType = annotationType;
            return this;
        }

        @Override
        public BindingBuilder<T> annotatedWith(Annotation annotation) {
            checkNotNull(annotation, "annotation");
            this.annotation = annotation;
            return this;
        }

        @Override
        public void to(Class<T> implementation) {
            checkNotNull(implementation, "implementation");
            // Generate a supplier and bind it
            to(ObjectSupplierGenerator.getSupplier(implementation));
        }

        @Override
        public void to(Supplier<T> supplier) {
            checkNotNull(supplier, "supplier");
            to((registry, injectableType) -> Optional.of(supplier));
        }

        @Override
        public void to(SupplierFactory<T> supplierFactory) {
            checkNotNull(supplierFactory, "supplierFactory");
            // Apply the annotation requirements, if present
            if (this.annotation != null) {
                supplierFactory = new AnnotationSupplierFactory<>(supplierFactory, this.annotation);
            } else if (this.annotationType != null) {
                supplierFactory = new AnnotationTypeSupplierFactory<>(supplierFactory, this.annotationType);
            }
            // Register the supplier factory
            supplierFactories.add(new FactoryEntry<>(this.typeToken, supplierFactory));
        }
    }

    private static final class AnnotationSupplierFactory<T> implements SupplierFactory<T> {

        private final SupplierFactory<T> supplierFactory;
        private final Annotation annotation;

        private AnnotationSupplierFactory(SupplierFactory<T> supplierFactory, Annotation annotation) {
            this.supplierFactory = supplierFactory;
            this.annotation = annotation;
        }

        @Override
        public Optional<Supplier<T>> get(InjectionRegistry registry, InjectableType<T> injectableType) {
            final Annotation annotation = injectableType.getAnnotation(this.annotation.getClass());
            if (!Objects.equal(this.annotation, annotation)) {
                return Optional.empty();
            }
            return this.supplierFactory.get(registry, injectableType);
        }
    }

    private static final class AnnotationTypeSupplierFactory<T> implements SupplierFactory<T> {

        private final SupplierFactory<T> supplierFactory;
        private final Class<? extends Annotation> annotationType;

        private AnnotationTypeSupplierFactory(SupplierFactory<T> supplierFactory,
                Class<? extends Annotation> annotationType) {
            this.supplierFactory = supplierFactory;
            this.annotationType = annotationType;
        }

        @Override
        public Optional<Supplier<T>> get(InjectionRegistry registry, InjectableType<T> injectableType) {
            final Annotation annotation = injectableType.getAnnotation(this.annotationType);
            if (annotation == null) {
                return Optional.empty();
            }
            return this.supplierFactory.get(registry, injectableType);
        }
    }
}
