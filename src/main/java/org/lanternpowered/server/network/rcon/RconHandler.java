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

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.lanternpowered.server.event.CauseStack;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.network.rcon.RconConnectionEvent;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

final class RconHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static final AttributeKey<RconSource> SOURCE = AttributeKey.valueOf("rcon-source");

    private static final byte FAILURE = -1;
    private static final byte TYPE_RESPONSE = 0;
    private static final byte TYPE_COMMAND = 2;
    private static final byte TYPE_LOGIN = 3;

    private final RconServer server;
    private final String password;

    RconHandler(RconServer server, String password) {
        this.password = password;
        this.server = server;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        if (buf.readableBytes() < 8) {
            return;
        }

        final int requestId = buf.readIntLE();
        final int type = buf.readIntLE();

        final byte[] payloadData = new byte[buf.readableBytes() - 2];
        buf.readBytes(payloadData);
        final String payload = new String(payloadData, StandardCharsets.UTF_8);

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
        final Channel channel = ctx.channel();
        final RconSource source = this.server.newSource(channel);

        final Cause cause = Cause.of(EventContext.empty(), source.getConnection());
        final RconConnectionEvent.Connect event = SpongeEventFactory.createRconConnectionEventConnect(cause, source);
        Sponge.getEventManager().post(event);
        if (event.isCancelled()) {
            ctx.channel().close();
            return;
        }

        if (!channel.attr(SOURCE).compareAndSet(null, source)) {
            throw new IllegalStateException("Rcon source may not be set more than once!");
        }

        this.server.onChannelActive(source);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        final Channel channel = ctx.channel();
        final RconSource source = channel.attr(SOURCE).getAndSet(null);

        if (source == null) {
            return;
        }
        final Cause cause = Cause.of(EventContext.empty(), source.getConnection());
        final RconConnectionEvent.Disconnect event = SpongeEventFactory.createRconConnectionEventDisconnect(cause, source);
        Sponge.getEventManager().post(event);

        this.server.onChannelInactive(source);
    }

    private static void handleLogin(ChannelHandlerContext ctx, String payload, String password, int requestId) {
        final RconSource source = ctx.channel().attr(SOURCE).get();
        if (password.equals(payload)) {
            final Cause cause = Cause.of(EventContext.empty(), source.getConnection());
            final RconConnectionEvent.Login event = SpongeEventFactory.createRconConnectionEventLogin(cause, source);

            if (!Sponge.getEventManager().post(event)) {
                source.setLoggedIn(true);
                sendResponse(ctx, requestId, TYPE_COMMAND, "");

                Lantern.getLogger().info("Rcon connection from [" + ctx.channel().remoteAddress() + "]");
                return;
            }
        }
        source.setLoggedIn(false);
        sendResponse(ctx, FAILURE, TYPE_COMMAND, "");
    }

    private static void handleCommand(ChannelHandlerContext ctx, String payload, int requestId)
            throws ExecutionException, InterruptedException {
        final RconSource source = ctx.channel().attr(SOURCE).get();
        if (!source.getLoggedIn()) {
            sendResponse(ctx, FAILURE, TYPE_COMMAND, "");
            return;
        }
        // Process the command on the main thread and send
        // the response on the netty thread.
        final String content = Lantern.getScheduler().callSync(() -> {
            final CauseStack causeStack = CauseStack.current();
            causeStack.pushCause(source.getConnection());
            Sponge.getCommandManager().process(source, payload);
            causeStack.popCause();
            return source.flush();
        }).get();
        sendLargeResponse(ctx, requestId, content);
    }

    private static void sendResponse(ChannelHandlerContext ctx, int requestId, int type, String payload) {
        final ByteBuf buf = ctx.alloc().buffer();
        buf.writeIntLE(requestId);
        buf.writeIntLE(type);
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
            final int length = payload.length() - start;
            final int truncated = length > 2048 ? 2048 : length;

            sendResponse(ctx, requestId, TYPE_RESPONSE, payload.substring(start, truncated));
            start += truncated;
        }
    }
}
