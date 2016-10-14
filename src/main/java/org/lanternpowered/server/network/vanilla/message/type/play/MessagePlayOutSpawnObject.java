/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.vanilla.message.type.play;

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.network.message.Message;

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
