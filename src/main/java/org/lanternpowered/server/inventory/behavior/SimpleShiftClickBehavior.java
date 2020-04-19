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
package org.lanternpowered.server.inventory.behavior;

import org.lanternpowered.server.inventory.AbstractContainer;
import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.IInventory;

public class SimpleShiftClickBehavior extends AbstractShiftClickBehavior {

    public static final SimpleShiftClickBehavior INSTANCE = new SimpleShiftClickBehavior();

    @Override
    public IInventory getTarget(AbstractContainer container, AbstractSlot slot) {
        return getDefaultTarget(container, slot);
    }
}
