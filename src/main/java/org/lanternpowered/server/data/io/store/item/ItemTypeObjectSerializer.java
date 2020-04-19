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
package org.lanternpowered.server.data.io.store.item;

import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.item.inventory.ItemStack;

public class ItemTypeObjectSerializer {

    static final DataQuery BLOCK_ENTITY_TAG = DataQuery.of("BlockEntityTag");

    public void serializeValues(ItemStack itemStack, SimpleValueContainer valueContainer, DataView dataView) {
    }

    public void deserializeValues(ItemStack itemStack, SimpleValueContainer valueContainer, DataView dataView) {
    }
}
