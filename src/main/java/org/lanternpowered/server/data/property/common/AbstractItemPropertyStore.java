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
package org.lanternpowered.server.data.property.common;

import org.spongepowered.api.data.property.PropertyHolder;
import org.spongepowered.api.data.property.store.PropertyStore;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

import javax.annotation.Nullable;

public abstract class AbstractItemPropertyStore<V> implements PropertyStore<V> {

    protected abstract Optional<V> getFor(ItemType itemType, @Nullable ItemStack itemStack);

    @Override
    public Optional<V> getFor(PropertyHolder propertyHolder) {
        if (propertyHolder instanceof ItemStack) {
            final ItemStack itemStack = (ItemStack) propertyHolder;
            return getFor(itemStack.getType(), itemStack);
        } else if (propertyHolder instanceof ItemType) {
            return getFor((ItemType) propertyHolder, null);
        }
        return Optional.empty();
    }

}
