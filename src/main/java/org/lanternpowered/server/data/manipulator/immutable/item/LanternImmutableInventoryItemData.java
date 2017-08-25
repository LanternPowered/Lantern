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
package org.lanternpowered.server.data.manipulator.immutable.item;

import org.lanternpowered.server.data.element.Element;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.data.manipulator.immutable.AbstractImmutableData;
import org.lanternpowered.server.inventory.LanternEmptyCarriedInventory;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableInventoryItemData;
import org.spongepowered.api.data.manipulator.mutable.item.InventoryItemData;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

public class LanternImmutableInventoryItemData extends AbstractImmutableData<ImmutableInventoryItemData, InventoryItemData>
        implements ImmutableInventoryItemData {

    public LanternImmutableInventoryItemData() {
        super(ImmutableInventoryItemData.class, InventoryItemData.class);
    }

    public LanternImmutableInventoryItemData(InventoryItemData manipulator) {
        super(manipulator);
        final Element<CarriedInventory<?>> element = getValueCollection().getElement(LanternKeys.ITEM_INVENTORY).get();
        if (element.get() instanceof LanternEmptyCarriedInventory) {
            element.set(new LanternEmptyCarriedInventory(this));
        }
    }

    @Override
    public void registerKeys() {
        getValueCollection().register(LanternKeys.ITEM_INVENTORY, new LanternEmptyCarriedInventory(this));
    }

    @Override
    public CarriedInventory<? extends Carrier> getInventory() {
        return get(LanternKeys.ITEM_INVENTORY).get();
    }
}
