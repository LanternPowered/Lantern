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
