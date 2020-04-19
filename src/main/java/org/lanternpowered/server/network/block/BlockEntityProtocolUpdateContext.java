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
package org.lanternpowered.server.network.block;

import org.lanternpowered.server.network.message.Message;

public interface BlockEntityProtocolUpdateContext {

    /**
     * Sends a {@link Message} to all the trackers.
     *
     * @param message The message
     */
    void send(Message message);
}
