package org.lanternpowered.server.network.pipeline;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public final class NoopHandler extends ChannelHandlerAdapter {

    public static final NoopHandler INSTANCE = new NoopHandler();
}
