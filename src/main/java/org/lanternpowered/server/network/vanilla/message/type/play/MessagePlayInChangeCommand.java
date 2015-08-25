package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;

import com.flowpowered.math.vector.Vector3i;

public class MessagePlayInChangeCommand implements Message {

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

    public static class Entity extends MessagePlayInChangeCommand {

        private final int entityId;

        public Entity(int entityId, String command, boolean shouldTrackOutput) {
            super(command, shouldTrackOutput);
            this.entityId = entityId;
        }

        public int getEntityId() {
            return this.entityId;
        }
    }

    public static class Block extends MessagePlayInChangeCommand {

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
