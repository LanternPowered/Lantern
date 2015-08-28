package org.lanternpowered.server.network.pipeline;

import org.lanternpowered.server.network.NetworkManager;
import org.lanternpowered.server.network.pipeline.MessageCodecHandler;
import org.lanternpowered.server.network.pipeline.MessageCompressionHandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

public final class MessageChannelInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * The time in seconds which are elapsed before a client is disconnected due 
     * to a read timeout.
     */
    private static final int READ_TIMEOUT = 20;

    /**
     * The time in seconds which are elapsed before a client is deemed idle due 
     * to a write timeout.
     */
    private static final int WRITE_IDLE_TIMEOUT = 15;

    private final NetworkManager networkManager;

    public MessageChannelInitializer(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline()
                // .addLast("legacyping", new LegacyPingHandler())
                .addLast("framing", new MessageFramingHandler())
                .addLast("compression", new MessageCompressionHandler())
                .addLast("codecs", new MessageCodecHandler())
                .addLast("readtimeout", new ReadTimeoutHandler(READ_TIMEOUT))
                .addLast("writeidletimeout", new IdleStateHandler(0, WRITE_IDLE_TIMEOUT, 0))
                .addLast("processor", new MessageProcessorHandler())
                .addLast("handler", new MessageChannelHandler(this.networkManager));
    }
}
