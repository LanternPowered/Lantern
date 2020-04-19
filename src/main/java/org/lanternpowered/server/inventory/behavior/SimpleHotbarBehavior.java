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

import static com.google.common.base.Preconditions.checkArgument;

import org.lanternpowered.server.inventory.client.ClientContainer;

public class SimpleHotbarBehavior implements HotbarBehavior {

    private int slot;

    @Override
    public void setSelectedSlotIndex(int hotbarSlot) {
        checkArgument(hotbarSlot >= 0 && hotbarSlot <= 8);
        this.slot = hotbarSlot;
    }

    @Override
    public int getSelectedSlotIndex() {
        return this.slot;
    }

    @Override
    public void handleSelectedSlotChange(ClientContainer clientContainer, int hotbarSlot) {
        this.slot = hotbarSlot;
    }
}
