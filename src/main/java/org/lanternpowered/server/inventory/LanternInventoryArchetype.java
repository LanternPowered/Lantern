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

import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.property.PropertyHolder;
import org.spongepowered.api.item.inventory.InventoryArchetype;

@SuppressWarnings("unchecked")
public abstract class LanternInventoryArchetype<T extends AbstractInventory> extends DefaultCatalogType
        implements InventoryArchetype, PropertyHolder {

    LanternInventoryArchetype(CatalogKey key) {
        super(key);
    }

    public abstract AbstractArchetypeBuilder<T, ? super T, ?> getBuilder();

    /**
     * Constructs a {@link AbstractInventory}.
     *
     * @return The inventory
     */
    public T build() {
        return getBuilder().build0(false, null, this);
    }

    /**
     * Constructs a {@link LanternInventoryBuilder}
     * with this archetype.
     *
     * @return The inventory builder
     */
    public LanternInventoryBuilder<T> builder() {
        return LanternInventoryBuilder.create().of(this);
    }
}
