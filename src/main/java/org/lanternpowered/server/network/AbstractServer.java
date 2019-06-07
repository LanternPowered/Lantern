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

import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueDatagramChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.lanternpowered.server.game.Lantern;

import java.net.SocketAddress;
import java.util.concurrent.ThreadFactory;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("unchecked")
public abstract class AbstractServer {

    /**
     * Whether the debug message of the epoll/kqueue
     * availability is already logged.
     */
    private static boolean epollAvailabilityLogged = false;
    private static boolean kqueueAvailabilityLogged = false;

    /**
     * Whether the server is initialized.
     */
    private boolean initialized = false;

    /**
     * Initializes the network server.
     *
     * @param address The address to bind the server to
     * @return The channel future
     */
    public final ChannelFuture init(SocketAddress address) {
        return init(address, true, true);
    }

    /**
     * Initializes the network server.
     *
     * @param address The address to bind the server to
     * @param useEpollWhenAvailable Whether you want to use epoll if it's available
     * @return The channel future
     */
    public final ChannelFuture init(SocketAddress address,
            boolean useEpollWhenAvailable,
            boolean useKQueueWhenAvailable) {
        if (this.initialized) {
            throw new IllegalStateException("The network server can only be initialized once.");
        }
        TransportType channelType = null;
        if (useEpollWhenAvailable) {
            final boolean epoll = Epoll.isAvailable();
            if (!epollAvailabilityLogged) {
                if (epoll) {
                    Lantern.getLogger().info("Epoll is enabled.");
                } else {
                    // Debug the reason why it is unavailable
                    Lantern.getLogger().warn("Epoll is unavailable: {}", getMessage(Epoll.unavailabilityCause()));
                }
                epollAvailabilityLogged = true;
            }
            if (epoll) {
                channelType = TransportType.EPOLL;
            }
        }
        if (channelType == null && useKQueueWhenAvailable) {
            final boolean kqueue = KQueue.isAvailable();
            if (!kqueueAvailabilityLogged) {
                if (kqueue) {
                    Lantern.getLogger().info("KQueue is enabled.");
                } else {
                    // Debug the reason why it is unavailable
                    Lantern.getLogger().warn("KQueue is unavailable: {}", getMessage(KQueue.unavailabilityCause()));
                }
                kqueueAvailabilityLogged = true;
            }
            if (kqueue) {
                channelType = TransportType.KQUEUE;
            }
        }
        if (channelType == null) {
            channelType = TransportType.NIO;
        }
        final ChannelFuture future = init(address, channelType);
        this.initialized = true;
        return future;
    }

    private static String getMessage(@Nullable Throwable t) {
        if (t == null) {
            return "unknown";
        }
        if (t.getMessage() == null) {
            return getMessage(t.getCause());
        }
        return t.getMessage();
    }

    protected abstract ChannelFuture init(SocketAddress address, TransportType type);

    public final void shutdown() {
        if (this.initialized) {
            shutdown0();
            this.initialized = false;
        }
    }

    protected abstract void shutdown0();

    /**
     * Creates a {@link EventLoopGroup} for the given {@link TransportType}.
     *
     * @param type The channel type for which a {@link EventLoopGroup} should be generated
     * @param threadFactory The thread factory
     * @return The event loop group
     */
    protected static EventLoopGroup createEventLoopGroup(TransportType type, ThreadFactory threadFactory) {
        switch (type) {
            case NIO:
                return new NioEventLoopGroup(0, threadFactory);
            case EPOLL:
                return new EpollEventLoopGroup(0, threadFactory);
            case KQUEUE:
                return new KQueueEventLoopGroup(0, threadFactory);
        }
        throw new UnsupportedOperationException("Unsupported channel type: " + type);
    }

    /**
     * Creates a {@link EventLoopGroup} for the given {@link TransportType}.
     *
     * @param type The channel type for which a {@link EventLoopGroup} should be generated
     * @return The event loop group
     */
    protected static EventLoopGroup createEventLoopGroup(TransportType type) {
        switch (type) {
            case NIO:
                return new NioEventLoopGroup();
            case EPOLL:
                return new EpollEventLoopGroup();
            case KQUEUE:
                return new KQueueEventLoopGroup();
        }
        throw new UnsupportedOperationException("Unsupported channel type: " + type);
    }

    protected static <C extends SocketChannel & ServerChannel> Class<C> getServerSocketChannelClass(TransportType type) {
        switch (type) {
            case NIO:
                return (Class) NioServerSocketChannel.class;
            case EPOLL:
                return (Class) EpollServerSocketChannel.class;
            case KQUEUE:
                return (Class) KQueueServerSocketChannel.class;
        }
        throw new UnsupportedOperationException("Unsupported channel type: " + type);
    }

    protected static <C extends SocketChannel> Class<C> getSocketChannelClass(TransportType type) {
        switch (type) {
            case NIO:
                return (Class) NioSocketChannel.class;
            case EPOLL:
                return (Class) EpollSocketChannel.class;
            case KQUEUE:
                return (Class) KQueueSocketChannel.class;
        }
        throw new UnsupportedOperationException("Unsupported channel type: " + type);
    }

    protected static <C extends DatagramChannel> Class<C> getDatagramChannelClass(TransportType type) {
        switch (type) {
            case NIO:
                return (Class) NioDatagramChannel.class;
            case EPOLL:
                return (Class) EpollDatagramChannel.class;
            case KQUEUE:
                return (Class) KQueueDatagramChannel.class;
        }
        throw new UnsupportedOperationException("Unsupported channel type: " + type);
    }

    protected enum TransportType {
        EPOLL,
        KQUEUE,
        NIO,
    }
}
