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

import org.lanternpowered.server.inventory.client.ClientContainer;

public interface HotbarBehavior {

    /**
     * Sets the selected hotbar slot index.
     *
     * @param hotbarSlot THe hotbar slot index
     */
    void setSelectedSlotIndex(int hotbarSlot);

    /**
     * Gets the selected hotbar slot index.
     *
     * @return The hotbar slot index
     */
    int getSelectedSlotIndex();

    /**
     * Handles a change in the selected hotbar slot
     * which is caused by the client.
     *
     * @param clientContainer The client container
     * @param hotbarSlot The selected slot index
     */
    void handleSelectedSlotChange(ClientContainer clientContainer, int hotbarSlot);
}
