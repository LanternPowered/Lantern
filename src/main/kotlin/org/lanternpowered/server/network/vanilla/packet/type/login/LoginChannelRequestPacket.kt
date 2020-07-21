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
package org.lanternpowered.server.network.vanilla.packet.type.login

import io.netty.util.ReferenceCounted
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.message.Packet

/**
 * A message send by the server to the client to request for data.
 *
 * @param transactionId The transaction id of the request, the response will use the same id
 * @param channel The channel this message is send to
 * @param content The content of the request message
 */
data class LoginChannelRequestPacket(
        val transactionId: Int,
        val channel: String,
        val content: ByteBuffer
) : Packet, ReferenceCounted by content
