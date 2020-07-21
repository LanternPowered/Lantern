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
package org.lanternpowered.server.network.vanilla.packet.type.play;

import org.lanternpowered.server.network.packet.Packet;

public final class PacketPlayInDropHeldItem implements Packet {

    private final boolean fullStack;

    public PacketPlayInDropHeldItem(boolean fullStack) {
        this.fullStack = fullStack;
    }

    /**
     * Whether a full item stack should be dropped
     * instead of one item.
     *
     * @return is full stack
     */
    public boolean isFullStack() {
        return this.fullStack;
    }
}
