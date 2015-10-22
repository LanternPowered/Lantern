/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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

public final class MessagePlayOutWorldBorder implements Message {

    /**
     * Builds a world border message with the {@link Action#SET_SIZE} action.
     * 
     * @param radius the new radius
     * @return the message
     */
    public static MessagePlayOutWorldBorder setSize(double radius) {
        MessagePlayOutWorldBorder message = new MessagePlayOutWorldBorder();
        message.action = Action.SET_SIZE;
        message.radiusNew = radius;
        return message;
    }

    /**
     * Builds a world border message with the {@link Action#LERP_SIZE} action.
     * 
     * @param radiusOld the start radius
     * @param radiusNew the end radius
     * @param lerpTime the lerp time in seconds
     * @return the message
     */
    public static MessagePlayOutWorldBorder lerpSize(double radiusOld, double radiusNew, long lerpTime) {
        MessagePlayOutWorldBorder message = new MessagePlayOutWorldBorder();
        message.action = Action.LERP_SIZE;
        message.radiusOld = radiusOld;
        message.radiusNew = radiusNew;
        message.lerpTime = lerpTime;
        return message;
    }

    /**
     * Builds a world border message with the {@link Action#SET_CENTER} action.
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the message
     */
    public static MessagePlayOutWorldBorder setCenter(double x, double z) {
        MessagePlayOutWorldBorder message = new MessagePlayOutWorldBorder();
        message.action = Action.SET_CENTER;
        message.x = x;
        message.z = z;
        return message;
    }

    /**
     * Builds a world border message with the {@link Action#INITIALIZE} action.
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @param radiusOld the start radius
     * @param radiusNew the end radius
     * @param lerpTime the lerp time in seconds
     * @param worldSize the size of the world the border is located in
     * @param warningTime the amount of time before your screen becomes red
     * @param warningBlocks the amount of blocks from the border
     * @return the message
     */
    public static MessagePlayOutWorldBorder initialize(double x, double z, double radiusOld,
            double radiusNew, long lerpTime, int worldSize, int warningBlocks, int warningTime) {
        MessagePlayOutWorldBorder message = new MessagePlayOutWorldBorder();
        message.action = Action.INITIALIZE;
        message.worldSize = worldSize;
        message.warningBlocks = warningBlocks;
        message.warningTime = warningTime;
        message.radiusOld = radiusOld;
        message.radiusNew = radiusNew;
        message.lerpTime = lerpTime;
        message.x = x;
        message.z = z;
        return message;
    }

    /**
     * Builds a world border message with the {@link Action#SET_WARNING_TIME}
     * action.
     * 
     * @param warningTime the amount of time before your screen becomes red
     * @return the message
     */
    public static MessagePlayOutWorldBorder setWarningTime(int warningTime) {
        MessagePlayOutWorldBorder message = new MessagePlayOutWorldBorder();
        message.action = Action.SET_WARNING_TIME;
        message.warningTime = warningTime;
        return message;
    }

    /**
     * Builds a world border message with the {@link Action#SET_WARNING_BLOCKS}
     * action.
     * 
     * @param warningBlocks the amount of blocks from the border
     * @return the message
     */
    public static MessagePlayOutWorldBorder setWarningBlocks(int warningBlocks) {
        MessagePlayOutWorldBorder message = new MessagePlayOutWorldBorder();
        message.action = Action.SET_WARNING_BLOCKS;
        message.warningBlocks = warningBlocks;
        return message;
    }

    private Action action;

    private double x;
    private double z;

    private double radiusOld;
    private double radiusNew;

    private long lerpTime;

    // The (maximum) size of the world
    private int worldSize;

    private int warningBlocks;
    private int warningTime;

    private MessagePlayOutWorldBorder() {
    }

    /**
     * Gets the action of this message.
     * 
     * @return the action
     */
    public Action getAction() {
        return this.action;
    }

    /**
     * Gets the x coordinate of the center.
     * 
     * @return the x coordinate
     */
    public double getX() {
        return this.x;
    }

    /**
     * Gets the z coordinate of the center.
     * 
     * @return the z coordinate
     */
    public double getZ() {
        return this.z;
    }

    /**
     * Gets the old or start radius.
     * 
     * @return the radius
     */
    public double getOldRadius() {
        return this.radiusOld;
    }

    /**
     * Gets the new or end radius.
     * 
     * @return the radius
     */
    public double getNewRadius() {
        return this.radiusNew;
    }

    /**
     * Gets the amount of time it takes to lerp from the old to new radius.
     * 
     * @return the amount of time
     */
    public long getLerpTime() {
        return this.lerpTime;
    }

    /**
     * The size of the world the border is located in.
     * 
     * @return the size
     */
    public int getWorldSize() {
        return this.worldSize;
    }

    /**
     * Gets the warning blocks.
     * 
     * @return the warning blocks
     */
    public int getWarningBlocks() {
        return this.warningBlocks;
    }

    /**
     * Gets the warning time.
     * 
     * @return the warning time
     */
    public int getWarningTime() {
        return this.warningTime;
    }

    public enum Action {
        SET_SIZE            (0),
        LERP_SIZE           (1),
        SET_CENTER          (2),
        INITIALIZE          (3),
        SET_WARNING_TIME    (4),
        SET_WARNING_BLOCKS  (5);

        private final byte id;

        Action(int id) {
            this.id = (byte) id;
        }

        /**
         * Gets the id of the action.
         * 
         * @return the id
         */
        public byte getId() {
            return this.id;
        }

    }
}
