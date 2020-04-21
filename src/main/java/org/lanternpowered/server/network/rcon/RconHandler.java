/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
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
import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.network.rcon.RconConnectionEvent;
import org.spongepowered.api.text.Text;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

final class RconHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static final AttributeKey<LanternRconConnection> CONNECTION = AttributeKey.valueOf("rcon-connection");

    private static final byte FAILURE = -1;
    private static final byte TYPE_RESPONSE = 0;
    private static final byte TYPE_COMMAND = 2;
    private static final byte TYPE_AUTH = 3;

    private final RconServer server;
    private final String password;

    RconHandler(RconServer server, String password) {
        this.password = password;
        this.server = server;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) {
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

        if (type == TYPE_AUTH) {
            handleAuth(ctx, payload, this.password, requestId);
        } else if (type == TYPE_COMMAND) {
            handleCommand(ctx, payload, requestId);
        } else {
            sendLargeResponse(ctx, requestId, "Unknown request " + Integer.toHexString(type));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws ExecutionException, InterruptedException {
        final Channel channel = ctx.channel();
        final LanternRconConnection connection = this.server.newConnection(channel);

        final Cause cause = Cause.of(EventContext.empty(), connection);
        final RconConnectionEvent.Connect event = SpongeEventFactory.createRconConnectionEventConnect(cause, connection);

        Lantern.getSyncScheduler().submit(() -> Sponge.getEventManager().post(event)).get();

        if (event.isCancelled()) {
            ctx.channel().close();
        }

        if (!channel.attr(CONNECTION).compareAndSet(null, connection)) {
            throw new IllegalStateException("Rcon source may not be set more than once!");
        }

        this.server.onChannelActive(connection);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws ExecutionException, InterruptedException {
        final Channel channel = ctx.channel();
        final LanternRconConnection connection = channel.attr(CONNECTION).getAndSet(null);

        if (connection == null) {
            return;
        }

        final Cause cause = Cause.of(EventContext.empty(), connection);
        final RconConnectionEvent.Disconnect event = SpongeEventFactory.createRconConnectionEventDisconnect(cause, connection);

        Lantern.getSyncScheduler().submit(() -> Sponge.getEventManager().post(event)).get();

        this.server.onChannelInactive(connection);
    }

    private static void handleAuth(ChannelHandlerContext ctx, String payload, String password, int requestId) {
        final LanternRconConnection connection = ctx.channel().attr(CONNECTION).get();
        Lantern.getSyncScheduler().submit(() -> {
            final Cause cause = Cause.of(EventContext.empty(), connection);
            final RconConnectionEvent.Auth event = SpongeEventFactory.createRconConnectionEventAuth(cause, connection);
            event.setCancelled(!password.equals(payload));

            Sponge.getEventManager().post(event);
            connection.setAuthorized(!event.isCancelled());

            if (connection.isAuthorized()) {
                Lantern.getLogger().info("Rcon connection from [" + ctx.channel().remoteAddress() + "]");
                ctx.channel().eventLoop().submit(() -> sendResponse(ctx, requestId, TYPE_COMMAND, ""));
            } else {
                ctx.channel().eventLoop().submit(() -> sendResponse(ctx, FAILURE, TYPE_COMMAND, ""));
            }
        });
    }

    private static void handleCommand(ChannelHandlerContext ctx, String payload, int requestId) {
        final LanternRconConnection connection = ctx.channel().attr(CONNECTION).get();
        if (!connection.isAuthorized()) {
            sendResponse(ctx, FAILURE, TYPE_COMMAND, "");
            return;
        }
        // Process the command on the main thread and send
        // the response on the netty thread.
        Lantern.getSyncScheduler().submit(() -> {
            final CauseStack causeStack = CauseStack.current();
            causeStack.pushCause(connection);
            try {
                Sponge.getCommandManager().process(connection, payload);
            } catch (CommandException e) {
                connection.sendMessage(Text.of("An error occurred while executing the command: " + payload + "; " + e));
            }
            causeStack.popCause();
            final String content = connection.flush();
            // Send the response on the netty thread
            ctx.channel().eventLoop().submit(() -> sendLargeResponse(ctx, requestId, content));
        });
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
            final int truncated = Math.min(length, 2048);

            sendResponse(ctx, requestId, TYPE_RESPONSE, payload.substring(start, truncated));
            start += truncated;
        }
    }
}
