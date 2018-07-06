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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.registry.CatalogRegistryModule;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

public class EnumValueRegistryModule<V extends CatalogType>
        implements CatalogRegistryModule<V>, CatalogMappingDataHolder {

    private final Map<CatalogKey, V> values = new HashMap<>();
    @Nullable private Set<V> unmodifiableValues;
    final Class<? extends Enum<?>> enumType;
    @Nullable private final Class<?> catalogClass;

    protected EnumValueRegistryModule(Class<? extends Enum<?>> enumType, @Nullable Class<?> catalogClass) {
        this.enumType = checkNotNull(enumType, "enumType");
        this.catalogClass = catalogClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void registerDefaults() {
        for (Enum<?> type : this.enumType.getEnumConstants()) {
            final V catalogType = (V) type;
            this.values.put(catalogType.getKey(), catalogType);
        }
        this.unmodifiableValues = ImmutableSet.copyOf(this.values.values());
    }

    @Override
    public Optional<V> get(CatalogKey key) {
        return Optional.ofNullable(this.values.get(key));
    }

    @Override
    public Collection<V> getAll() {
        return this.unmodifiableValues == null ? Collections.emptyList() : this.unmodifiableValues;
    }

    @Override
    public List<CatalogMappingData> getCatalogMappings() {
        if (this.catalogClass == null) {
            return ImmutableList.of();
        }
        final Map<String, V> mappings = new HashMap<>();
        this.values.forEach((key, value) -> mappings.put(key.getValue(), value));
        return ImmutableList.of(new CatalogMappingData(this.catalogClass, mappings));
    }
}
