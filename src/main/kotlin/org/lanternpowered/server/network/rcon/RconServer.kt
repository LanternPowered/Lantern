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
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.socket.SocketChannel
import org.lanternpowered.api.util.collections.concurrentHashMapOf
import org.lanternpowered.server.network.TransportType
import org.lanternpowered.server.util.executor.LanternExecutorService
import org.lanternpowered.server.util.netty.addChannelFutureListener
import org.spongepowered.api.service.rcon.RconService
import java.io.Closeable
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.util.concurrent.CompletableFuture

class RconServer(
        private val password: String,
        val syncExecutor: LanternExecutorService,
        private val bossGroup: EventLoopGroup,
        private val workerGroup: EventLoopGroup
) : RconService, Closeable {

    private val connectionByHostname = concurrentHashMapOf<String, LanternRconConnection>()
    private lateinit var bootstrap: ServerBootstrap
    private var endpoint: Channel? = null

    lateinit var address: InetSocketAddress
        private set

    fun init(address: SocketAddress): CompletableFuture<Boolean> {
        val transportType = TransportType.findBestType()

        this.address = address as InetSocketAddress
        this.bootstrap = ServerBootstrap()
                .group(this.bossGroup, this.workerGroup)
                .channelFactory(transportType.serverSocketChannelFactory)
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    public override fun initChannel(ch: SocketChannel) {
                        ch.pipeline()
                                .addLast(RconFramingHandler())
                                .addLast(RconHandler(this@RconServer, password))
                    }
                })

        val result = CompletableFuture<Boolean>()
        this.bootstrap.bind(address)
                .addChannelFutureListener { future ->
                    if (future.isSuccess) {
                        this.endpoint = future.channel()
                        result.complete(true)
                    } else {
                        result.complete(false)
                    }
                }
        return result
    }

    override fun close() {
        check(this::bootstrap.isInitialized) { "The rcon server wasn't initialized." }
        // Don't allow any new connections
        this.endpoint?.close()
        // Close all open connections
        for (connection in this.connectionByHostname.values)
            connection.close()
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
