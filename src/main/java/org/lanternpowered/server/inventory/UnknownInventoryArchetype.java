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

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UnknownInventoryArchetype extends LanternInventoryArchetype<AbstractInventory> {

    public UnknownInventoryArchetype(ResourceKey key) {
        super(key);
    }

    @Override
    public AbstractArchetypeBuilder<AbstractInventory, ? super AbstractInventory, ?> getBuilder() {
        throw new IllegalStateException("The " + getName() + " inventory archetype cannot be constructed.");
    }

    @Override
    public List<InventoryArchetype> getChildArchetypes() {
        return Collections.emptyList();
    }

    @Override
    public Map<String, InventoryProperty<String, ?>> getProperties() {
        return Collections.emptyMap();
    }

    @Override
    public Optional<InventoryProperty<String, ?>> getProperty(String key) {
        return Optional.empty();
    }

    @Override
    public <P extends InventoryProperty<String, ?>> Optional<P> getProperty(Class<P> property) {
        return Optional.empty();
    }

    @Override
    public <T extends InventoryProperty<String, ?>> Optional<T> getProperty(Class<T> type, String key) {
        return Optional.empty();
    }
}
