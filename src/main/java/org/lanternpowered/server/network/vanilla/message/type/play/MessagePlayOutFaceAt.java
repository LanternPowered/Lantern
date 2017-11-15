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

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.network.message.Message;

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
