/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
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

public final class MessagePlayOutPlayerPositionAndLook implements Message {

    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final int flags;
    private final int teleportId;

    public MessagePlayOutPlayerPositionAndLook(double x, double y, double z, float yaw, float pitch, int flags, int teleportId) {
        this.teleportId = teleportId;
        this.pitch = pitch;
        this.flags = flags;
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

    public int getFlags() {
        return this.flags;
    }

    public int getTeleportId() {
        return this.teleportId;
    }

    public static class Flags {
        public static final int RELATIVE_X = 0x1;
        public static final int RELATIVE_Y = 0x2;
        public static final int RELATIVE_Z = 0x4;
        public static final int RELATIVE_PITCH = 0x8;
        public static final int RELATIVE_YAW = 0x10;
    }
}
