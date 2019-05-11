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
package org.lanternpowered.server.network.status

import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import io.netty.buffer.Unpooled
import io.netty.handler.codec.base64.Base64
import org.lanternpowered.api.util.ToStringHelper
import org.spongepowered.api.network.status.Favicon
import java.awt.image.BufferedImage
import java.io.InputStream
import java.net.URL
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import javax.imageio.ImageIO

class LanternFavicon private constructor(
        private val decoded: BufferedImage,
        val encoded: String
) : Favicon {

    override fun getImage() = this.decoded

    override fun equals(other: Any?) = this === other || (other is LanternFavicon && this.encoded == other.encoded)
    override fun hashCode() = this.encoded.hashCode()

    override fun toString() = ToStringHelper(this)
            .addValue(this.decoded)
            .toString()

    object Factory : Favicon.Factory {

        private const val faviconPrefix = "data:image/png;base64,"

        override fun load(raw: String) = raw.replace("\n", "").let { LanternFavicon(decode(it), it) }
        override fun load(path: Path) = load(ImageIO.read(path.toFile()))
        override fun load(url: URL) = load(ImageIO.read(url))
        override fun load(input: InputStream) = load(ImageIO.read(input))
        override fun load(image: BufferedImage) = LanternFavicon(image, encode(image))

        /**
         * Encodes the buffered image into the encoded favicon string.
         *
         * @param image the buffered image
         * @return the favicon string
         */
        private fun encode(image: BufferedImage): String {
            check(image.width == 64) { "favicon must be 64 pixels wide" }
            check(image.height == 64) { "favicon must be 64 pixels high" }

            val buf = Unpooled.buffer()
            try {
                ImageIO.write(image, "PNG", ByteBufOutputStream(buf))
                val base64 = Base64.encode(buf, false)

                try {
                    return this.faviconPrefix + base64.toString(StandardCharsets.UTF_8)
                } finally {
                    base64.release()
                }
            } finally {
                buf.release()
            }
        }

        /**
         * Decodes the buffered image from the encoded favicon string.
         *
         * @param encoded the encoded string
         * @return the buffered image
         */
        private fun decode(encoded: String): BufferedImage {
            check(encoded.startsWith(this.faviconPrefix)) { "unknown favicon format" }

            val base64 = Unpooled.copiedBuffer(encoded.substring(this.faviconPrefix.length), StandardCharsets.UTF_8)
            try {
                val buf = Base64.decode(base64)
                try {
                    val result = ImageIO.read(ByteBufInputStream(buf))
                    check(result.width == 64) { "favicon must be 64 pixels wide" }
                    check(result.height == 64) { "favicon must be 64 pixels high" }
                    return result
                } finally {
                    buf.release()
                }
            } finally {
                base64.release()
            }
        }
    }
}
