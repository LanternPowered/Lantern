package org.lanternpowered.server.network.rcon;

import java.net.InetSocketAddress;
import java.util.Set;

import org.spongepowered.api.service.rcon.RconService;

import com.google.common.collect.Sets;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class RconServer implements RconService {

    private final Set<RconSource> sources = Sets.newConcurrentHashSet();

    private ServerBootstrap bootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private String password = "";
    private InetSocketAddress address;

    /**
     * Initializes the rcon server.
     * 
     * @param address the address to bind to
     * @param password the password
     * @return the channel future
     */
    public ChannelFuture init(final InetSocketAddress address, final String password) {
        if (this.bootstrap != null) {
            throw new IllegalStateException("The rcon server is already active.");
        }

        this.password = password;
        this.address = address;

        this.bootstrap = new ServerBootstrap();
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();

        return this.bootstrap
                .group(this.bossGroup, this.workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new RconFramingHandler())
                                .addLast(new RconHandler(RconServer.this, password));
                    }

                })
                .bind(address);
    }

    /**
     * Shut the Rcon server down.
     */
    public void shutdown() {
        if (this.bootstrap == null) {
            throw new IllegalStateException("The rcon server is not active.");
        }
        this.workerGroup.shutdownGracefully();
        this.bossGroup.shutdownGracefully();
        this.workerGroup = null;
        this.bossGroup = null;
        this.bootstrap = null;
    }

    /**
     * Creates a new rcon source.
     * 
     * @param channel the channel
     * @return the rcon source
     */
    public RconSource newSource(Channel channel) {
        return new RconSource(new RconConnection((InetSocketAddress) channel.remoteAddress(), this.address));
    }

    /**
     * Called when the channel becomes active.
     * 
     * @param channel the channel
     * @param source the source
     */
    public void onChannelActive(Channel channel, RconSource source) {
        this.sources.add(source);
    }

    /**
     * Called when the channel becomes inactive.
     * 
     * @param channel the channel
     * @param source the source
     */
    public void onChannelInactive(Channel channel, RconSource source) {
        this.sources.remove(source);
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
