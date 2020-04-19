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

public final class MessagePlayOutSpawnThunderbolt implements Message {

    private final int entityId;
    private final Vector3d position;

    public MessagePlayOutSpawnThunderbolt(int entityId, Vector3d position) {
        this.entityId = entityId;
        this.position = position;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public Vector3d getPosition() {
        return this.position;
    }
}
