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
package org.lanternpowered.server.network.query

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.EventLoopGroup
import org.lanternpowered.server.LanternGame
import org.lanternpowered.server.LanternServerNew
import org.lanternpowered.server.network.TransportType
import org.lanternpowered.server.util.ThreadHelper
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * Implementation of a server for the minecraft server query protocol.
 * @see [Protocol Specifications](http://wiki.vg/Query)
 */
class QueryServer(
        val server: LanternServerNew,
        private val showPlugins: Boolean
) {

    private lateinit var group: EventLoopGroup
    private lateinit var bootstrap: Bootstrap
    private lateinit var flushTask: ScheduledFuture<*>

    private val challengeTokens = ConcurrentHashMap<InetSocketAddress, Int>()

    /**
     * Initializes the query server.
     */
    fun init(address: SocketAddress): ChannelFuture {
        val transportType = TransportType.findBestType()

        this.group = transportType.eventLoopGroupSupplier(0, ThreadHelper.newThreadFactory())
        this.bootstrap = Bootstrap()
                .group(this.group)
                .channelFactory(transportType.datagramChannelFactory)
                .handler(QueryHandler(this, this.showPlugins))

        this.flushTask = this.group.scheduleAtFixedRate({
            flushChallengeTokens()
        }, 30, 30, TimeUnit.MINUTES)

        return this.bootstrap.bind(address)
    }

    /**
     * Shuts the query server down.
     */
    fun shutdown() {
        check(this::bootstrap.isInitialized) { "The query server wasn't initialized." }
        this.group.shutdownGracefully()
        this.flushTask.cancel(false)
    }

    /**
     * Generate a new token.
     *
     * @param address The sender address
     * @return The generated challenge token
     */
    fun generateChallengeToken(address: InetSocketAddress): Int {
        val token = Random.nextInt()
        this.challengeTokens[address] = token
        return token
    }

    /**
     * Verify that the request is using the correct challenge token.
     *
     * @param address The sender address
     * @param token The token
     * @return Whether the token is valid
     */
    fun verifyChallengeToken(address: InetSocketAddress, token: Int): Boolean =
            this.challengeTokens[address] == token

    /**
     * Invalidates all challenge tokens.
     */
    private fun flushChallengeTokens() {
        this.challengeTokens.clear()
    }
}
