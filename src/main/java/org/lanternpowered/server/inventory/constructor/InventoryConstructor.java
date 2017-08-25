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
package org.lanternpowered.server.inventory.constructor;

import org.lanternpowered.server.inventory.AbstractInventory;

public abstract class InventoryConstructor<T extends AbstractInventory> {

    private final Class<T> inventoryType;
    private final Class<? extends T> carriedInventoryType;

    protected InventoryConstructor(Class<T> inventoryType, Class<? extends T> carriedInventoryType) {
        this.carriedInventoryType = carriedInventoryType;
        this.inventoryType = inventoryType;
    }

    /**
     * Gets the {@link AbstractInventory} type.
     *
     * @return The type
     */
    public Class<T> getType() {
        return this.inventoryType;
    }

    public T construct() {
        return construct(false);
    }

    /**
     * Constructs a new {@link AbstractInventory}.
     *
     * @param carried Whether the inventory can be carried
     * @return The inventory
     */
    public abstract T construct(boolean carried);
}
