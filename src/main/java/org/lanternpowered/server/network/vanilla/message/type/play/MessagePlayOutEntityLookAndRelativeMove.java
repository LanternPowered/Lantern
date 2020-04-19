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

public final class MessagePlayOutEntityLookAndRelativeMove implements Message {

    private final int entityId;
    private final boolean onGround;

    private final byte yaw;
    private final byte pitch;

    private final int deltaX;
    private final int deltaY;
    private final int deltaZ;

    public MessagePlayOutEntityLookAndRelativeMove(int entityId, int deltaX, int deltaY, int deltaZ,
            byte yaw, byte pitch, boolean onGround) {
        this.onGround = onGround;
        this.entityId = entityId;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.deltaZ = deltaZ;
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

    public int getDeltaX() {
        return this.deltaX;
    }

    public int getDeltaY() {
        return this.deltaY;
    }

    public int getDeltaZ() {
        return this.deltaZ;
    }
}
