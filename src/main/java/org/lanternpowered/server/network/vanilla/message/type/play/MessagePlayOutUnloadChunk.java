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

public final class MessagePlayOutUnloadChunk implements Message {

    private final int x;
    private final int z;

    public MessagePlayOutUnloadChunk(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getZ() {
        return this.z;
    }

    public int getX() {
        return this.x;
    }
}
