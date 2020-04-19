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

public final class MessagePlayOutBlockChange implements Message {

    private final Vector3i position;
    private final int blockState;

    public MessagePlayOutBlockChange(Vector3i position, int blockState) {
        this.blockState = blockState;
        this.position = position;
    }

    public Vector3i getPosition() {
        return this.position;
    }

    public int getBlockState() {
        return this.blockState;
    }
}
