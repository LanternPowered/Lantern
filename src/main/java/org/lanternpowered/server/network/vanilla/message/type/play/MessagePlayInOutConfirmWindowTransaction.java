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

public final class MessagePlayInOutConfirmWindowTransaction implements Message {

    private final int windowId;
    private final int transaction;
    private final boolean accepted;

    public MessagePlayInOutConfirmWindowTransaction(int windowId, int transaction, boolean accepted) {
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
