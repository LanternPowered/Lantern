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

import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.network.message.Message;

public abstract class MessagePlayInEditCommandBlock implements Message {

    private final String command;
    private final boolean shouldTrackOutput;

    MessagePlayInEditCommandBlock(String command, boolean shouldTrackOutput) {
        this.command = checkNotNull(command, "command");
        this.shouldTrackOutput = shouldTrackOutput;
    }

    /**
     * Gets the new command.
     *
     * @return The command
     */
    public String getCommand() {
        return this.command;
    }

    /**
     * Gets whether the last output should be tracked.
     *
     * @return Track output
     */
    public boolean shouldTrackOutput() {
        return this.shouldTrackOutput;
    }

    public static final class Entity extends MessagePlayInEditCommandBlock {

        private final int entityId;

        public Entity(int entityId, String command, boolean shouldTrackOutput) {
            super(command, shouldTrackOutput);
            this.entityId = entityId;
        }

        /**
         * Gets the id of the entity that is being edited.
         *
         * @return The entity id
         */
        public int getEntityId() {
            return this.entityId;
        }
    }

    public static final class Block extends MessagePlayInEditCommandBlock {

        private final Vector3i position;
        private final Mode mode;
        private final boolean conditional;
        private final boolean automatic;

        public Block(Vector3i position, String command, boolean shouldTrackOutput, Mode mode, boolean conditional, boolean automatic) {
            super(command, shouldTrackOutput);
            this.position = checkNotNull(position, "position");
            this.mode = checkNotNull(mode, "mode");
            this.conditional = conditional;
            this.automatic = automatic;
        }

        public Vector3i getBlockPosition() {
            return this.position;
        }

        public Mode getMode() {
            return this.mode;
        }

        public boolean isConditional() {
            return this.conditional;
        }

        public boolean isAutomatic() {
            return this.automatic;
        }

        public enum Mode {
            SEQUENCE,
            AUTO,
            REDSTONE,
        }
    }
}
