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
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.lanternpowered.server.game.Lantern;

import java.net.SocketAddress;
import java.util.concurrent.ThreadFactory;

public abstract class ServerBase {

    /**
     * Whether the debug message of the epoll
     * availability is already logged.
     */
    private static boolean epollAvailabilityLogged = false;

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
        return this.init(address, false);
    }

    /**
     * Initializes the network server.
     *
     * @param address The address to bind the server to
     * @param useEpollWhenAvailable Whether you want to use epoll if it's available
     * @return The channel future
     */
    public final ChannelFuture init(SocketAddress address, boolean useEpollWhenAvailable) {
        if (this.initialized) {
            throw new IllegalStateException("The network server can only be initialized once.");
        }
        boolean epoll = false;
        if (epollAvailabilityLogged) {
            epoll = Epoll.isAvailable() && useEpollWhenAvailable;
        } else if (useEpollWhenAvailable) {
            if (Epoll.isAvailable()) {
                epoll = true;
                Lantern.getLogger().info("Epoll is enabled.");
            } else {
                // Debug the reason why it is unavailable
                Lantern.getLogger().debug("Epoll is unavailable.", Epoll.unavailabilityCause());
            }
            epollAvailabilityLogged = true;
        }
        final ChannelFuture future = init0(address, epoll);
        this.initialized = true;
        return future;
    }

    protected abstract ChannelFuture init0(SocketAddress address, boolean epoll);

    public final void shutdown() {
        if (this.initialized) {
            shutdown0();
            this.initialized = false;
        }
    }

    protected abstract void shutdown0();

    /**
     * Creates a {@link EventLoopGroup}.
     *
     * @param epoll Whether a epoll event loop group should be created
     * @param threadFactory The thread factory
     * @return The event loop group
     */
    protected static EventLoopGroup createEventLoopGroup(boolean epoll, ThreadFactory threadFactory) {
        return epoll ? new EpollEventLoopGroup(0, threadFactory) : new NioEventLoopGroup(0, threadFactory);
    }

    /**
     * Creates a {@link EventLoopGroup}.
     *
     * @param epoll Whether a epoll event loop group should be created
     * @return The event loop group
     */
    protected static EventLoopGroup createEventLoopGroup(boolean epoll) {
        return epoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    }

    @SuppressWarnings("unchecked")
    protected static <C extends SocketChannel & ServerChannel> Class<C> getServerSocketChannelClass(boolean epoll) {
        return (Class) (epoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class);
    }

    @SuppressWarnings("unchecked")
    protected static <C extends SocketChannel> Class<C> getSocketChannelClass(boolean epoll) {
        return (Class) (epoll ? EpollSocketChannel.class : NioSocketChannel.class);
    }

    @SuppressWarnings("unchecked")
    protected static <C extends DatagramChannel> Class<C> getDatagramChannelClass(boolean epoll) {
        return (Class) (epoll ? EpollDatagramChannel.class : NioDatagramChannel.class);
    }
}
