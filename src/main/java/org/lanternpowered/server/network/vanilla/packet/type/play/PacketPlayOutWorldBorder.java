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

import org.lanternpowered.server.network.packet.Packet;

public abstract class PacketPlayOutWorldBorder implements Packet {

    public static final class Initialize extends PacketPlayOutWorldBorder {

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

    public static final class UpdateDiameter extends PacketPlayOutWorldBorder {

        private final double diameter;

        public UpdateDiameter(double diameter) {
            this.diameter = diameter;
        }

        public double getDiameter() {
            return this.diameter;
        }
    }

    public static final class UpdateLerpedDiameter extends PacketPlayOutWorldBorder {

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

    public static final class UpdateCenter extends PacketPlayOutWorldBorder {

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

    public static final class UpdateWarningTime extends PacketPlayOutWorldBorder {

        private final int time;

        public UpdateWarningTime(int time) {
            this.time = time;
        }

        public int getTime() {
            return this.time;
        }
    }

    public static final class UpdateWarningDistance extends PacketPlayOutWorldBorder {

        private final int distance;

        public UpdateWarningDistance(int distance) {
            this.distance = distance;
        }

        public int getDistance() {
            return this.distance;
        }
    }

    PacketPlayOutWorldBorder() {
    }
}
