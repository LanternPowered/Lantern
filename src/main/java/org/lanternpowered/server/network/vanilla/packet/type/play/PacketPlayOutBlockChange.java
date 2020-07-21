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

public final class PacketPlayOutBlockChange implements Packet {

    private final Vector3i position;
    private final int blockState;

    public PacketPlayOutBlockChange(Vector3i position, int blockState) {
        this.blockState = blockState;
        this.position = position;
    }

    public Vector3i getPosition() {
        return this.position;
    }

    public int getBlockState() {
        return this.blockState;
    }
}
