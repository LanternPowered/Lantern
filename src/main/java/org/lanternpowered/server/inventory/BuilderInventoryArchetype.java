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

import com.google.common.collect.ImmutableMap;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class BuilderInventoryArchetype<T extends AbstractInventory> extends LanternInventoryArchetype<T> {

    private final AbstractArchetypeBuilder<T, ? super T, ?> builder;
    private final List<InventoryArchetype> childArchetypes;
    private final Map<String, InventoryProperty<String, ?>> propertiesByName;

    BuilderInventoryArchetype(String pluginId, String name, AbstractArchetypeBuilder<T, ? super T, ?> builder) {
        super(pluginId, name);
        this.childArchetypes = Collections.unmodifiableList(builder.getArchetypes());
        this.builder = builder;
        final ImmutableMap.Builder<String, InventoryProperty<String, ?>> mapBuilder = ImmutableMap.builder();
        for (Map.Entry<Class<?>, Map<String, InventoryProperty<String, ?>>> entry : builder.properties.entrySet()) {
            mapBuilder.putAll(entry.getValue());
        }
        this.propertiesByName = mapBuilder.build();
    }

    @Override
    public AbstractArchetypeBuilder<T, ? super T, ?> getBuilder() {
        return this.builder;
    }

    @Override
    public List<InventoryArchetype> getChildArchetypes() {
        return this.childArchetypes;
    }

    @Override
    public Map<String, InventoryProperty<String, ?>> getProperties() {
        return this.propertiesByName;
    }

    @Override
    public Optional<InventoryProperty<String, ?>> getProperty(String key) {
        checkNotNull(key, "key");
        return Optional.ofNullable(this.propertiesByName.get(key));
    }

    @Override
    public <P extends InventoryProperty<String, ?>> Optional<P> getProperty(Class<P> property) {
        checkNotNull(property, "property");
        final Map<String, InventoryProperty<String, ?>> map = this.builder.properties.get(property);
        return map == null ? Optional.empty(): Optional.ofNullable((P) map.values().stream().findFirst().orElse(null));
    }

    @Override
    public <P extends InventoryProperty<String, ?>> Optional<P> getProperty(Class<P> property, String key) {
        checkNotNull(property, "property");
        final Map<String, InventoryProperty<String, ?>> map = this.builder.properties.get(property);
        return map == null ? Optional.empty() : Optional.ofNullable((P) map.get(key));
    }
}
