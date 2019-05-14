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
package org.lanternpowered.server.network.rcon;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import org.lanternpowered.server.network.AbstractServer;
import org.spongepowered.api.service.rcon.RconService;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

public class RconServer extends AbstractServer implements RconService {

    private final Map<String, LanternRconConnection> connectionByHostname = new ConcurrentHashMap<>();
    private final String password;

    @Nullable private ServerBootstrap bootstrap;
    @Nullable private EventLoopGroup bossGroup;
    @Nullable private EventLoopGroup workerGroup;

    @Nullable private InetSocketAddress address;

    public RconServer(String password) {
        this.password = password;
    }

    @Override
    protected ChannelFuture init(SocketAddress address, TransportType transportType) {
        this.address = (InetSocketAddress) address;
        this.bootstrap = new ServerBootstrap();
        this.bossGroup = createEventLoopGroup(transportType);
        this.workerGroup = createEventLoopGroup(transportType);
        return this.bootstrap
                .group(this.bossGroup, this.workerGroup)
                .channel(getServerSocketChannelClass(transportType))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline()
                                .addLast(new RconFramingHandler())
                                .addLast(new RconHandler(RconServer.this, password));
                    }
                })
                .bind(address);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void shutdown0() {
        this.workerGroup.shutdownGracefully();
        this.bossGroup.shutdownGracefully();
        this.workerGroup = null;
        this.bossGroup = null;
        this.bootstrap = null;
    }

    /**
     * Creates a new {@link LanternRconConnection}.
     * 
     * @param channel The channel
     * @return source The rcon source
     */
    LanternRconConnection newConnection(Channel channel) {
        //noinspection ConstantConditions
        return new LanternRconConnection((InetSocketAddress) channel.remoteAddress(), this.address);
    }

    /**
     * Called when the {@link LanternRconConnection} becomes active.
     *
     * @param connection The rcon connection
     */
    void onChannelActive(LanternRconConnection connection) {
        this.connectionByHostname.put(connection.getAddress().getHostName(), connection);
    }

    /**
     * Called when the {@link LanternRconConnection} becomes inactive.
     *
     * @param connection The rcon connection
     */
    void onChannelInactive(LanternRconConnection connection) {
        this.connectionByHostname.remove(connection.getAddress().getHostName());
    }

    public Optional<LanternRconConnection> getByHostName(String hostname) {
        return Optional.ofNullable(this.connectionByHostname.get(hostname));
    }

    @Override
    public boolean isRconEnabled() {
        return this.bootstrap != null;
    }

    @Override
    public String getRconPassword() {
        return this.password;
    }

}
