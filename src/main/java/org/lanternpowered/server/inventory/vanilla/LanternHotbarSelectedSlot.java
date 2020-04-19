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
package org.lanternpowered.server.inventory.vanilla;

import org.lanternpowered.server.inventory.AbstractForwardingSlot;
import org.lanternpowered.server.inventory.AbstractSlot;

public class LanternHotbarSelectedSlot extends AbstractForwardingSlot {

    private final LanternHotbarInventory hotbarInventory;

    public LanternHotbarSelectedSlot(LanternHotbarInventory hotbarInventory) {
        this.hotbarInventory = hotbarInventory;
    }

    @Override
    protected AbstractSlot getDelegateSlot() {
        return this.hotbarInventory.getSelectedSlot();
    }
}
