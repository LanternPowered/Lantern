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
package org.lanternpowered.server.data.property.inventory;

import org.lanternpowered.server.inventory.property.AbstractInventoryProperty;
import org.spongepowered.api.data.property.PropertyHolder;
import org.spongepowered.api.data.property.store.PropertyStore;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.world.Location;

import java.util.Optional;

public class InventoryPropertyStore<V> implements PropertyStore<V> {

    private Class<T> property;

    public InventoryPropertyStore(Class<T> property) {
        this.property = property;
    }

    @Override
    public Optional<T> getFor(PropertyHolder propertyHolder) {
        if (propertyHolder instanceof Location) {
            return ((Location<?>) propertyHolder).getTileEntity()
                    .flatMap(te -> (te instanceof Carrier) ? Optional.of(((Carrier) te).getInventory()) : Optional.empty())
                    .flatMap(this::getFor);
        } else if (propertyHolder instanceof Inventory) {
            final Object key = AbstractInventoryProperty.getDefaultKey(this.property);
            return ((Inventory) propertyHolder).getProperty(this.property, key);
        }
        return Optional.empty();
    }
}
