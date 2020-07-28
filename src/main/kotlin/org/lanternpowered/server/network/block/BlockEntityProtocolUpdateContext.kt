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
package org.lanternpowered.server.network.block

import org.lanternpowered.server.network.packet.Packet

interface BlockEntityProtocolUpdateContext {

    /**
     * Sends a [Packet] to all the trackers.
     *
     * @param packet The packet
     */
    fun send(packet: Packet)
}
