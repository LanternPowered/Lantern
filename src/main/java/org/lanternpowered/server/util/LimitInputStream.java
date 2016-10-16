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
package org.lanternpowered.server.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A {@link InputStream} that allows only a limits the amount of bytes that may be
 * read and throws a {@link IOException} if the limit is exceeded.
 */
public final class LimitInputStream extends FilterInputStream {

    private long left;
    private long mark = -1;

    /**
     * Creates a new {@link LimitInputStream} for the specified {@link InputStream} and
     * the limited amount of bytes that may be read.
     *
     * @param in the input stream
     * @param limit the limit of bytes
     */
    public LimitInputStream(InputStream in, long limit) {
        super(checkNotNull(in, "in"));
        checkArgument(limit >= 0L, "limit must be non-negative");
        this.left = limit;
    }

    @Override
    public int available() throws IOException {
        return this.in.available();
    }

    @Override
    public synchronized void mark(int readLimit) {
        this.in.mark(readLimit);
        this.mark = this.left;
    }

    @Override
    public int read() throws IOException {
        if (this.left == 0L) {
            throw new IOException("Limit of bytes exceeded.");
        }
        int result = this.in.read();
        if (result != -1) {
            this.left -= 1L;
        }
        return result;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.left - len < 0) {
            throw new IOException("Limit of bytes exceeded.");
        }
        int result = this.in.read(b, off, len);
        if (result != -1) {
            this.left -= result;
        }
        return result;
    }

    @Override
    public synchronized void reset() throws IOException {
        if (!this.in.markSupported()) {
            throw new IOException("Mark not supported.");
        }
        if (this.mark == -1L) {
            throw new IOException("Mark not set.");
        }
        this.in.reset();
        this.left = this.mark;
    }

    @Override
    public long skip(long n) throws IOException {
        if (this.left - n < 0) {
            throw new IOException("Limit of bytes exceeded.");
        }
        long skipped = this.in.skip(n);
        this.left -= skipped;
        return skipped;
    }
}
