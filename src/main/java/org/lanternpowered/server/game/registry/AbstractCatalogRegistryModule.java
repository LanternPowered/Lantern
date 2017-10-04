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
package org.lanternpowered.server.game.registry;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.registry.AlternateCatalogRegistryModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

public abstract class AbstractCatalogRegistryModule<T extends CatalogType>
        implements AlternateCatalogRegistryModule<T>, CatalogMappingDataHolder {

    @Nullable private Collection<T> values;
    Map<String, T> types = new HashMap<>();
    @Nullable Function<T, String> mappingProvider;
    private final Class<?>[] catalogClasses;
    @Nullable private final String patternValue;
    @Nullable private final Pattern pattern;
    final List<Consumer<T>> registrationConsumers = new ArrayList<>();

    public AbstractCatalogRegistryModule(Class<?>... catalogClasses) {
        this(null, catalogClasses, null);
    }

    public AbstractCatalogRegistryModule(Class<?>[] catalogClasses, @Nullable Function<T, String> mappingProvider) {
        this(catalogClasses, mappingProvider, null);
    }

    public AbstractCatalogRegistryModule(Class<?>[] catalogClasses, @Nullable Function<T, String> mappingProvider, @Nullable String pattern) {
        this(mappingProvider, checkNotNull(catalogClasses, "catalogClasses"), pattern);
    }

    public AbstractCatalogRegistryModule(Class<?>[] catalogClasses, @Nullable String pattern) {
        this(null, catalogClasses, pattern);
    }

    private AbstractCatalogRegistryModule(@Nullable Function<T, String> mappingProvider, Class<?>[] catalogClasses, @Nullable String pattern) {
        this.catalogClasses = checkNotNull(catalogClasses, "catalogClasses");
        for (Class<?> catalogClass : catalogClasses) {
            checkNotNull(catalogClass, "catalogClass is null in " + getClass().getName());
        }
        this.mappingProvider = mappingProvider;
        this.patternValue = pattern;
        this.pattern = pattern == null ? null : Pattern.compile(pattern);
    }

    void checkFinalizedContent() {
        checkState(this.values == null, "The content is already finalized.");
    }

    public void addRegistrationConsumer(Consumer<T> consumer) {
        this.registrationConsumers.add(consumer);
    }

    /**
     * Finalizes the content of the registry module, any further attempts for
     * changes will throw exceptions.
     */
    protected void finalizeContent() {
        checkFinalizedContent();
        this.types = ImmutableMap.copyOf(this.types);
        this.values = ImmutableSet.copyOf(this.types.values());
    }

    void validateCatalogType(T catalogType, String defaultPatternValue, Pattern defaultPattern) {
        checkState(this.values == null, "The content is finalized and doesn't allow any new entries.");
        checkNotNull(catalogType, "catalogType");
        final String id = catalogType.getId();
        checkArgument(id != null, "The catalog type id may not be null!");
        checkArgument(catalogType.getName() != null, "The catalog type name may not be null!");
        checkArgument((this.pattern == null ? defaultPattern : this.pattern).matcher(id).matches(),
                "The catalog type id %s must match the regex: %s", id, this.patternValue == null ? defaultPatternValue : this.patternValue);
        checkState(!this.types.containsKey(id), "There is already a catalog type registered with the id: %s", id);
    }

    /**
     * Registers a catalog type.
     *
     * @param catalogType The catalog type
     */
    protected abstract void register(T catalogType);

    @Override
    public Collection<T> getAll() {
        return this.values != null ? this.values : ImmutableSet.copyOf(this.types.values());
    }

    @Override
    public List<CatalogMappingData> getCatalogMappings() {
        if (this.catalogClasses.length == 0) {
            return ImmutableList.of();
        }
        final ImmutableList.Builder<CatalogMappingData> builder = ImmutableList.builder();
        final Map<String, T> mappings = provideCatalogMap();
        for (Class<?> catalogClass : this.catalogClasses) {
            builder.add(new CatalogMappingData(catalogClass, mappings));
        }
        return builder.build();
    }
}
