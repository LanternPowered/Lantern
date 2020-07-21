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

import org.lanternpowered.server.network.message.Packet;

public interface BlockEntityProtocolUpdateContext {

    /**
     * Sends a {@link Packet} to all the trackers.
     *
     * @param packet The message
     */
    void send(Packet packet);
}
