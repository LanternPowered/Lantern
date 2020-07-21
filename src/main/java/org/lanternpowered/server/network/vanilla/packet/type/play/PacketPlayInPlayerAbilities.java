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

/**
 * This message is send to update the flying state of the player.
 */
public class PacketPlayInPlayerAbilities implements Packet {

    private final boolean flying;

    /**
     * Creates a new player abilities message.
     * 
     * @param flying Whether the player is flying
     */
    public PacketPlayInPlayerAbilities(boolean flying) {
        this.flying = flying;
    }

    /**
     * Gets whether the player is flying.
     * 
     * @return Is flying
     */
    public boolean isFlying() {
        return this.flying;
    }

}
