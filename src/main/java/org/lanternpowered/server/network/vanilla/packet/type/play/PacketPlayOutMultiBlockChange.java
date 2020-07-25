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

import java.util.Collection;

public final class PacketPlayOutMultiBlockChange implements Packet {

    private final int chunkX;
    private final int chunkZ;

    private final Collection<BlockChangePacket> changes;

    public PacketPlayOutMultiBlockChange(int chunkX, int chunkZ, Collection<BlockChangePacket> changes) {
        this.changes = changes;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkZ() {
        return this.chunkZ;
    }

    public Collection<BlockChangePacket> getChanges() {
        return this.changes;
    }
}
