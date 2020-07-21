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
import org.spongepowered.math.vector.Vector3i;

public abstract class PacketPlayInDataRequest implements Packet {

    private final int transactionId;

    PacketPlayInDataRequest(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    public static class Entity extends PacketPlayInDataRequest {

        private final int entityId;

        public Entity(int transactionId, int entityId) {
            super(transactionId);
            this.entityId = entityId;
        }

        public int getEntityId() {
            return this.entityId;
        }
    }

    public static class Block extends PacketPlayInDataRequest {

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
