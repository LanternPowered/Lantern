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
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.item.inventory.ItemStack;

public class WritableBookItemTypeObjectSerializer extends ItemTypeObjectSerializer {

    public static final DataQuery PAGES = DataQuery.of("pages");

    @Override
    public void serializeValues(ItemStack itemStack, SimpleValueContainer valueContainer, DataView dataView) {
        super.serializeValues(itemStack, valueContainer, dataView);
        valueContainer.remove(Keys.PLAIN_BOOK_PAGES).ifPresent(lines -> dataView.set(PAGES, lines));
    }

    @Override
    public void deserializeValues(ItemStack itemStack, SimpleValueContainer valueContainer, DataView dataView) {
        super.deserializeValues(itemStack, valueContainer, dataView);
        dataView.getStringList(PAGES).ifPresent(lines -> valueContainer.set(Keys.PLAIN_BOOK_PAGES, lines));
    }
}
