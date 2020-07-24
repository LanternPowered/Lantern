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
/*
 * Copyright (c) 2011-2014 Glowstone - Tad Hardesty
 * Copyright (c) 2010-2011 Lightstone - Graham Edgecombe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.rcon

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.socket.SocketChannel
import org.lanternpowered.api.util.collections.concurrentHashMapOf
import org.lanternpowered.server.network.TransportType
import org.lanternpowered.server.util.ThreadHelper
import org.lanternpowered.server.util.executor.LanternExecutorService
import org.spongepowered.api.service.rcon.RconService
import java.io.Closeable
import java.net.InetSocketAddress
import java.net.SocketAddress

class RconServer(
        private val password: String,
        val syncExecutor: LanternExecutorService
) : RconService, Closeable {

    private val connectionByHostname = concurrentHashMapOf<String, LanternRconConnection>()

    private lateinit var bootstrap: ServerBootstrap
    private lateinit var bossGroup: EventLoopGroup
    private lateinit var workerGroup: EventLoopGroup

    lateinit var address: InetSocketAddress
        private set

    fun init(address: SocketAddress): ChannelFuture {
        val transportType = TransportType.findBestType()
        val threadFactory = ThreadHelper.newThreadFactory()

        this.address = address as InetSocketAddress
        this.bootstrap = ServerBootstrap()
        this.bossGroup = transportType.eventLoopGroupSupplier( 0, threadFactory)
        this.workerGroup = transportType.eventLoopGroupSupplier( 0, threadFactory)

        this.bootstrap.apply {
            group(bossGroup, workerGroup)
            channelFactory(transportType.serverSocketChannelFactory)
            childHandler(object : ChannelInitializer<SocketChannel>() {
                public override fun initChannel(ch: SocketChannel) {
                    ch.pipeline()
                            .addLast(RconFramingHandler())
                            .addLast(RconHandler(this@RconServer, password))
                }
            })
        }

        return this.bootstrap.bind(address)
    }

    override fun close() {
        check(this::bootstrap.isInitialized) { "The rcon server wasn't initialized." }
        this.workerGroup.shutdownGracefully()
        this.bossGroup.shutdownGracefully()
    }

    fun add(connection: LanternRconConnection) {
        this.connectionByHostname[connection.address.hostName] = connection
    }

    fun remove(connection: LanternRconConnection) {
        this.connectionByHostname.remove(connection.address.hostName)
    }

    fun getByHostName(hostname: String): LanternRconConnection? = this.connectionByHostname[hostname]

    override fun isRconEnabled(): Boolean = true
    override fun getRconPassword(): String = this.password
}
