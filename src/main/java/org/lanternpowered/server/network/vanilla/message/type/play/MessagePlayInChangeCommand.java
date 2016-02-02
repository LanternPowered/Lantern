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

import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.network.message.Message;

public abstract class MessagePlayInChangeCommand implements Message {

    private final String command;
    private final boolean shouldTrackOutput;

    MessagePlayInChangeCommand(String command, boolean shouldTrackOutput) {
        this.shouldTrackOutput = shouldTrackOutput;
        this.command = command;
    }

    public String getCommand() {
        return this.command;
    }

    public boolean shouldTrackOutput() {
        return this.shouldTrackOutput;
    }

    public static final class Entity extends MessagePlayInChangeCommand {

        private final int entityId;

        public Entity(int entityId, String command, boolean shouldTrackOutput) {
            super(command, shouldTrackOutput);
            this.entityId = entityId;
        }

        public int getEntityId() {
            return this.entityId;
        }
    }

    public static final class Block extends MessagePlayInChangeCommand {

        private final Vector3i position;

        public Block(Vector3i position, String command, boolean shouldTrackOutput) {
            super(command, shouldTrackOutput);
            this.position = position;
        }

        public Vector3i getBlockPosition() {
            return this.position;
        }
    }
}
