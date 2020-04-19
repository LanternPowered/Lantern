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

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.math.vector.Vector3i;

public final class MessagePlayOutPlayerSpawnPosition implements Message {

    private final Vector3i position;

    /**
     * Creates the player spawn position message.
     * 
     * @param position the position
     */
    public MessagePlayOutPlayerSpawnPosition(Vector3i position) {
        this.position = checkNotNull(position, "position");
    }

    /**
     * Gets the spawn position of this message.
     * 
     * @return the position
     */
    public Vector3i getPosition() {
        return this.position;
    }

}
