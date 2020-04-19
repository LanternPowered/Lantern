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
package org.lanternpowered.server.network.vanilla.message.type.login

import io.netty.util.ReferenceCounted
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.message.Message

/**
 * A login channel response [Message] is send by the client after a
 * [MessageLoginOutChannelRequest] message is send by the server.
 * The transaction id in both messages should match to be valid.
 *
 * @param transactionId The transaction id of the response, the request will use the same id
 * @param content The content of the response message
 */
data class MessageLoginInChannelResponse(
        val transactionId: Int,
        val content: ByteBuffer
) : Message, ReferenceCounted by content
