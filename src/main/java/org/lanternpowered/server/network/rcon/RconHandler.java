/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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
package org.lanternpowered.server.network.rcon;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.network.rcon.RconConnectionEvent;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

public class RconHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static final AttributeKey<RconSource> SOURCE = AttributeKey.valueOf("rcon-source");

    private static final byte FAILURE = -1;
    private static final byte TYPE_RESPONSE = 0;
    private static final byte TYPE_COMMAND = 2;
    private static final byte TYPE_LOGIN = 3;

    private final RconServer server;
    private final String password;

    public RconHandler(RconServer server, String password) {
        this.password = password;
        this.server = server;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        buf = buf.order(ByteOrder.LITTLE_ENDIAN);
        if (buf.readableBytes() < 8) {
            return;
        }

        int requestId = buf.readInt();
        int type = buf.readInt();

        byte[] payloadData = new byte[buf.readableBytes() - 2];
        buf.readBytes(payloadData);
        String payload = new String(payloadData, StandardCharsets.UTF_8);

        // Two byte padding
        buf.readBytes(2);

        if (type == TYPE_LOGIN) {
            handleLogin(ctx, payload, this.password, requestId);
        } else if (type == TYPE_COMMAND) {
            handleCommand(ctx, payload, requestId);
        } else {
            sendLargeResponse(ctx, requestId, "Unknown request " + Integer.toHexString(type));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        RconSource source = this.server.newSource(channel);

        if (!channel.attr(SOURCE).compareAndSet(null, source)) {
            throw new IllegalStateException("Rcon source may not be set more than once!");
        }

        this.server.onChannelActive(channel, source);

        RconConnectionEvent.Connect event = SpongeEventFactory.createRconConnectionEventConnect(source);
        LanternGame.get().getEventManager().post(event);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        RconSource source = channel.attr(SOURCE).getAndRemove();

        RconConnectionEvent.Disconnect event = SpongeEventFactory.createRconConnectionEventDisconnect(source);
        LanternGame.get().getEventManager().post(event);

        this.server.onChannelInactive(channel, source);
    }

    private static void handleLogin(ChannelHandlerContext ctx, String payload, String password, int requestId) {
        RconSource source = ctx.channel().attr(SOURCE).get();
        if (password.equals(payload)) {
            RconConnectionEvent.Login event = SpongeEventFactory.createRconConnectionEventLogin(source);

            if (!LanternGame.get().getEventManager().post(event)) {
                source.setLoggedIn(true);
                sendResponse(ctx, requestId, TYPE_COMMAND, "");

                LanternGame.log().info("Rcon connection from [" + ctx.channel().remoteAddress() + "]");
                return;
            }
        }
        source.setLoggedIn(false);
        sendResponse(ctx, FAILURE, TYPE_COMMAND, "");
    }

    private static void handleCommand(ChannelHandlerContext ctx, String payload, int requestId) {
        RconSource source = ctx.channel().attr(SOURCE).get();
        if (!source.getLoggedIn()) {
            sendResponse(ctx, FAILURE, TYPE_COMMAND, "");
            return;
        }
        LanternGame.get().getCommandDispatcher().process(source, payload);
        sendLargeResponse(ctx, requestId, source.flush());
    }

    private static void sendResponse(ChannelHandlerContext ctx, int requestId, int type, String payload) {
        ByteBuf buf = ctx.alloc().buffer().order(ByteOrder.LITTLE_ENDIAN);
        buf.writeInt(requestId);
        buf.writeInt(type);
        buf.writeBytes(payload.getBytes(StandardCharsets.UTF_8));
        buf.writeByte(0);
        buf.writeByte(0);
        ctx.write(buf);
    }

    private static void sendLargeResponse(ChannelHandlerContext ctx, int requestId, String payload) {
        if (payload.length() == 0) {
            sendResponse(ctx, requestId, TYPE_RESPONSE, "");
            return;
        }
        int start = 0;
        while (start < payload.length()) {
            int length = payload.length() - start;
            int truncated = length > 2048 ? 2048 : length;

            sendResponse(ctx, requestId, TYPE_RESPONSE, payload.substring(start, truncated));
            start += truncated;
        }
    }

}
