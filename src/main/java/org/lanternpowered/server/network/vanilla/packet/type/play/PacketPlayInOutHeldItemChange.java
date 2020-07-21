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
package org.lanternpowered.server.network.vanilla.packet.type.play;

import org.lanternpowered.server.network.message.Packet;

public final class PacketPlayInOutHeldItemChange implements Packet {

    private final int slot;

    /**
     * Creates a new held item change message.
     *
     * @param slot The new slot
     */
    public PacketPlayInOutHeldItemChange(int slot) {
        this.slot = slot;
    }

    /**
     * Gets the item slot the player changed to or will change to.
     * 
     * @return The slot
     */
    public int getSlot() {
        return this.slot;
    }

}
