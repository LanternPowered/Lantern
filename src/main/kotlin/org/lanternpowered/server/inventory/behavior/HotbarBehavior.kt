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
package org.lanternpowered.server.inventory.behavior

import org.lanternpowered.server.inventory.client.ClientContainer

/**
 * Represents the behavior of how a hotbar should work.
 */
interface HotbarBehavior {

    /**
     * The selected hotbar slot index.
     *
     * @return The hotbar slot index
     */
    var selectedSlotIndex: Int

    /**
     * Handles a change in the selected hotbar slot which is caused by the client.
     *
     * @param clientContainer The client container
     * @param slotIndex The selected slot index
     */
    fun handleSelectedSlotChange(clientContainer: ClientContainer, slotIndex: Int)
}
