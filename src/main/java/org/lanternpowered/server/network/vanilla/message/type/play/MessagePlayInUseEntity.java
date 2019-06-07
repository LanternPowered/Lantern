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
