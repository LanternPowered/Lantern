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

import org.lanternpowered.api.ext.*
import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.network.NetworkSession
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.buffer.ByteBufferAllocator
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutChannelRequest
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.Platform
import org.spongepowered.api.network.ChannelBuf
import org.spongepowered.api.network.ClientConnection
import org.spongepowered.api.network.NoResponseException
import org.spongepowered.api.network.raw.login.RawLoginDataChannel
import org.spongepowered.api.network.raw.login.RawLoginDataRequestHandler
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

internal class LanternRawLoginDataChannel(registrar: LanternChannelRegistrar, key: CatalogKey) :
        LanternChannelBinding(registrar, key), RawLoginDataChannel {

    override fun setRequestHandler(side: Platform.Type, handler: RawLoginDataRequestHandler) {
        // Only on the client side, for now
    }

    override fun setRequestHandler(handler: RawLoginDataRequestHandler) {
        // Only on the client side, for now
    }

    private class Transaction(channel: String, val completableFuture: CompletableFuture<ChannelBuf>) : ChannelTransaction(channel)

    override fun sendTo(connection: ClientConnection, payload: Consumer<ChannelBuf>): CompletableFuture<ChannelBuf> {
        connection as NetworkSession

        val transactionStore = connection.transactionStore
        // Only supported during login phase
        check(transactionStore is LoginChannelTransactionStore) { "The RawLoginDataChannel is only usable during the login handshake." }

        val buf = ByteBufferAllocator.pooled().buffer()
        payload.accept(buf)

        val completableFuture = CompletableFuture<ChannelBuf>()
        val transactionId = transactionStore.nextId()

        // Store the completable future
        transactionStore.put(transactionId, Transaction(this.name, completableFuture))
        // Send the message, also append a handler to catch exception if the message fails to send
        connection.sendWithFuture(MessageLoginOutChannelRequest(transactionId, this.name, buf)).addListener { future ->
            if (!future.isSuccess) {
                completableFuture.completeExceptionally(future.cause())
            }
        }

        return completableFuture
    }

    override fun handlePayload(buf: ByteBuffer, connection: NetworkSession) {
        // Should never happen
    }

    override fun handleLoginPayload(buf: ByteBuffer?, transactionId: Int, connection: NetworkSession) {
        val transactionStore = connection.transactionStore
        val transaction = transactionStore.getData(transactionId)

        if (transaction is Transaction) {
            // Handle the response on the sync scheduler
            Lantern.getSyncScheduler().submit {
                val future = transaction.completableFuture
                if (buf == null) {
                    future.completeExceptionally(NoResponseException())
                } else {
                    future.uncheckedCast<CompletableFuture<ChannelBuf>>().complete(buf)
                }
                // The login phase needs to keep track of handled transactions to be
                // able to continue, so delay the removal until it's actually handled
                transactionStore.removeData(transactionId)
            }
        }
    }
}
