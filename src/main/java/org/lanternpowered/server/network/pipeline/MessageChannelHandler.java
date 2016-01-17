/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.network.pipeline;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.lanternpowered.server.network.NetworkManager;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.SimpleCodecContext;
import org.lanternpowered.server.network.message.codec.serializer.SerializerCollection;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.session.Session;

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
                ctx.alloc(), SerializerCollection.DEFAULT, channel, session));
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
