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
package org.lanternpowered.server.network.vanilla.message.type.status;

import org.lanternpowered.server.network.message.Message;

public final class MessageStatusInOutPing implements Message {

    private final long time;

    public MessageStatusInOutPing(long time) {
        this.time = time;
    }

    /**
     * Gets the time value.
     * 
     * @return The time
     */
    public long getTime() {
        return this.time;
    }

}
