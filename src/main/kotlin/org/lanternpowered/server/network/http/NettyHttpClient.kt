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
 * Copyright (c) 2018 Velocity team
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
package org.lanternpowered.server.network.http

import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.handler.codec.http.DefaultFullHttpRequest
import io.netty.handler.codec.http.HttpClientCodec
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpVersion
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.SslHandler
import io.netty.resolver.dns.DnsAddressResolverGroup
import io.netty.resolver.dns.DnsNameResolverBuilder
import org.lanternpowered.server.network.TransportType
import org.lanternpowered.server.util.netty.addChannelFutureListener
import java.net.InetSocketAddress
import java.net.URL
import java.util.concurrent.CompletableFuture
import kotlin.time.DurationUnit
import kotlin.time.seconds

class NettyHttpClient(
        private val userAgent: String,
        private val defaultLoop: EventLoopGroup
) {

    private val transportType = TransportType.findBestType()
    private val resolverGroup = DnsAddressResolverGroup(
            DnsNameResolverBuilder()
                    .channelFactory(this.transportType.datagramChannelFactory)
                    .negativeTtl(15)
                    .ndots(1))

    private fun establishConnection(url: URL, group: EventLoopGroup): ChannelFuture {
        val host = url.host
        var port = url.port
        val ssl = url.protocol == "https"
        if (port == -1)
            port = if (ssl) 443 else 80
        val address = InetSocketAddress.createUnresolved(host, port)
        return Bootstrap()
                .channelFactory(this.transportType.socketChannelFactory)
                .group(group)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5.seconds.toInt(DurationUnit.MILLISECONDS))
                .resolver(this.resolverGroup)
                .handler(object : ChannelInitializer<Channel>() {
                    override fun initChannel(ch: Channel) {
                        if (ssl) {
                            val context = SslContextBuilder.forClient().protocols("TLSv1.2").build()
                            // Unbelievably, Java doesn't automatically check the CN to make sure we're talking
                            // to the right host! Therefore, we provide the intended host name and port, along
                            // with asking Java very nicely if it could check the hostname in the certificate
                            // for us.
                            val engine = context.newEngine(ch.alloc(), address.hostString, address.port)
                            engine.sslParameters.endpointIdentificationAlgorithm = "HTTPS"
                            ch.pipeline().addLast("ssl", SslHandler(engine))
                        }
                        ch.pipeline().addLast("http", HttpClientCodec())
                    }
                })
                .connect(address)
    }

    /**
     * Attempts an HTTP GET request to the specified URL.
     *
     * @param url The URL to fetch
     * @return A future representing the response
     */
    fun get(url: String): CompletableFuture<SimpleHttpResponse> =
            this.get(URL(url), this.defaultLoop)

    /**
     * Attempts an HTTP GET request to the specified URL.
     *
     * @param url The URL to fetch
     * @return A future representing the response
     */
    fun get(url: String, loop: EventLoopGroup): CompletableFuture<SimpleHttpResponse> =
            this.get(URL(url), loop)

    /**
     * Attempts an HTTP GET request to the specified URL.
     *
     * @param url The URL to fetch
     * @return A future representing the response
     */
    fun get(url: URL): CompletableFuture<SimpleHttpResponse> =
            this.get(url, this.defaultLoop)

    /**
     * Attempts an HTTP GET request to the specified URL.
     *
     * @param url The URL to fetch
     * @param loop The event loop group to use
     * @return A future representing the response
     */
    fun get(url: URL, loop: EventLoopGroup): CompletableFuture<SimpleHttpResponse> {
        val reply = CompletableFuture<SimpleHttpResponse>()
        this.establishConnection(url, loop)
                .addChannelFutureListener { future ->
                    if (future.isSuccess) {
                        val channel = future.channel()
                        channel.pipeline().addLast("collector", SimpleHttpResponseCollector(reply))
                        var pathAndQuery = url.path
                        if (url.query != null && url.query.isNotEmpty())
                            pathAndQuery += "?" + url.query
                        val request = DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, pathAndQuery)
                        request.headers().add(HttpHeaderNames.HOST, url.host)
                        request.headers().add(HttpHeaderNames.USER_AGENT, userAgent)
                        channel.writeAndFlush(request, channel.voidPromise())
                    } else {
                        reply.completeExceptionally(future.cause())
                    }
                }
        return reply
    }

    /**
     * Attempts an HTTP POST request to the specified URL.
     *
     * @param url The URL to fetch
     * @param body The body to post
     * @param headers Extra header values
     * @return A future representing the response
     */
    fun post(url: String, body: String, headers: Map<out CharSequence, Any>): CompletableFuture<SimpleHttpResponse> =
            this.post(URL(url), this.defaultLoop, Unpooled.wrappedBuffer(body.toByteArray(Charsets.UTF_8)), headers)

    /**
     * Attempts an HTTP POST request to the specified URL.
     *
     * @param url The URL to fetch
     * @param body The body to post
     * @param headers Extra header values
     * @return A future representing the response
     */
    fun post(url: String, body: ByteBuf, headers: Map<out CharSequence, Any>): CompletableFuture<SimpleHttpResponse> =
            this.post(URL(url), this.defaultLoop, body, headers)

    /**
     * Attempts an HTTP POST request to the specified URL.
     *
     * @param url The URL to fetch
     * @param body The body to post
     * @param headers Extra header values
     * @return A future representing the response
     */
    fun post(url: URL, body: String, headers: Map<out CharSequence, Any>): CompletableFuture<SimpleHttpResponse> =
            this.post(url, this.defaultLoop, Unpooled.wrappedBuffer(body.toByteArray(Charsets.UTF_8)), headers)

    /**
     * Attempts an HTTP POST request to the specified URL.
     *
     * @param url The URL to fetch
     * @param body The body to post
     * @param headers Extra header values
     * @return A future representing the response
     */
    fun post(url: URL, body: ByteBuf, headers: Map<out CharSequence, Any>): CompletableFuture<SimpleHttpResponse> =
            this.post(url, this.defaultLoop, body, headers)

    /**
     * Attempts an HTTP POST request to the specified URL.
     *
     * @param url The URL to fetch
     * @param loop The event loop to use
     * @param body The body to post
     * @param headers Extra header values
     * @return A future representing the response
     */
    fun post(url: String, loop: EventLoopGroup, body: String, headers: Map<out CharSequence, Any>): CompletableFuture<SimpleHttpResponse> =
            this.post(URL(url), loop, Unpooled.wrappedBuffer(body.toByteArray(Charsets.UTF_8)), headers)

    /**
     * Attempts an HTTP POST request to the specified URL.
     *
     * @param url The URL to fetch
     * @param loop The event loop to use
     * @param body The body to post
     * @param headers Extra header values
     * @return A future representing the response
     */
    fun post(url: String, loop: EventLoopGroup, body: ByteBuf, headers: Map<out CharSequence, Any>): CompletableFuture<SimpleHttpResponse> =
            this.post(URL(url), loop, body, headers)

    /**
     * Attempts an HTTP POST request to the specified URL.
     *
     * @param url The URL to fetch
     * @param loop The event loop to use
     * @param body The body to post
     * @param headers Extra header values
     * @return A future representing the response
     */
    fun post(url: URL, loop: EventLoopGroup, body: String, headers: Map<out CharSequence, Any>): CompletableFuture<SimpleHttpResponse> =
            this.post(url, loop, Unpooled.wrappedBuffer(body.toByteArray(Charsets.UTF_8)), headers)

    /**
     * Attempts an HTTP POST request to the specified URL.
     *
     * @param url The URL to fetch
     * @param loop The event loop to use
     * @param body The body to post
     * @param headers Extra header values
     * @return A future representing the response
     */
    fun post(url: URL, loop: EventLoopGroup, body: ByteBuf, headers: Map<out CharSequence, Any>): CompletableFuture<SimpleHttpResponse> {
        val reply = CompletableFuture<SimpleHttpResponse>()
        this.establishConnection(url, loop)
                .addChannelFutureListener { future: ChannelFuture ->
                    if (future.isSuccess) {
                        val channel = future.channel()
                        channel.pipeline().addLast("collector", SimpleHttpResponseCollector(reply))
                        var pathAndQuery = url.path
                        if (url.query != null && url.query.isNotEmpty())
                            pathAndQuery += "?" + url.query
                        val request = DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
                                HttpMethod.POST, pathAndQuery, body)
                        request.headers().add(HttpHeaderNames.HOST, url.host)
                        request.headers().add(HttpHeaderNames.USER_AGENT, userAgent)
                        request.headers().add(HttpHeaderNames.CONTENT_LENGTH, body.readableBytes())
                        for ((name, value) in headers)
                            request.headers().add(name, value)
                        channel.writeAndFlush(request, channel.voidPromise())
                    } else {
                        body.release()
                        reply.completeExceptionally(future.cause())
                    }
                }
        return reply
    }
}
