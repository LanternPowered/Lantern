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
package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.math.vector.Vector3i;

public abstract class MessagePlayInDataRequest implements Message {

    private final int transactionId;

    MessagePlayInDataRequest(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    public static class Entity extends MessagePlayInDataRequest {

        private final int entityId;

        public Entity(int transactionId, int entityId) {
            super(transactionId);
            this.entityId = entityId;
        }

        public int getEntityId() {
            return this.entityId;
        }
    }

    public static class Block extends MessagePlayInDataRequest {

        private final Vector3i position;

        public Block(int transactionId, Vector3i position) {
            super(transactionId);
            this.position = position;
        }

        public Vector3i getPosition() {
            return this.position;
        }
    }
}
