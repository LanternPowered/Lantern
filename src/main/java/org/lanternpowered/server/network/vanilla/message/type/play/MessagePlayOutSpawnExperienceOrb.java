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

public final class MessagePlayOutSpawnExperienceOrb implements Message {

    private final int entityId;
    private final int quantity;
    private final Vector3d position;

    public MessagePlayOutSpawnExperienceOrb(int entityId, int quantity, Vector3d position) {
        this.entityId = entityId;
        this.position = position;
        this.quantity = quantity;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public Vector3d getPosition() {
        return this.position;
    }
}
