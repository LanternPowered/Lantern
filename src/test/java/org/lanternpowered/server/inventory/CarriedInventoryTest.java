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
package org.lanternpowered.server.inventory;

import org.junit.Test;
import org.lanternpowered.server.inventory.constructor.InventoryConstructor;
import org.lanternpowered.server.inventory.constructor.InventoryConstructorFactory;
import org.lanternpowered.server.inventory.type.LanternGridInventory;

public class CarriedInventoryTest {

    @Test
    public void test() {
        final InventoryConstructorFactory factory = InventoryConstructorFactory.get();
        final InventoryConstructor<LanternGridInventory> constructor = factory.getConstructor(LanternGridInventory.class);
        System.out.println(constructor.construct(0).getClass().getName());
        System.out.println(constructor.construct(3).getClass().getName());
    }
}
