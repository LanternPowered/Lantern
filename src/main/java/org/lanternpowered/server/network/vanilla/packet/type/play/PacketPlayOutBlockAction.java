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

import org.lanternpowered.server.block.action.BlockActionData;
import org.lanternpowered.server.network.message.Packet;
import org.spongepowered.math.vector.Vector3i;

public final class PacketPlayOutBlockAction implements Packet, BlockActionData {

    private final Vector3i position;
    private final int blockType;
    private final int[] parameters = new int[2];

    public PacketPlayOutBlockAction(Vector3i position, int blockType) {
        this.blockType = blockType;
        this.position = position;
    }

    public Vector3i getPosition() {
        return this.position;
    }

    public int getBlockType() {
        return this.blockType;
    }

    public int[] getParameters() {
        return this.parameters;
    }

    @Override
    public void set(int index, int data) {
        if (index >= 0 && index < this.parameters.length) {
            this.parameters[index] = data;
        }
    }
}
