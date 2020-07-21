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
import org.spongepowered.math.vector.Vector3d;

public final class PacketPlayInPlayerVehicleMovement implements Packet {

    private final Vector3d position;
    private final float yaw;
    private final float pitch;

    public PacketPlayInPlayerVehicleMovement(Vector3d position, float yaw, float pitch) {
        this.position = position;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public Vector3d getPosition() {
        return this.position;
    }
}
