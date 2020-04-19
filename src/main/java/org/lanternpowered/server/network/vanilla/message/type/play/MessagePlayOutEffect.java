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

public final class MessagePlayOutEffect implements Message {

    private final Vector3i position;
    private final int type;
    private final int data;
    private final boolean broadcast;

    public MessagePlayOutEffect(Vector3i position, int type, int data, boolean broadcast) {
        this.position = position;
        this.type = type;
        this.data = data;
        this.broadcast = broadcast;
    }

    public Vector3i getPosition() {
        return this.position;
    }

    public int getData() {
        return this.data;
    }

    public int getType() {
        return this.type;
    }

    public boolean isBroadcast() {
        return this.broadcast;
    }
}
