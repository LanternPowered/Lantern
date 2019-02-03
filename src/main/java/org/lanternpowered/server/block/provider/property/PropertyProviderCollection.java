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
package org.lanternpowered.server.block.provider.property;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.util.CopyableBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public final class PropertyProviderCollection {

    /**
     * Creates a new {@link PropertyProviderCollection.Builder}.
     *
     * @return The builder
     */
    public static Builder builder() {
        return new Builder();
    }

    private final Map<Class<?>, PropertyProvider<?>> propertyProviders;

    private PropertyProviderCollection(Map<Class<?>, PropertyProvider<?>> propertyProviders) {
        this.propertyProviders = propertyProviders;
    }

    /**
     * Gets the {@link PropertyProvider} for the specified property
     * type if present.
     *
     * @param propertyType The property type
     * @param <T> The property type
     * @return The property provider if found, otherwise {@link Optional#empty()}.
     */
    @SuppressWarnings("unchecked")
    public <T extends Property> Optional<PropertyProvider<T>> get(Class<T> propertyType) {
        return Optional.ofNullable((PropertyProvider) this.propertyProviders.get(
                checkNotNull(propertyType, "propertyType")));
    }

    /**
     * Gets a {@link Set} with all the {@link Property} types that are registered in
     * this collection.
     *
     * @return The property types
     */
    @SuppressWarnings("unchecked")
    public Set<Class<? extends Property<?,?>>> keys() {
        return (Set) this.propertyProviders.keySet();
    }

    /**
     * Creates a new {@link PropertyProviderCollection.Builder} with
     * the contents of this collection.
     *
     * @return The builder
     */
    public Builder toBuilder() {
        return new Builder().from(this);
    }

    @SuppressWarnings("unchecked")
    public static final class Builder implements CopyableBuilder<PropertyProviderCollection, Builder> {

        private final Map<Class<?>, PropertyProvider<?>> propertyProviders = new HashMap<>();

        private Builder() {
        }

        public PropertyProviderCollection build() {
            return new PropertyProviderCollection(ImmutableMap.copyOf(this.propertyProviders));
        }

        /**
         * Adds all the {@link PropertyProvider}s of the {@link PropertyProviderCollection}.
         *
         * @param providerCollection The property provider collection
         * @return This builder for chaining
         */
        public Builder add(PropertyProviderCollection providerCollection) {
            providerCollection.propertyProviders.forEach((key, value) -> add((Class) key, value));
            return this;
        }

        /**
         * Adds a {@link PropertyProvider}.
         *
         * @param propertyType The property type
         * @param provider The property provider
         * @param <T> The property type
         * @return This builder for chaining
         */
        public <T extends Property<?,?>> Builder add(Class<T> propertyType, PropertyProvider<? extends T> provider) {
            checkNotNull(propertyType, "propertyType");
            checkNotNull(provider, "provider");
            if (provider instanceof CachedPropertyObjectProvider) {
                provider = new SimplePropertyProvider<>(((CachedPropertyObjectProvider) provider).getFunction());
            }
            this.propertyProviders.put(propertyType, provider);
            return this;
        }

        /**
         * Adds a {@link PropertyProvider}.
         *
         * @param propertyType The property type
         * @param provider The property provider
         * @param <T> The property type
         * @return This builder for chaining
         */
        public <T extends Property<?,?>> Builder add(Class<T> propertyType, Function<BlockState, ? extends T> provider) {
            checkNotNull(propertyType, "propertyType");
            checkNotNull(provider, "provider");
            this.propertyProviders.put(propertyType, new SimplePropertyProvider<>(provider));
            return this;
        }

        @Override
        public Builder from(PropertyProviderCollection value) {
            this.propertyProviders.clear();
            add(value);
            return this;
        }

        @Override
        public Builder reset() {
            this.propertyProviders.clear();
            return this;
        }
    }
}
