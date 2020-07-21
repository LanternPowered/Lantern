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

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Packet;
import org.spongepowered.math.vector.Vector3i;

public abstract class PacketPlayInEditCommandBlock implements Packet {

    private final String command;
    private final boolean shouldTrackOutput;

    PacketPlayInEditCommandBlock(String command, boolean shouldTrackOutput) {
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

    public static final class Entity extends PacketPlayInEditCommandBlock {

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

    public static final class Block extends PacketPlayInEditCommandBlock {

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
