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
package org.lanternpowered.server.status;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import org.spongepowered.api.network.status.Favicon;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import javax.imageio.ImageIO;

public class LanternFavicon implements Favicon {

    private final BufferedImage decoded;
    private final String encoded;

    private LanternFavicon(BufferedImage decoded, String encoded) {
        this.encoded = encoded;
        this.decoded = decoded;
    }

    /**
     * Gets the encoded string of the favicon.
     * 
     * @return the encoded string
     */
    public String getEncoded() {
        return this.encoded;
    }

    @Override
    public BufferedImage getImage() {
        return this.decoded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LanternFavicon)) {
            return false;
        }
        LanternFavicon that = (LanternFavicon) o;
        return Objects.equal(this.encoded, that.encoded);
    }

    @Override
    public int hashCode() {
        return this.encoded.hashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .addValue(this.decoded)
                .toString();
    }

    /**
     * Loads the favicon from the buffered image.
     * 
     * @param image the image
     * @return the favicon
     */
    public static Favicon load(BufferedImage image) throws IOException {
        return new LanternFavicon(image, encode(checkNotNull(image, "image")));
    }

    /**
     * Loads the favicon from the raw favicon data.
     * 
     * @param raw the raw data
     * @return the favicon
     */
    public static Favicon load(String raw) throws IOException {
        return new LanternFavicon(decode(checkNotNull(raw, "raw")), raw);
    }

    /**
     * Loads the favicon from the image path.
     * 
     * @param path the path
     * @return the favicon
     */
    public static Favicon load(Path path) throws IOException {
        return load(ImageIO.read(checkNotNull(path, "path").toFile()));
    }

    /**
     * Loads the favicon from the url.
     * 
     * @param url the url
     * @return the favicon
     */
    public static Favicon load(URL url) throws IOException {
        return load(ImageIO.read(checkNotNull(url, "url")));
    }

    /**
     * Loads the favicon from the input stream.
     * 
     * @param in the input stream
     * @return the favicon
     */
    public static Favicon load(InputStream in) throws IOException {
        return load(ImageIO.read(checkNotNull(in, "in")));
    }

    private static final String FAVICON_PREFIX = "data:image/png;base64,";

    /**
     * Encodes the buffered image into the encoded favicon string.
     * 
     * @param image the buffered image
     * @return the favicon string
     */
    private static String encode(BufferedImage image) throws IOException {
        checkArgument(image.getWidth() == 64, "favicon must be 64 pixels wide");
        checkArgument(image.getHeight() == 64, "favicon must be 64 pixels high");

        ByteBuf buf = Unpooled.buffer();

        try {
            ImageIO.write(image, "PNG", new ByteBufOutputStream(buf));
            ByteBuf base64 = Base64.encode(buf);

            try {
                return FAVICON_PREFIX + base64.toString(StandardCharsets.UTF_8);
            } finally {
                base64.release();
            }
        } finally {
            buf.release();
        }
    }

    /**
     * Decodes the buffered image from the encoded favicon string.
     * 
     * @param encoded the encoded string
     * @return the buffered image
     */
    private static BufferedImage decode(String encoded) throws IOException {
        checkArgument(encoded.startsWith(FAVICON_PREFIX), "unknown favicon format");

        ByteBuf base64 = Unpooled.copiedBuffer(encoded.substring(FAVICON_PREFIX.length()), StandardCharsets.UTF_8);
        try {
            ByteBuf buf = Base64.decode(base64);
            try {
                BufferedImage result = ImageIO.read(new ByteBufInputStream(buf));
                checkState(result.getWidth() == 64, "favicon must be 64 pixels wide");
                checkState(result.getHeight() == 64, "favicon must be 64 pixels high");
                return result;
            } finally {
                buf.release();
            }
        } finally {
            base64.release();
        }
    }

}
