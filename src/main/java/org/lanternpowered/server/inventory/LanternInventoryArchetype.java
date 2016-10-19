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
package org.lanternpowered.server.inventory;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.lanternpowered.server.catalog.PluginCatalogType;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

public class LanternInventoryArchetype extends PluginCatalogType.Base implements InventoryArchetype {

    private final Map<String, InventoryProperty<?,?>> inventoryPropertiesByName;
    private final Map<InventoryPropertyKey, InventoryProperty<?,?>> inventoryPropertiesByKey;

    private final List<InventoryArchetype> childArchetypes;

    public LanternInventoryArchetype(String pluginId, String name, List<InventoryArchetype> childArchetypes,
            Map<String, InventoryProperty<?, ?>> inventoryPropertiesByName,
            Map<InventoryPropertyKey, InventoryProperty<?, ?>> inventoryPropertiesByKey) {
        super(pluginId, name);
        this.childArchetypes = ImmutableList.copyOf(childArchetypes);
        this.inventoryPropertiesByName = ImmutableMap.copyOf(inventoryPropertiesByName);
        this.inventoryPropertiesByKey = ImmutableMap.copyOf(inventoryPropertiesByKey);
    }

    public LanternInventoryArchetype(String pluginId, String id, String name, List<InventoryArchetype> childArchetypes,
            Map<String, InventoryProperty<?, ?>> inventoryPropertiesByName,
            Map<InventoryPropertyKey, InventoryProperty<?, ?>> inventoryPropertiesByKey) {
        super(pluginId, id, name);
        this.childArchetypes = ImmutableList.copyOf(childArchetypes);
        this.inventoryPropertiesByName = ImmutableMap.copyOf(inventoryPropertiesByName);
        this.inventoryPropertiesByKey = ImmutableMap.copyOf(inventoryPropertiesByKey);
    }

    public Map<Class<?>, InventoryProperty<?,?>> getPropertiesByClass() {
        final ImmutableMap.Builder<Class<?>, InventoryProperty<?,?>> builder = ImmutableMap.builder();
        this.inventoryPropertiesByKey.forEach((key, value) -> builder.put(value.getClass(), value));
        return builder.build();
    }

    @Override
    public List<InventoryArchetype> getChildArchetypes() {
        return this.childArchetypes;
    }

    @Override
    public Map<String, InventoryProperty<String, ?>> getProperties() {
        //noinspection unchecked
        return (Map) this.inventoryPropertiesByName;
    }

    @Override
    public Optional<InventoryProperty<String, ?>> getProperty(String key) {
        checkNotNull(key, "key");
        //noinspection unchecked
        return Optional.ofNullable((InventoryProperty<String, ?>) this.inventoryPropertiesByName.get(key));
    }

    @Override
    public <T extends InventoryProperty<String, ?>> Optional<T> getProperty(Class<T> property, @Nullable String key) {
        checkNotNull(property, "property");
        final InventoryPropertyKey propertyKey = new InventoryPropertyKey(property, key);
        final InventoryProperty<?, ?> property1 = this.inventoryPropertiesByKey.get(propertyKey);
        if (property1 != null && property.isInstance(property1)) {
            return Optional.of(property.cast(property1));
        }
        return Optional.empty();
    }
}
