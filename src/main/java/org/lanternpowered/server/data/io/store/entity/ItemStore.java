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
package org.lanternpowered.server.data.io.store.entity;

import org.lanternpowered.server.data.io.store.ObjectSerializerRegistry;
import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.lanternpowered.server.entity.LanternItem;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStack;

public class ItemStore extends EntityStore<LanternItem> {

    private static final DataQuery ITEM = DataQuery.of("Item");
    private static final DataQuery PICKUP_DELAY = DataQuery.of("PickupDelay");

    @Override
    public void serializeValues(LanternItem item, SimpleValueContainer valueContainer, DataView dataView) {
        dataView.set(ITEM, ObjectSerializerRegistry.get().get(ItemStack.class).get()
                .serialize(valueContainer.remove(Keys.REPRESENTED_ITEM).get().createStack()));
        valueContainer.remove(Keys.PICKUP_DELAY).ifPresent(v -> dataView.set(PICKUP_DELAY, v.shortValue()));
        super.serializeValues(item, valueContainer, dataView);
    }

    @Override
    public void deserializeValues(LanternItem item, SimpleValueContainer valueContainer, DataView dataView) {
        valueContainer.set(Keys.REPRESENTED_ITEM, ObjectSerializerRegistry.get().get(ItemStack.class).get()
                .deserialize(dataView.getView(ITEM).get()).createSnapshot());
        valueContainer.set(Keys.PICKUP_DELAY, dataView.getInt(PICKUP_DELAY).orElse(0));
        super.deserializeValues(item, valueContainer, dataView);
    }
}
