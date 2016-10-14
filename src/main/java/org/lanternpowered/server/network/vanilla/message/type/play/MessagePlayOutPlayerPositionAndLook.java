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

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.util.RelativePositions;

import java.util.Set;

public final class MessagePlayOutPlayerPositionAndLook implements Message {

    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final int teleportId;
    private final Set<RelativePositions> relativePositions;

    public MessagePlayOutPlayerPositionAndLook(double x, double y, double z, float yaw, float pitch, Set<RelativePositions> relativePositions,
            int teleportId) {
        this.relativePositions = relativePositions;
        this.teleportId = teleportId;
        this.pitch = pitch;
        this.yaw = yaw;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getY() {
        return this.y;
    }

    public double getX() {
        return this.x;
    }

    public double getZ() {
        return this.z;
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

}
