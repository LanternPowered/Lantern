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
package org.lanternpowered.server.network;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.lanternpowered.server.LanternServer;
import org.lanternpowered.server.network.buffer.LanternByteBufferAllocator;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.codec.SimpleCodecContext;
import org.lanternpowered.server.network.pipeline.LegacyProtocolHandler;
import org.lanternpowered.server.network.pipeline.MessageCodecHandler;
import org.lanternpowered.server.network.pipeline.MessageFramingHandler;
import org.lanternpowered.server.network.pipeline.MessageProcessorHandler;
import org.lanternpowered.server.network.pipeline.NoopHandler;
import org.lanternpowered.server.util.ThreadHelper;

import java.net.SocketAddress;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

@Singleton
public final class NetworkManager extends AbstractServer {

    private final static AtomicInteger threadCounter = new AtomicInteger(0);

    private ServerBootstrap bootstrap;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private final Set<NetworkSession> sessions = Sets.newConcurrentHashSet();
    private final LanternServer server;

    @Nullable private SocketAddress socketAddress;

    @Inject
    public NetworkManager(LanternServer server) {
        this.server = server;
    }

    /**
     * Gets the {@link SocketAddress} if present.
     * 
     * @return The socket address
     */
    public Optional<SocketAddress> getAddress() {
        return Optional.ofNullable(this.socketAddress);
    }

    /**
     * Gets the {@link LanternServer}.
     * 
     * @return The server
     */
    public LanternServer getServer() {
        return this.server;
    }

    /**
     * Pulses all the sessions.
     */
    public void pulseSessions() {
        this.sessions.forEach(NetworkSession::pulse);
    }

    /**
     * Called when the {@link NetworkSession} becomes active.
     *
     * @param session The session
     */
    void onActive(NetworkSession session) {
        this.sessions.add(session);
    }

    /**
     * Called when the {@link NetworkSession} becomes inactive.
     *
     * @param session The session
     */
    void onInactive(NetworkSession session) {
        this.sessions.remove(session);
    }

    @Override
    protected ChannelFuture init(SocketAddress address, TransportType channelType) {
        this.bootstrap = new ServerBootstrap();
        // Take advantage of the fast thread local threads,
        // this is also provided by the default thread factory
        final ThreadFactory threadFactory = ThreadHelper.newThreadFactory(() -> "netty-" + threadCounter.getAndIncrement());
        this.bossGroup = createEventLoopGroup(channelType, threadFactory);
        this.workerGroup = createEventLoopGroup(channelType, threadFactory);
        this.socketAddress = address;
        return this.bootstrap
                .group(this.bossGroup, this.workerGroup)
                .channel(getServerSocketChannelClass(channelType))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        final ChannelPipeline pipeline = ch.pipeline();
                        final NetworkSession networkSession = new NetworkSession(ch, server, NetworkManager.this);
                        final CodecContext codecContext = new SimpleCodecContext(new LanternByteBufferAllocator(ch.alloc()), ch, networkSession);
                        pipeline.addLast(new ReadTimeoutHandler(NetworkSession.READ_TIMEOUT_SECONDS))
                                .addLast(NetworkSession.LEGACY_PING, new LegacyProtocolHandler(networkSession))
                                .addLast(NetworkSession.ENCRYPTION, NoopHandler.INSTANCE)
                                .addLast(NetworkSession.FRAMING, new MessageFramingHandler())
                                .addLast(NetworkSession.COMPRESSION, NoopHandler.INSTANCE)
                                .addLast(NetworkSession.CODECS, new MessageCodecHandler(codecContext))
                                .addLast(NetworkSession.PROCESSOR, new MessageProcessorHandler(codecContext))
                                .addLast(NetworkSession.HANDLER, networkSession);
                    }
                })
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .bind(address);
    }

    @Override
    public void shutdown0() {
        this.workerGroup.shutdownGracefully();
        this.bossGroup.shutdownGracefully();
        this.bootstrap = null;
    }
}
