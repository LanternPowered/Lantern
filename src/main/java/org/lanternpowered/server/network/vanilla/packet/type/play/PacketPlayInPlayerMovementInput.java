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

import org.lanternpowered.server.network.packet.Packet;

/**
 * This is the only message that we will use to modify the controls
 * of the player. More info will come as I write the implementation.
 */
public final class PacketPlayInPlayerMovementInput implements Packet {

    private final float sideways;
    private final float forwards;

    public PacketPlayInPlayerMovementInput(float forwards, float sideways) {
        this.sideways = sideways;
        this.forwards = forwards;
    }

    /**
     * Gets the forwards value. (Positive is forwards, negative is backwards.)
     * 
     * @return the forwards value
     */
    public float getForwards() {
        return this.forwards;
    }

    /**
     * Gets the sideways value. (Positive is left, negative is right.)
     * 
     * @return the sideways value
     */
    public float getSideways() {
        return this.sideways;
    }
}
