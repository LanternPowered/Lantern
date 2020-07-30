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
package org.lanternpowered.server.network.pipeline

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.CodecException
import io.netty.handler.codec.MessageToMessageCodec
import java.nio.ByteBuffer
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.ShortBufferException
import javax.crypto.spec.IvParameterSpec

class PacketEncryptionHandler(sharedSecret: SecretKey) : MessageToMessageCodec<ByteBuf, ByteBuf>() {

    private var encodeBuf = CryptBuf(Cipher.ENCRYPT_MODE, sharedSecret)
    private var decodeBuf = CryptBuf(Cipher.DECRYPT_MODE, sharedSecret)

    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) = this.encodeBuf.crypt(msg, out)
    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) = this.decodeBuf.crypt(msg, out)

    private class CryptBuf(mode: Int, sharedSecret: SecretKey) {

        private val cipher: Cipher = Cipher.getInstance("AES/CFB8/NoPadding")

        init {
            this.cipher.init(mode, sharedSecret, IvParameterSpec(sharedSecret.encoded))
        }

        fun crypt(msg: ByteBuf, out: MutableList<Any>) {
            val outBuffer = ByteBuffer.allocate(msg.readableBytes())
            try {
                this.cipher.update(msg.nioBuffer(), outBuffer)
            } catch (e: ShortBufferException) {
                throw CodecException("Encryption buffer was too short", e)
            }
            outBuffer.flip()
            out.add(Unpooled.wrappedBuffer(outBuffer))
        }
    }
}
