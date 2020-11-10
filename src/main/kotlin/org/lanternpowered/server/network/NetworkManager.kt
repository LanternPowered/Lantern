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
package org.lanternpowered.server.network

import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.PooledByteBufAllocator
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.handler.timeout.ReadTimeoutHandler
import org.lanternpowered.api.util.collections.concurrentHashSetOf
import org.lanternpowered.server.LanternServer
import org.lanternpowered.server.network.buffer.LanternByteBufferAllocator
import org.lanternpowered.server.network.packet.SimpleCodecContext
import org.lanternpowered.server.network.pipeline.LegacyProtocolHandler
import org.lanternpowered.server.network.pipeline.NettyPacketDecoder
import org.lanternpowered.server.network.pipeline.NettyPacketEncoder
import org.lanternpowered.server.network.pipeline.NoopHandler
import org.lanternpowered.server.network.pipeline.OutboundPacketProcessorHandler
import org.lanternpowered.server.network.pipeline.PacketFramingHandler
import org.lanternpowered.server.network.protocol.ProtocolState
import org.lanternpowered.server.util.netty.addChannelFutureListener
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class NetworkManager(
        private val server: LanternServer,
        private val bossGroup: EventLoopGroup,
        private val workerGroup: EventLoopGroup
) {

    init {
        ProtocolState
    }

    private val sessions = concurrentHashSetOf<NetworkSession>()
    private lateinit var bootstrap: ServerBootstrap
    private var endpoint: Channel? = null

    lateinit var address: InetSocketAddress
        private set

    fun init(address: SocketAddress): CompletableFuture<Throwable?> {
        val transportType = TransportType.findBestType()

        this.address = address as InetSocketAddress
        this.bootstrap = ServerBootstrap()
                .group(this.bossGroup, this.workerGroup)
                .channelFactory(transportType.serverSocketChannelFactory)
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    public override fun initChannel(channel: SocketChannel) {
                        val pipeline = channel.pipeline()
                        val networkSession = NetworkSession(this@NetworkManager, server, channel)
                        val codecContext = SimpleCodecContext(
                                LanternByteBufferAllocator(channel.alloc()), channel, networkSession)
                        pipeline.addLast(ReadTimeoutHandler(NetworkSession.READ_TIMEOUT.toLongMilliseconds(), TimeUnit.MILLISECONDS))
                                .addLast(NetworkSession.LEGACY_PING, LegacyProtocolHandler(networkSession))
                                .addLast(NetworkSession.ENCRYPTION, NoopHandler)
                                .addLast(NetworkSession.FRAMING, PacketFramingHandler())
                                .addLast(NetworkSession.COMPRESSION, NoopHandler)
                                .addLast(NetworkSession.PACKET_DECODER, NettyPacketDecoder(codecContext))
                                .addLast(NetworkSession.PACKET_ENCODER, NettyPacketEncoder(codecContext))
                                .addLast(NetworkSession.PROCESSOR, OutboundPacketProcessorHandler(codecContext))
                                .addLast(NetworkSession.HANDLER, networkSession)
                    }
                })
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)

        val result = CompletableFuture<Throwable?>()
        this.bootstrap.bind(address)
                .addChannelFutureListener { future ->
                    if (future.isSuccess) {
                        result.complete(null)
                    } else {
                        result.complete(future.cause())
                    }
                }
        return result
    }

    fun add(session: NetworkSession) {
        this.sessions += session
    }

    fun remove(session: NetworkSession) {
        this.sessions -= session
    }

    fun update() {
        for (session in this.sessions)
            session.update()
    }

    fun closeEndpoint() {
        check(this::bootstrap.isInitialized) { "The network manager wasn't initialized." }
        // Don't allow any new connections
        this.endpoint?.close()
    }
}
