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
package org.lanternpowered.server.network.rcon

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.util.AttributeKey
import org.lanternpowered.api.Lantern
import org.lanternpowered.api.cause.CauseStack.Companion.current
import org.lanternpowered.api.cause.causeOf
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.event.LanternEventFactory
import org.lanternpowered.api.text.textOf
import org.lanternpowered.server.LanternGame
import org.lanternpowered.server.util.future.thenAsync
import org.spongepowered.api.command.exception.CommandException
import org.spongepowered.api.event.network.rcon.RconConnectionEvent
import java.nio.charset.StandardCharsets

internal class RconHandler(
        private val server: RconServer,
        private val password: String
) : SimpleChannelInboundHandler<ByteBuf>() {

    override fun channelRead0(ctx: ChannelHandlerContext, buf: ByteBuf) {
        if (buf.readableBytes() < 8)
            return

        val requestId = buf.readIntLE()
        val type = buf.readIntLE()
        val payloadData = ByteArray(buf.readableBytes() - 2)
        buf.readBytes(payloadData)
        val payload = String(payloadData, StandardCharsets.UTF_8)

        // Two byte padding
        buf.readBytes(2)
        when (type) {
            TYPE_AUTH -> handleAuth(ctx, payload, this.password, requestId)
            TYPE_COMMAND -> handleCommand(ctx, payload, requestId)
            else -> sendLargeResponse(ctx, requestId, "Unknown request " + type.toString(16))
        }
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        val channel = ctx.channel()
        val connection = LanternRconConnection(channel, this.server.address)
        check(channel.attr(CONNECTION).compareAndSet(null, connection)) { "Rcon source may not be set more than once!" }
        LanternGame.syncScheduler
                .submit {
                    val cause = causeOf(connection)
                    val event = LanternEventFactory.createRconConnectionEventConnect(cause, connection)
                    EventManager.post(event)
                    event
                }
                .thenAsync(channel.eventLoop()) { event ->
                    if (event.isCancelled) {
                        ctx.channel().close()
                    } else {
                        this.server.add(connection)
                    }
                }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        val channel = ctx.channel()
        val connection = channel.attr(CONNECTION).getAndSet(null) ?: return
        val cause = causeOf(connection)
        this.server.remove(connection)
        val event = LanternEventFactory.createRconConnectionEventDisconnect(cause, connection)
        LanternGame.syncScheduler.submit { EventManager.post(event) }
    }

    companion object {

        private val CONNECTION = AttributeKey.valueOf<LanternRconConnection>("rcon-connection")

        private const val FAILURE = -1
        private const val TYPE_RESPONSE = 0
        private const val TYPE_COMMAND = 2
        private const val TYPE_AUTH = 3

        private fun handleAuth(ctx: ChannelHandlerContext, payload: String, password: String, requestId: Int) {
            val connection = ctx.channel().attr(CONNECTION).get()
            LanternGame.syncScheduler.submit {
                val cause = causeOf(connection)
                val event: RconConnectionEvent.Auth = LanternEventFactory.createRconConnectionEventAuth(cause, connection)
                event.isCancelled = password != payload
                EventManager.post(event)
                connection.isAuthorized = !event.isCancelled
                if (connection.isAuthorized) {
                    LanternGame.logger.info("Rcon connection from [" + ctx.channel().remoteAddress() + "]")
                    ctx.channel().eventLoop().submit { sendResponse(ctx, requestId, TYPE_COMMAND, "") }
                } else {
                    ctx.channel().eventLoop().submit { sendResponse(ctx, FAILURE, TYPE_COMMAND, "") }
                }
            }
        }

        private fun handleCommand(ctx: ChannelHandlerContext, payload: String, requestId: Int) {
            val connection = ctx.channel().attr(CONNECTION).get()
            if (!connection.isAuthorized) {
                sendResponse(ctx, FAILURE, TYPE_COMMAND, "")
                return
            }
            // Process the command on the main thread and send
            // the response on the netty thread.
            LanternGame.syncScheduler.submit {
                val causeStack = current()
                causeStack.pushCause(connection)
                try {
                    Lantern.commandManager.process(connection, payload)
                } catch (e: CommandException) {
                    connection.sendMessage(textOf("An error occurred while executing the command: $payload; $e"))
                }
                causeStack.popCause()
                connection.flush()
            }.thenAsync(ctx.channel().eventLoop()) { content ->
                // Send the response on the netty thread
                sendLargeResponse(ctx, requestId, content)
            }
        }

        private fun sendResponse(ctx: ChannelHandlerContext, requestId: Int, type: Int, payload: String) {
            val buf = ctx.alloc().buffer()
            buf.writeIntLE(requestId)
            buf.writeIntLE(type)
            buf.writeBytes(payload.toByteArray(StandardCharsets.UTF_8))
            buf.writeByte(0)
            buf.writeByte(0)
            ctx.write(buf)
        }

        private fun sendLargeResponse(ctx: ChannelHandlerContext, requestId: Int, payload: String) {
            if (payload.isEmpty()) {
                sendResponse(ctx, requestId, TYPE_RESPONSE, "")
                return
            }
            var start = 0
            while (start < payload.length) {
                val length = payload.length - start
                val truncated = length.coerceAtMost(2048)
                sendResponse(ctx, requestId, TYPE_RESPONSE, payload.substring(start, truncated))
                start += truncated
            }
        }
    }
}
