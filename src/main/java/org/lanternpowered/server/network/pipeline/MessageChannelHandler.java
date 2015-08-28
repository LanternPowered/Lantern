package org.lanternpowered.server.network.pipeline;

import org.lanternpowered.server.network.NetworkManager;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.SimpleCodecContext;
import org.lanternpowered.server.network.message.codec.object.serializer.SimpleObjectSerializers;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.session.Session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

public final class MessageChannelHandler extends SimpleChannelInboundHandler<Message> {

    private final NetworkManager networkManager;

    public MessageChannelHandler(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        ctx.channel().attr(Session.SESSION).get().messageReceived(message);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        Session session = this.networkManager.newSession(channel);

        if (!channel.attr(Session.SESSION).compareAndSet(null, session)) {
            throw new IllegalStateException("Session may not be set more than once!");
        }
        channel.attr(MessageCodecHandler.CONTEXT).set(new SimpleCodecContext(
                SimpleObjectSerializers.DEFAULT, channel, session));
        channel.attr(Session.STATE).set(ProtocolState.HANDSHAKE);

        this.networkManager.onChannelActive(channel, session);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();

        // Disconnect the session
        Session session = channel.attr(Session.SESSION).get();
        session.onDisconnect();

        this.networkManager.onChannelInactive(channel, session);

        // Remove the attributes from the channel
        channel.attr(Session.STATE).remove();
        channel.attr(Session.SESSION).remove();
        channel.attr(MessageCodecHandler.CONTEXT).remove();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Session session = ctx.channel().attr(Session.SESSION).get();

        if (session != null) {
            session.onInboundThrowable(cause);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object event) throws Exception {
        if (event instanceof IdleStateEvent) {
            Session session = ctx.channel().attr(Session.SESSION).get();
            if (session != null) {
                session.idle();
            }
        }
    }

}
