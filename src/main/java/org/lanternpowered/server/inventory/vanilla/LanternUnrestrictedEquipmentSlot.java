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

import org.lanternpowered.server.inventory.type.slot.LanternEquipmentSlot;

public class LanternUnrestrictedEquipmentSlot extends LanternEquipmentSlot {

    @Override
    protected void init() {
        super.init();
        // Get rid of the filter, any item can be held
        setFilter(null);
    }
}
