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
package org.lanternpowered.server.network.packet

import org.lanternpowered.server.network.NetworkContext

interface PacketHandler<P : Packet> {

    /**
     * Handles a [Packet] that was received.
     *
     * @param ctx The context that received the message
     * @param packet The packet that was received
     */
    fun handle(ctx: NetworkContext, packet: P)

}
