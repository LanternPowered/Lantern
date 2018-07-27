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

import java.util.List;
import java.util.function.IntFunction;

public final class InventoryConstructor<T extends AbstractInventory> {

    public static int CARRIED = 0x1;
    public static int VIEWABLE = 0x2;

    private final Class<T> inventoryType;
    private final List<Class<T>> classes;
    private final IntFunction<T> constructor;

    InventoryConstructor(Class<T> inventoryType,
            List<Class<T>> classes, IntFunction<T> constructor) {
        this.inventoryType = inventoryType;
        this.classes = classes;
        this.constructor = constructor;
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
        return construct(0);
    }

    /**
     * Constructs a new {@link AbstractInventory}.
     *
     * @param flags The flags
     * @return The inventory
     */
    public T construct(int flags) {
        return this.constructor.apply(flags);
    }
}
