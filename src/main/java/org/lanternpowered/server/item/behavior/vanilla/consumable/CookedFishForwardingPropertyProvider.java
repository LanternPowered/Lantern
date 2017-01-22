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
package org.lanternpowered.server.item.behavior.vanilla.consumable;

import org.lanternpowered.server.data.type.LanternCookedFish;
import org.lanternpowered.server.item.PropertyProvider;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.CookedFishes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import javax.annotation.Nullable;

public class CookedFishForwardingPropertyProvider<P extends Property<?,?>> implements PropertyProvider<P> {

    private final Class<P> propertyType;

    public CookedFishForwardingPropertyProvider(Class<P> propertyType) {
        this.propertyType = propertyType;
    }

    @Nullable
    @Override
    public P get(ItemType itemType, @Nullable ItemStack itemStack) {
        final LanternCookedFish cookedFish = (LanternCookedFish) (itemStack == null ? CookedFishes.COD : itemStack.get(Keys.COOKED_FISH).get());
        return cookedFish.getProperties().get(this.propertyType).map(provider -> provider.get(itemType, itemStack)).orElse(null);
    }
}
