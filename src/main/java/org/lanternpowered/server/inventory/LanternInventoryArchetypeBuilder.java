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
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LanternInventoryArchetypeBuilder implements InventoryArchetype.Builder {

    private final List<InventoryArchetype> types = new ArrayList<>();
    private final Map<String, InventoryProperty<String, ?>> inventoryPropertiesByName = new HashMap<>();
    private final Map<Class<?>, InventoryProperty<String, ?>> inventoryProperties = new HashMap<>();

    @Override
    public LanternInventoryArchetypeBuilder from(InventoryArchetype value) {
        checkNotNull(value, "value");
        this.types.addAll(value.getChildArchetypes());
        //noinspection unchecked
        this.inventoryPropertiesByName.putAll(value.getProperties());
        //noinspection unchecked
        this.inventoryProperties.putAll((Map) ((LanternInventoryArchetype) value).getPropertiesByClass());
        return this;
    }

    @Override
    public LanternInventoryArchetypeBuilder reset() {
        this.types.clear();
        this.inventoryPropertiesByName.clear();
        this.inventoryProperties.clear();
        return this;
    }

    public LanternInventoryArchetypeBuilder property(String key, InventoryProperty<String, ?> property) {
        checkNotNull(property, "property");
        //noinspection ConstantConditions
        if (key != null) {
            this.inventoryPropertiesByName.put(key, property);
        }
        this.inventoryProperties.put(property.getClass(), property);
        return this;
    }

    @Override
    public LanternInventoryArchetypeBuilder property(InventoryProperty<String, ?> property) {
        checkNotNull(property, "property");
        return property(property.getKey(), property);
    }

    @Override
    public LanternInventoryArchetypeBuilder with(InventoryArchetype archetype) {
        checkNotNull(archetype, "archetype");
        this.types.add(archetype);
        return this;
    }

    @Override
    public LanternInventoryArchetypeBuilder with(InventoryArchetype... archetypes) {
        checkNotNull(archetypes, "archetypes");
        Collections.addAll(this.types, archetypes);
        return this;
    }

    @Override
    public LanternInventoryArchetype build(String id, String name) {
        checkNotNullOrEmpty(id, "id");
        checkNotNullOrEmpty(name, "name");
        final int index = id.indexOf(':');
        final String pluginId;
        if (index != -1) {
            pluginId = id.substring(0, index);
            id = id.substring(index + 1);
        } else {
            pluginId = "minecraft";
        }
        final Map<InventoryPropertyKey, InventoryProperty<?,?>> inventoryProperties = new HashMap<>();
        this.inventoryProperties.values().forEach(property ->
                inventoryProperties.put(new InventoryPropertyKey(property.getClass(), property.getKey()), property));
        //noinspection unchecked
        return new LanternInventoryArchetype(pluginId, id, name, this.types, (Map) this.inventoryPropertiesByName, inventoryProperties);
    }
}
