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
import org.spongepowered.api.util.RelativePositions;
import org.spongepowered.math.vector.Vector3d;

import java.util.Set;

public final class MessagePlayOutPlayerPositionAndLook implements Message {

    private final Vector3d position;
    private final float yaw;
    private final float pitch;
    private final int teleportId;
    private final Set<RelativePositions> relativePositions;

    public MessagePlayOutPlayerPositionAndLook(Vector3d position, float yaw, float pitch,
            Set<RelativePositions> relativePositions, int teleportId) {
        this.position = position;
        this.relativePositions = relativePositions;
        this.teleportId = teleportId;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public Set<RelativePositions> getRelativePositions() {
        return this.relativePositions;
    }

    public int getTeleportId() {
        return this.teleportId;
    }

    public Vector3d getPosition() {
        return this.position;
    }
}
