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
package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.math.vector.Vector3d;

public final class MessagePlayOutEntityTeleport implements Message {

    private final int entityId;
    private final boolean onGround;

    private final byte yaw;
    private final byte pitch;

    private final Vector3d position;

    public MessagePlayOutEntityTeleport(int entityId, Vector3d position, byte yaw, byte pitch, boolean onGround) {
        this.position = position;
        this.onGround = onGround;
        this.entityId = entityId;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public byte getYaw() {
        return this.yaw;
    }

    public byte getPitch() {
        return this.pitch;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public Vector3d getPosition() {
        return this.position;
    }
}
