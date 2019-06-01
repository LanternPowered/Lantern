/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.channel

import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.network.NetworkSession
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.buffer.ByteBufferAllocator
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.Platform
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.network.ChannelBuf
import org.spongepowered.api.network.ClientConnection
import org.spongepowered.api.network.raw.RawDataChannel
import org.spongepowered.api.network.raw.RawDataHandler
import java.util.function.Consumer

/**
 * A simple raw data channel, the client side handlers will be ignored since
 * we will only provide a server implementation.
 */
internal class LanternRawDataChannel(registrar: LanternChannelRegistrar, key: CatalogKey) :
        LanternChannelBinding(registrar, key), RawDataChannel {

    private val handlers = mutableListOf<RawDataHandler>()

    override fun addHandler(handler: RawDataHandler) {
        checkBound()
        this.handlers.add(handler)
    }

    override fun addHandler(side: Platform.Type, handler: RawDataHandler) {
        checkBound()
        if (side == Platform.Type.SERVER) {
            this.handlers.add(handler)
        }
    }

    override fun removeHandler(handler: RawDataHandler) {
        checkBound()
        this.handlers.remove(handler)
    }

    override fun sendToServer(payload: Consumer<ChannelBuf>) {
        checkBound()
    }

    override fun sendToAll(payload: Consumer<ChannelBuf>) {
        checkBound()
        val buf by lazy { ByteBufferAllocator.pooled().buffer().apply(payload::accept) }
        Lantern.getServer().rawOnlinePlayers.forEach { player ->
            if (player.connection.hasChannel(this.name)) {
                player.connection.send(MessagePlayInOutChannelPayload(this.name, buf))
            }
        }
    }

    override fun sendTo(player: Player, payload: Consumer<ChannelBuf>) {
        sendTo(player.connection, payload)
    }

    override fun sendTo(connection: ClientConnection, payload: Consumer<ChannelBuf>) {
        checkBound()
        (connection as NetworkSession).apply {
            if (hasChannel(name)) {
                val buf = ByteBufferAllocator.pooled().buffer().apply(payload::accept)
                send(MessagePlayInOutChannelPayload(name, buf))
            }
        }
    }

    override fun handlePayload(buf: ByteBuffer, connection: NetworkSession) {
        Lantern.getSyncScheduler().submit {
            for (listener in this.handlers) {
                // We slice the buffer, to preserve the reader index for all the handlers,
                // the buffer shouldn't be modified in any way
                listener.handlePayload(buf.slice(), connection, Platform.Type.SERVER)
            }
        }
    }

    override fun handleLoginPayload(buf: ByteBuffer?, transactionId: Int, connection: NetworkSession) {
    }
}
