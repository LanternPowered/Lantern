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
package org.lanternpowered.server.inventory;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.data.property.Property;
import org.spongepowered.api.item.inventory.InventoryArchetype;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class BuilderInventoryArchetype<T extends AbstractInventory> extends LanternInventoryArchetype<T> {

    private final AbstractArchetypeBuilder<T, ? super T, ?> builder;
    private final List<InventoryArchetype> childArchetypes;

    BuilderInventoryArchetype(ResourceKey key, AbstractArchetypeBuilder<T, ? super T, ?> builder) {
        super(key);
        this.childArchetypes = Collections.unmodifiableList(builder.getArchetypes());
        this.builder = builder;
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
    public <V> Optional<V> getProperty(Property<V> property) {
        checkNotNull(property, "property");
        return Optional.ofNullable((V) this.builder.properties.get(property));
    }

    @Override
    public Map<Property<?>, ?> getProperties() {
        return this.builder.getProperties();
    }
}
