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

public final class PacketPlayInOutConfirmWindowTransaction implements Packet {

    private final int windowId;
    private final int transaction;
    private final boolean accepted;

    public PacketPlayInOutConfirmWindowTransaction(int windowId, int transaction, boolean accepted) {
        this.transaction = transaction;
        this.windowId = windowId;
        this.accepted = accepted;
    }

    public int getWindowId() {
        return this.windowId;
    }

    public int getTransaction() {
        return this.transaction;
    }

    public boolean isAccepted() {
        return this.accepted;
    }
}
