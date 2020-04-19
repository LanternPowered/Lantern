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

import org.checkerframework.checker.nullness.qual.Nullable;

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
    protected ChannelFuture init(SocketAddress address, TransportType transportType) {
        this.bootstrap = new ServerBootstrap();
        // Take advantage of the fast thread local threads,
        // this is also provided by the default thread factory
        final ThreadFactory threadFactory = ThreadHelper.newThreadFactory(() -> "netty-" + threadCounter.getAndIncrement());
        this.bossGroup = createEventLoopGroup(transportType, threadFactory);
        this.workerGroup = createEventLoopGroup(transportType, threadFactory);
        this.socketAddress = address;
        return this.bootstrap
                .group(this.bossGroup, this.workerGroup)
                .channel(getServerSocketChannelClass(transportType))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
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
