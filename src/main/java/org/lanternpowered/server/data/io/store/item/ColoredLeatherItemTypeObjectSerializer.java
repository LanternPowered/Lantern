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

import static org.lanternpowered.server.data.DataHelper.getOrCreateView;
import static org.lanternpowered.server.data.io.store.item.ItemStackStore.DISPLAY;

import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Color;

public class ColoredLeatherItemTypeObjectSerializer extends ItemTypeObjectSerializer {

    private static final DataQuery COLOR = DataQuery.of("color");

    @Override
    public void serializeValues(ItemStack itemStack, SimpleValueContainer valueContainer, DataView dataView) {
        super.serializeValues(itemStack, valueContainer, dataView);
        valueContainer.remove(Keys.COLOR).ifPresent(color -> getOrCreateView(dataView, DISPLAY).set(COLOR, color.getRgb()));
    }

    @Override
    public void deserializeValues(ItemStack itemStack, SimpleValueContainer valueContainer, DataView dataView) {
        super.deserializeValues(itemStack, valueContainer, dataView);
        dataView.getView(DISPLAY).ifPresent(view -> view.getInt(COLOR).ifPresent(
                value -> valueContainer.set(Keys.COLOR, Color.ofRgb(value))));
    }
}
