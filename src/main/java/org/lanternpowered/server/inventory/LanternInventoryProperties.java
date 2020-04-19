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

import org.lanternpowered.server.item.predicate.ItemPredicate;
import org.spongepowered.api.data.property.Property;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

@SuppressWarnings("unchecked")
public final class LanternInventoryProperties {

    /**
     * Represents the {@link ItemPredicate item filter} of a slot.
     */
    public static final Property<ItemPredicate> ITEM_FILTER =
            DummyObjectProvider.createFor(Property.class, "ITEM_FILTER");

}
