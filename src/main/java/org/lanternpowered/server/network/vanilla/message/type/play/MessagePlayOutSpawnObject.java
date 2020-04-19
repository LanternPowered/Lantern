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

import java.util.UUID;

public final class MessagePlayOutSpawnObject implements Message {

    private final int entityId;
    private final UUID uniqueId;
    private final int objectType;
    private final int objectData;
    private final Vector3d position;
    private final int yaw;
    private final int pitch;
    private final Vector3d velocity;

    public MessagePlayOutSpawnObject(int entityId, UUID uniqueId, int objectType, int objectData,
            Vector3d position, int yaw, int pitch, Vector3d velocity) {
        this.entityId = entityId;
        this.uniqueId = uniqueId;
        this.objectType = objectType;
        this.objectData = objectData;
        this.position = position;
        this.yaw = yaw;
        this.pitch = pitch;
        this.velocity = velocity;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public int getObjectType() {
        return this.objectType;
    }

    public int getObjectData() {
        return this.objectData;
    }

    public Vector3d getPosition() {
        return this.position;
    }

    public int getYaw() {
        return this.yaw;
    }

    public int getPitch() {
        return this.pitch;
    }

    public Vector3d getVelocity() {
        return this.velocity;
    }
}
