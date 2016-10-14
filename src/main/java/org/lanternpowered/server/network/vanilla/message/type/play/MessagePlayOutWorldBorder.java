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

public abstract class MessagePlayOutWorldBorder implements Message {

    public static final class Initialize extends MessagePlayOutWorldBorder {

        private final double centerX;
        private final double centerZ;

        private final double oldDiameter;
        private final double newDiameter;

        private final long lerpTime;

        // The (maximum) size of the world
        private final int worldSize;

        private final int warningDistance;
        private final int warningTime;

        public Initialize(double centerX, double centerZ, double oldDiameter, double newDiameter, long lerpTime,
                int worldSize, int warningDistance, int warningTime) {
            this.centerX = centerX;
            this.centerZ = centerZ;
            this.oldDiameter = oldDiameter;
            this.newDiameter = newDiameter;
            this.lerpTime = lerpTime;
            this.worldSize = worldSize;
            this.warningDistance = warningDistance;
            this.warningTime = warningTime;
        }

        public double getCenterX() {
            return this.centerX;
        }

        public double getCenterZ() {
            return this.centerZ;
        }

        public double getOldDiameter() {
            return this.oldDiameter;
        }

        public double getNewDiameter() {
            return this.newDiameter;
        }

        public long getLerpTime() {
            return this.lerpTime;
        }

        public int getWorldSize() {
            return this.worldSize;
        }

        public int getWarningDistance() {
            return this.warningDistance;
        }

        public int getWarningTime() {
            return this.warningTime;
        }
    }

    public static final class UpdateDiameter extends MessagePlayOutWorldBorder {

        private final double diameter;

        public UpdateDiameter(double diameter) {
            this.diameter = diameter;
        }

        public double getDiameter() {
            return this.diameter;
        }
    }

    public static final class UpdateLerpedDiameter extends MessagePlayOutWorldBorder {

        private final double oldDiameter;
        private final double newDiameter;

        private final long lerpTime;

        public UpdateLerpedDiameter(double oldDiameter, double newDiameter, long lerpTime) {
            this.oldDiameter = oldDiameter;
            this.newDiameter = newDiameter;
            this.lerpTime = lerpTime;
        }

        public double getOldDiameter() {
            return this.oldDiameter;
        }

        public double getNewDiameter() {
            return this.newDiameter;
        }

        public long getLerpTime() {
            return this.lerpTime;
        }
    }

    public static final class UpdateCenter extends MessagePlayOutWorldBorder {

        private final double x;
        private final double z;

        public UpdateCenter(double x, double z) {
            this.x = x;
            this.z = z;
        }

        public double getX() {
            return this.x;
        }

        public double getZ() {
            return this.z;
        }
    }

    public static final class UpdateWarningTime extends MessagePlayOutWorldBorder {

        private final int time;

        public UpdateWarningTime(int time) {
            this.time = time;
        }

        public int getTime() {
            return this.time;
        }
    }

    public static final class UpdateWarningDistance extends MessagePlayOutWorldBorder {

        private final int distance;

        public UpdateWarningDistance(int distance) {
            this.distance = distance;
        }

        public int getDistance() {
            return this.distance;
        }
    }

    MessagePlayOutWorldBorder() {
    }
}
