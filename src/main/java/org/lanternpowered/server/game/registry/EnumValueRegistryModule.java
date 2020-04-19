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

import org.checkerframework.checker.nullness.qual.Nullable;

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
