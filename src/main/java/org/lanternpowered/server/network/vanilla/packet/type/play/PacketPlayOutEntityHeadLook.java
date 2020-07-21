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

public final class PacketPlayOutEntityHeadLook implements Packet {

    private final int entityId;
    private final byte yaw;

    public PacketPlayOutEntityHeadLook(int entityId, byte yaw) {
        this.entityId = entityId;
        this.yaw = yaw;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public byte getYaw() {
        return this.yaw;
    }

}
