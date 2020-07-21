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
import org.spongepowered.math.vector.Vector3i;

public final class PacketPlayOutBlockBreakAnimation implements Packet {

    private final Vector3i position;
    private final int state;
    private final int id;

    /**
     * Creates a new block break animation message. The id must be unique for
     * every break animation and the state must be between 0-9 in order to
     * create/update the animation, and any other value will remove it.
     * 
     * @param position the position
     * @param id the id
     * @param state the state
     */
    public PacketPlayOutBlockBreakAnimation(Vector3i position, int id, int state) {
        this.position = position;
        this.state = state;
        this.id = id;
    }

    /**
     * Gets the position of the block animation.
     * 
     * @return the position
     */
    public Vector3i getPosition() {
        return this.position;
    }

    /**
     * Gets the id of the block animation.
     * 
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets the state of the block animation.
     * 
     * @return the state
     */
    public int getState() {
        return this.state;
    }
}
