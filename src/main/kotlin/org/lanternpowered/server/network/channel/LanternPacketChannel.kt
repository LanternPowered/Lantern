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

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.lanternpowered.api.ext.*
import org.lanternpowered.lmbda.kt.createLambda
import org.lanternpowered.lmbda.kt.privateLookupIn
import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.network.NetworkSession
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.buffer.ByteBufferAllocator
import org.lanternpowered.server.network.message.Message
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutChannelRequest
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.Platform
import org.spongepowered.api.network.ChannelException
import org.spongepowered.api.network.ClientConnection
import org.spongepowered.api.network.NoResponseException
import org.spongepowered.api.network.packet.Packet
import org.spongepowered.api.network.packet.PacketBinding
import org.spongepowered.api.network.packet.PacketChannel
import org.spongepowered.api.network.packet.PacketIOException
import org.spongepowered.api.network.packet.RequestPacket
import org.spongepowered.api.network.packet.RequestPacketResponse
import org.spongepowered.api.network.packet.ResponsePacket
import org.spongepowered.api.network.packet.TransactionalPacketBinding
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.concurrent.CompletableFuture

internal class LanternPacketChannel(registrar: LanternChannelRegistrar, key: CatalogKey) :
        LanternChannelBinding(registrar, key), PacketChannel {

    private val byOpcode = Int2ObjectOpenHashMap<Any>()
    private val byClass = HashMap<Class<*>, PacketBinding<*>>()

    private val transactionalBindingByRequest = HashMap<Class<*>, TransactionalPacketBinding<*, *>>()

    override fun <M : Packet> register(packetClass: Class<M>, packetId: Int): PacketBinding<M> {
        check(packetClass !in this.byClass) { "The packet class ${packetClass.name} is already registered." }
        check(packetId !in this.byOpcode) { "The packet opcode $packetId is already used." }
        val binding = createPacketBinding(packetId, packetClass)
        this.byClass[packetClass] = binding
        this.byOpcode[packetId] = binding
        return binding
    }

    override fun <M : RequestPacket<R>, R : ResponsePacket> register(
            requestPacketClass: Class<M>, responsePacketClass: Class<R>, packetOpcode: Int): TransactionalPacketBinding<M, R> {
        check(requestPacketClass !in this.byClass) { "The request packet class ${requestPacketClass.name} is already registered." }
        check(responsePacketClass !in this.byClass) { "The response packet class ${responsePacketClass.name} is already registered." }
        check(packetOpcode !in this.byOpcode) { "The packet opcode $packetOpcode is already used." }
        val requestBinding = createPacketBinding(packetOpcode, requestPacketClass)
        val responseBinding = createPacketBinding(packetOpcode, responsePacketClass)
        val transactionalBinding = LanternTransactionalPacketBinding(packetOpcode, requestBinding, responseBinding)
        this.byClass[requestPacketClass] = requestBinding
        this.byClass[responsePacketClass] = responseBinding
        this.byOpcode[packetOpcode] = transactionalBinding
        return transactionalBinding
    }

    private fun <M : Packet> createPacketBinding(opcode: Int, packetClass: Class<M>): LanternPacketBinding<M> {
        val privateLookup = lookup.privateLookupIn(packetClass)
        val constructor = privateLookup.findConstructor(packetClass, MethodType.methodType(Void.TYPE))
        val packetConstructor = constructor.createLambda<() -> M>()
        return LanternPacketBinding(opcode, packetClass, packetConstructor)
    }

    override fun <M : Packet> getBinding(packetClass: Class<M>)
            = this.byClass[packetClass].uncheckedCast<PacketBinding<M>?>().optional()

    override fun <M : RequestPacket<R>, R : ResponsePacket> getTransactionalBinding(requestPacket: Class<M>)
            = this.transactionalBindingByRequest[requestPacket].uncheckedCast<TransactionalPacketBinding<M, R>?>().optional()

    private fun findOpcode(packetClass: Class<out Packet>): Int {
        checkBound()
        return this.byClass[packetClass]?.opcode ?: throw IllegalStateException(
                "The packet type ${packetClass.name} isn't registered to this channel.")
    }

    override fun sendTo(connection: ClientConnection, packet: Packet) {
        if (packet is RequestPacket<*>) {
            sendTo(connection, packet)
        } else if (packet is ResponsePacket) {
            throw UnsupportedOperationException("A response packet can only be returned as response to a RequestPacket.")
        } else {
            connection as NetworkSession

            // Get the opcode, this checks if the packet class is valid
            val opcode = findOpcode(packet.javaClass)

            val transactionStore = connection.channel.attr(ChannelTransactionStore.KEY).get()
            val allocator = ByteBufferAllocator.pooled()

            val buf = allocator.buffer()
            buf.writeVarLong(TYPE_NORMAL.toLong() or (opcode.toLong() shl TYPE_BITS))
            // Slice to avoid too visibility of written data and writerIndex
            packet.write(buf.slice())

            // Check if the login phase is still applicable
            if (transactionStore is LoginChannelTransactionStore) {
                val transactionId = transactionStore.nextId()

                // We need to store some kind of transaction object, but it will be
                // ignored when handling a response
                transactionStore.put(transactionId, NoTransaction(this.name))
                connection.sendWithFuture(MessageLoginOutChannelRequest(transactionId, this.name, buf)).addListener { future ->
                    if (!future.isSuccess) {
                        transactionStore.removeData(transactionId)
                    }
                }
            } else {
                connection.send(MessagePlayInOutChannelPayload(this.name, buf))
            }
        }
    }

    private class NoTransaction(channel: String) : ChannelTransaction(channel)

    private class Transaction<P : RequestPacket<R>, R : ResponsePacket>(
            channel: String,
            val request: P,
            val binding: LanternTransactionalPacketBinding<P, R>,
            val completableFuture: CompletableFuture<R>?
    ) : ChannelTransaction(channel)

    override fun <R : ResponsePacket> sendTo(connection: ClientConnection, packet: RequestPacket<R>): CompletableFuture<R> {
        val completableFuture = CompletableFuture<R>()
        sendTo(connection, packet, completableFuture)
        return completableFuture
    }

    private fun <R : ResponsePacket> sendTo(connection: ClientConnection, packet: RequestPacket<R>, completableFuture: CompletableFuture<R>?) {
        connection as NetworkSession

        // Check if the channel is bound
        checkBound()

        val binding = this.transactionalBindingByRequest[packet.javaClass]
                .uncheckedCast<LanternTransactionalPacketBinding<RequestPacket<R>, R>?>()
        check(binding != null) { "The packet type ${packet.javaClass.name} isn't registered to this channel." }

        val transactionStore = connection.channel.attr(ChannelTransactionStore.KEY).get()
        val allocator = ByteBufferAllocator.pooled()
        val opcode = binding.opcode

        val buf = allocator.buffer()
        val transactionId = transactionStore.nextId()

        // Check if the login phase is still applicable
        val message: Message = if (transactionStore is LoginChannelTransactionStore) {
            buf.writeVarLong(TYPE_REQUEST.toLong() or (opcode.toLong() shl TYPE_BITS))
            // Slice to avoid too visibility of written data and writerIndex
            packet.write(buf.slice())

            MessageLoginOutChannelRequest(transactionId, this.name, buf)
        } else {
            buf.writeVarLong(TYPE_REQUEST.toLong() or (transactionId.toLong() shl TYPE_BITS))
            buf.writeVarInt(opcode)
            // Slice to avoid too visibility of written data and writerIndex
            packet.write(buf.slice())

            MessagePlayInOutChannelPayload(this.name, buf)
        }

        transactionStore.put(transactionId, Transaction(this.name, packet, binding, completableFuture))
        connection.sendWithFuture(message).addListener { future ->
            if (!future.isSuccess) {
                if (completableFuture != null) {
                    Lantern.getSyncScheduler().submit {
                        try {
                            completableFuture.completeExceptionally(future.cause())
                        } catch (t: Throwable) {
                            Lantern.getLogger().error("Failed to handle response packet failure", t)
                        }
                    }
                }
                transactionStore.removeData(transactionId)
            }
        }
    }

    override fun <R : ResponsePacket> sendToServer(packet: RequestPacket<R>): CompletableFuture<R> {
        throw UnsupportedOperationException("Sending packets to the server isn't supported.")
    }

    override fun sendToServer(packet: Packet) {
        throw UnsupportedOperationException("Sending packets to the server isn't supported.")
    }

    override fun sendToAll(packet: Packet) {
        Lantern.getServer().rawOnlinePlayers.forEach { player -> sendTo(player, packet) }
    }

    private fun <P : Packet> handleNormalPacket(connection: NetworkSession, opcode: Int, buf: ByteBuffer) {
        val binding = this.byOpcode[opcode].uncheckedCast<LanternPacketBinding<P>?>()
        check(binding != null) { "Unexpected opcode $opcode" }

        val packet = binding.packetConstructor()
        try {
            packet.read(buf.slice())
        } catch (t: Throwable) {
            Lantern.getLogger().error("Failed to read packet", t)
            return
        }

        Lantern.getSyncScheduler().submit {
            binding.handlers.forEach { handler ->
                try {
                    handler.handleMessage(packet, connection, Platform.Type.SERVER)
                } catch (t: Throwable) {
                    Lantern.getLogger().error("Failed to handle packet", t)
                }
            }
        }
    }

    private fun <P : RequestPacket<R>, R : ResponsePacket> handleRequestPacket(
            connection: NetworkSession, opcode: Int, transactionId: Int, buf: ByteBuffer, login: Boolean) {
        val binding = this.byOpcode[opcode].uncheckedCast<LanternTransactionalPacketBinding<P, R>?>()
        check(binding != null) { "Unexpected opcode $opcode" }

        fun sendNoResponse() {
            val response = ByteBufferAllocator.pooled().buffer()
            response.writeVarLong(TYPE_NO_RESPONSE.toLong() or (transactionId.toLong() shl TYPE_BITS))
            response.writeVarInt(opcode)

            if (login) {
                val transactionStore = connection.channel.attr(ChannelTransactionStore.KEY).get()
                connection.send(MessageLoginOutChannelRequest(transactionStore.nextId(), this.name, response))
            } else {
                connection.send(MessagePlayInOutChannelPayload(this.name, response))
            }
        }

        val request = binding.requestBinding.packetConstructor()
        try {
            request.read(buf.slice())
        } catch (t: Throwable) {
            sendNoResponse()
            throw PacketIOException("Failed to read packet", t)
        }

        val requestHandler = binding.requestHandler
        if (requestHandler != null) {
            requestHandler.handleRequest(request, connection, Platform.Type.SERVER, object : RequestPacketResponse<R> {

                override fun fail(exception: ChannelException) {
                    sendNoResponse()
                }

                override fun success(response: R) {
                    val responseBuf = ByteBufferAllocator.pooled().buffer()
                    responseBuf.writeVarLong(TYPE_RESPONSE.toLong() or (transactionId.toLong() shl TYPE_BITS))
                    responseBuf.writeVarInt(opcode)
                    response.write(responseBuf.slice())

                    if (login) {
                        val transactionStore = connection.channel.attr(ChannelTransactionStore.KEY).get()
                        connection.send(MessageLoginOutChannelRequest(transactionStore.nextId(), name, responseBuf))
                    } else {
                        connection.send(MessagePlayInOutChannelPayload(name, responseBuf))
                    }
                }
            })
        } else {
            sendNoResponse()
        }
    }

    private fun <P : RequestPacket<R>, R : ResponsePacket> handleResponsePacket(
            connection: NetworkSession, transactionId: Int, buf: ByteBuffer?) {
        val transactionStore = connection.channel.attr(ChannelTransactionStore.KEY).get()
        var transaction = transactionStore.getData(transactionId)

        if (transaction != null) {
            if (transaction is NoTransaction) {
                // Just remove the entry, responses like this can be send during the login phase
                transactionStore.removeData(transactionId)
            } else {
                transaction = transaction.uncheckedCast<Transaction<P, R>>()

                val binding = transaction.binding
                val completableFuture = transaction.completableFuture

                if (buf == null) {
                    val exception = NoResponseException()
                    Lantern.getSyncScheduler().submit {
                        binding.responseHandlers.forEach { handler ->
                            try {
                                handler.handleFailure(transaction.request, connection, Platform.Type.SERVER, exception)
                            } catch (t: Throwable) {
                                Lantern.getLogger().error("Failed to handle response packet failure", t)
                            }
                        }
                        try {
                            completableFuture?.completeExceptionally(exception)
                        } catch (t: Throwable) {
                            Lantern.getLogger().error("Failed to handle response packet failure", t)
                        }
                        transactionStore.removeData(transactionId)
                    }
                } else {
                    val responseBinding = binding.responseBinding
                    val response = responseBinding.packetConstructor()

                    try {
                        response.read(buf.slice())
                    } catch (t: Throwable) {
                        Lantern.getLogger().error("Failed to read response packet", t)
                        if (completableFuture != null) {
                            Lantern.getSyncScheduler().submit {
                                completableFuture.completeExceptionally(PacketIOException(t))
                            }
                        }
                        return
                    }

                    Lantern.getSyncScheduler().submit {
                        binding.responseHandlers.forEach { handler ->
                            try {
                                handler.handleResponse(response, transaction.request, connection, Platform.Type.SERVER)
                            } catch (t: Throwable) {
                                Lantern.getLogger().error("Failed to handle response packet", t)
                            }
                        }
                        try {
                            completableFuture?.complete(response)
                        } catch (t: Throwable) {
                            Lantern.getLogger().error("Failed to handle response packet", t)
                        }
                        transactionStore.removeData(transactionId)
                    }
                }
            }
        }
    }

    override fun handlePayload(buf: ByteBuffer, connection: NetworkSession) {
        val longValue = buf.readVarLong()
        // Extract the type, this are the 2 least significant bits
        val type = (longValue and TYPE_MASK).toInt()
        // Get the actual value
        val value = (longValue ushr TYPE_BITS).toInt()

        // Now depending on the type, the value means something different
        when (type) {
            TYPE_NORMAL -> handleNormalPacket<Packet>(connection, value, buf)
            TYPE_REQUEST -> {
                val opcode = buf.readVarInt()
                handleRequestPacket<RequestPacket<ResponsePacket>, ResponsePacket>(connection, opcode, value, buf, false)
            }
            TYPE_RESPONSE -> handleResponsePacket<RequestPacket<ResponsePacket>, ResponsePacket>(connection, value, buf)
            TYPE_NO_RESPONSE -> handleResponsePacket<RequestPacket<ResponsePacket>, ResponsePacket>(connection, value, null)
        }

        buf.release()
    }

    override fun handleLoginPayload(buf: ByteBuffer?, transactionId: Int, connection: NetworkSession) {
        if (buf == null) {
            handleResponsePacket<RequestPacket<ResponsePacket>, ResponsePacket>(connection, transactionId, null)
        } else {
            val longValue = buf.readVarLong()
            // Extract the type, this are the 2 least significant bits
            val type = (longValue and TYPE_MASK).toInt()
            // Get the actual value
            val value = (longValue ushr TYPE_BITS).toInt()

            // Now depending on the type, the value means something different
            when (type) {
                TYPE_NORMAL -> handleNormalPacket<Packet>(connection, value, buf)
                TYPE_REQUEST -> {
                    val opcode = buf.readVarInt()
                    handleRequestPacket<RequestPacket<ResponsePacket>, ResponsePacket>(connection, opcode, value, buf, true)
                }
                TYPE_RESPONSE -> handleResponsePacket<RequestPacket<ResponsePacket>, ResponsePacket>(connection, transactionId, buf)
                TYPE_NO_RESPONSE -> handleResponsePacket<RequestPacket<ResponsePacket>, ResponsePacket>(connection, transactionId, null)
            }
        }
    }

    companion object {

        val lookup: MethodHandles.Lookup = MethodHandles.lookup()

        /**
         * Represents a normal packet type.
         */
        const val TYPE_NORMAL = 0

        /**
         * Represents a request packet type, see [RequestPacket].
         */
        const val TYPE_REQUEST = 1

        /**
         * Represents a response packet type, see [ResponsePacket].
         */
        const val TYPE_RESPONSE = 2

        /**
         * Represents a no response packet type.
         */
        const val TYPE_NO_RESPONSE = 3

        const val TYPE_BITS = 3 // 3 bits are reserved for types
        const val TYPE_MASK = ((1 shl TYPE_BITS) - 1).toLong()
    }
}
