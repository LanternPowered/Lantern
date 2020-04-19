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

import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.AbstractTopBottomShiftClickBehavior;
import org.lanternpowered.server.inventory.IInventory;
import org.lanternpowered.server.inventory.PlayerTopBottomContainer;

public class SimpleContainerShiftClickBehavior extends AbstractTopBottomShiftClickBehavior {

    public static final SimpleContainerShiftClickBehavior INSTANCE = new SimpleContainerShiftClickBehavior();

    @Override
    public IInventory getTarget(PlayerTopBottomContainer container, AbstractSlot slot) {
        // Just shift click to the top inventory as well by default, and
        // block shift clicking when the top is full
        if (container.getPlayerInventory().getPrimary().containsInventory(slot)) {
            return container.getOpenInventory();
        }
        return getDefaultTarget(container, slot);
    }
}
