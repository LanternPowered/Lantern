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
package org.lanternpowered.server.network.message.handler

import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.message.Message

interface Handler<M : Message> {

    /**
     * Handles a [Message] that was received.
     *
     * @param context The context that received the message
     * @param message The message that was received
     */
    fun handle(context: NetworkContext, message: M)

}
