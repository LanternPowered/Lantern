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
 * Copyright (c) 2018 Velocity team
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
package org.lanternpowered.server.network.http

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.codec.http.HttpContent
import io.netty.handler.codec.http.LastHttpContent
import io.netty.util.ReferenceCountUtil
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture

class SimpleHttpResponseCollector(
        private val reply: CompletableFuture<SimpleHttpResponse>
) : ChannelInboundHandlerAdapter() {

    private val buffer = StringBuilder()
    private var httpCode = 0

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        try {
            if (msg is HttpResponse<*>)
                this.httpCode = msg.statusCode()
            if (msg is HttpContent) {
                this.buffer.append(msg.content().toString(Charsets.UTF_8))
                if (msg is LastHttpContent) {
                    ctx.close()
                    this.reply.complete(SimpleHttpResponse(this.httpCode, this.buffer.toString()))
                }
            }
        } finally {
            ReferenceCountUtil.release(msg)
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        ctx.close()
        this.reply.completeExceptionally(cause)
    }
}
