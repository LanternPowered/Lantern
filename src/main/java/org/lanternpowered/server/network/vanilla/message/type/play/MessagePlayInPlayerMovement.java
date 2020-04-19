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

public final class MessagePlayInPlayerMovement implements Message {

    private final boolean onGround;
    private final Vector3d position;

    public MessagePlayInPlayerMovement(Vector3d position, boolean onGround) {
        this.position = position;
        this.onGround = onGround;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public Vector3d getPosition() {
        return this.position;
    }
}
