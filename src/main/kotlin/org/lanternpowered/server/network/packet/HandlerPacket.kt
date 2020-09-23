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

import org.lanternpowered.server.network.NettyThreadOnlyHelper

data class HandlerPacket<P : Packet> @JvmOverloads constructor(
        val packet: P,
        val handler: PacketHandler<in P>,
        val handleThread: HandleThread = getDefaultHandleThread(handler)
) : Packet {

    enum class HandleThread {
        /**
         * The message is handled to the main (sync) server thread.
         */
        MAIN,
        /**
         * The message is handled on the netty thread.
         */
        NETTY,
        /**
         * The message is handled on a async thread.
         */
        ASYNC,
    }
}

fun getDefaultHandleThread(handler: PacketHandler<*>): HandlerPacket.HandleThread {
    return if (NettyThreadOnlyHelper.isHandlerNettyThreadOnly(handler::class.java)) {
        HandlerPacket.HandleThread.NETTY
    } else {
        HandlerPacket.HandleThread.MAIN
    }
}
