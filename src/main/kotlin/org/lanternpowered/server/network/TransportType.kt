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

import io.netty.channel.ChannelFactory
import io.netty.channel.EventLoopGroup
import io.netty.channel.epoll.Epoll as NettyEpoll
import io.netty.channel.epoll.EpollDatagramChannel
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.channel.epoll.EpollSocketChannel
import io.netty.channel.kqueue.KQueue as NettyKQueue
import io.netty.channel.kqueue.KQueueDatagramChannel
import io.netty.channel.kqueue.KQueueEventLoopGroup
import io.netty.channel.kqueue.KQueueServerSocketChannel
import io.netty.channel.kqueue.KQueueSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramChannel
import io.netty.channel.socket.ServerSocketChannel
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioDatagramChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import org.lanternpowered.server.game.Lantern
import java.util.concurrent.ThreadFactory

internal sealed class TransportType(
        serverSocketChannelSupplier: () -> ServerSocketChannel,
        socketChannelSupplier: () -> SocketChannel,
        datagramChannelSupplier: () -> DatagramChannel,
        val eventLoopGroupSupplier: (threads: Int, threadFactory: ThreadFactory) -> EventLoopGroup
) {

    val serverSocketChannelFactory = ChannelFactory(serverSocketChannelSupplier)
    val socketChannelFactory = ChannelFactory(socketChannelSupplier)
    val datagramChannelFactory = ChannelFactory(datagramChannelSupplier)

    object Nio : TransportType(
            ::NioServerSocketChannel, ::NioSocketChannel, ::NioDatagramChannel, ::NioEventLoopGroup)

    object KQueue : TransportType(
            ::KQueueServerSocketChannel, ::KQueueSocketChannel, ::KQueueDatagramChannel, ::KQueueEventLoopGroup)

    object Epoll : TransportType(
            ::EpollServerSocketChannel, ::EpollSocketChannel, ::EpollDatagramChannel, ::EpollEventLoopGroup)

    companion object {

        private var logged: Boolean = false

        /**
         * Searches for the best transport type.
         */
        fun findBestType(): TransportType {
            try {
                if (System.getProperty("lantern.disable-native-transport")?.toLowerCase() != "true") {
                    if (NettyKQueue.isAvailable()) {
                        if (!this.logged)
                            Lantern.getLogger().info("KQueue is available.")
                        return KQueue
                    } else if (!this.logged)
                        Lantern.getLogger().info("KQueue is unavailable: {}",
                                this.getMessage(NettyKQueue.unavailabilityCause()))
                    if (NettyEpoll.isAvailable()) {
                        if (!this.logged)
                            Lantern.getLogger().info("Epoll is available.")
                        return Epoll
                    } else if (!this.logged)
                        Lantern.getLogger().info("Epoll is unavailable: {}",
                                this.getMessage(NettyEpoll.unavailabilityCause()))
                }
                return Nio
            } finally {
                this.logged = true
            }
        }

        private fun getMessage(t: Throwable?): String {
            if (t == null)
                return "unknown"
            return if (t.message == null) getMessage(t.cause) else t.message!!
        }
    }
}
