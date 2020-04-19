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
package org.lanternpowered.server.network.message

import org.lanternpowered.server.network.NettyThreadOnlyHelper
import org.lanternpowered.server.network.message.handler.Handler

data class HandlerMessage<M : Message> @JvmOverloads constructor(
        val message: M,
        val handler: Handler<in M>,
        val handleThread: HandleThread = getDefaultHandleThread(handler)
) : Message {

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

fun getDefaultHandleThread(handler: Handler<*>): HandlerMessage.HandleThread {
    return if (NettyThreadOnlyHelper.isHandlerNettyThreadOnly(handler::class.java)) {
        HandlerMessage.HandleThread.NETTY
    } else {
        HandlerMessage.HandleThread.MAIN
    }
}
