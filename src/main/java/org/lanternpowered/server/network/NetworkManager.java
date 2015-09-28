package org.lanternpowered.server.network;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import org.lanternpowered.server.LanternServer;
import org.lanternpowered.server.network.pipeline.MessageChannelInitializer;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.session.SessionRegistry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public final class NetworkManager {

    private final ServerBootstrap bootstrap = new ServerBootstrap();
    private final AtomicInteger counter = new AtomicInteger(0);

    private final EventLoopGroup bossGroup = new NioEventLoopGroup(0,
            runnable -> new Thread(runnable, "netty-" + this.counter.getAndIncrement()));
    private final EventLoopGroup workerGroup = new NioEventLoopGroup(0,
            runnable -> new Thread(runnable, "netty-" + this.counter.getAndIncrement()));

    private final SessionRegistry sessionRegistry = new SessionRegistry();
    private final LanternServer server;

    private SocketAddress socketAddress;

    public NetworkManager(LanternServer server) {
        this.server = server;
    }

    /**
     * Gets the socket address.
     * 
     * @return the socket address
     */
    @Nullable
    public SocketAddress getAddress() {
        return this.socketAddress;
    }

    /**
     * Gets the server.
     * 
     * @return the server
     */
    public LanternServer getServer() {
        return this.server;
    }

    /**
     * Gets the session registry.
     * 
     * @return the session registry
     */
    public SessionRegistry getSessionRegistry() {
        return this.sessionRegistry;
    }

    /**
     * Creates a new session for the channel.
     * 
     * @param channel the channel
     * @return the session
     */
    public Session newSession(Channel channel) {
        return new Session(this.server, channel);
    }

    /**
     * Called when the channel becomes active.
     * 
     * @param channel the channel
     * @param session the session
     */
    public void onChannelActive(Channel channel, Session session) {
        this.sessionRegistry.add(session);
    }

    /**
     * Called when the channel becomes inactive.
     * 
     * @param channel the channel
     * @param session the session
     */
    public void onChannelInactive(Channel channel, Session session) {
        this.sessionRegistry.remove(session);
    }

    /**
     * Initializes and loads the netty server.
     * 
     * @param address the address
     */
    public ChannelFuture init(SocketAddress address) {
        this.socketAddress = address;
        return this.bootstrap
                .group(this.bossGroup, this.workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new MessageChannelInitializer(this))
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .bind(address);
    }

    /**
     * Shuts down the netty server.
     */
    public void shutdown() {
        this.workerGroup.shutdownGracefully();
        this.bossGroup.shutdownGracefully();
    }
}
