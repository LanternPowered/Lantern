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
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.math.vector.Vector3d;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class MessagePlayInUseEntity implements Message {

    private final int entityId;

    private MessagePlayInUseEntity(int entityId) {
        this.entityId = entityId;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public static final class Attack extends MessagePlayInUseEntity {

        public Attack(int entityId) {
            super(entityId);
        }
    }

    public static final class Interact extends MessagePlayInUseEntity {

        private final HandType handType;
        @Nullable private final Vector3d position;

        public Interact(int entityId, HandType handType, @Nullable Vector3d position) {
            super(entityId);
            this.position = position;
            this.handType = handType;
        }

        public HandType getHandType() {
            return this.handType;
        }

        public Optional<Vector3d> getPosition() {
            return Optional.ofNullable(this.position);
        }
    }
}
