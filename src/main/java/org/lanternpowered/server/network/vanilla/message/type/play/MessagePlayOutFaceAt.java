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
import org.spongepowered.math.vector.Vector3d;

public abstract class MessagePlayOutFaceAt implements Message {

    private final Vector3d position;
    private final BodyPosition sourcePosition;

    private MessagePlayOutFaceAt(Vector3d position, BodyPosition sourcePosition) {
        this.position = position;
        this.sourcePosition = sourcePosition;
    }

    public Vector3d getPosition() {
        return this.position;
    }

    public BodyPosition getSourceBodyPosition() {
        return this.sourcePosition;
    }

    public static final class Position extends MessagePlayOutFaceAt {

        public Position(Vector3d position, BodyPosition sourcePosition) {
            super(position, sourcePosition);
        }
    }

    public static final class Entity extends MessagePlayOutFaceAt {

        private final int entityId;
        private final BodyPosition entityBodyPosition;

        public Entity(Vector3d fallbackPosition, BodyPosition sourcePosition,
                int entityId, BodyPosition entityBodyPosition) {
            super(fallbackPosition, sourcePosition);
            this.entityId = entityId;
            this.entityBodyPosition = entityBodyPosition;
        }

        public int getEntityId() {
            return this.entityId;
        }

        public BodyPosition getEntityBodyPosition() {
            return this.entityBodyPosition;
        }
    }

    public enum BodyPosition {
        FEET,
        EYES,
    }
}
