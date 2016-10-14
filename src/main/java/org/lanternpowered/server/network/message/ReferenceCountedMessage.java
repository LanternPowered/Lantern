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
package org.lanternpowered.server.network.message;

import io.netty.util.ReferenceCounted;

import javax.annotation.Nullable;

/**
 * A simple implementation of a {@link Message} that is reference counted.
 */
public abstract class ReferenceCountedMessage implements Message, ReferenceCounted {

    @Override
    public int refCnt() {
        final ReferenceCounted referenceCounted = this.getReferenceCounted();
        return referenceCounted == null ? 1 : referenceCounted.refCnt();
    }

    @Override
    public ReferenceCounted retain() {
        final ReferenceCounted referenceCounted = this.getReferenceCounted();
        if (referenceCounted != null) {
            referenceCounted.retain();
        }
        return this;
    }

    @Override
    public ReferenceCounted retain(int increment) {
        final ReferenceCounted referenceCounted = this.getReferenceCounted();
        if (referenceCounted != null) {
            referenceCounted.retain(increment);
        }
        return this;
    }

    @Override
    public ReferenceCounted touch() {
        final ReferenceCounted referenceCounted = this.getReferenceCounted();
        if (referenceCounted != null) {
            referenceCounted.touch();
        }
        return this;
    }

    @Override
    public ReferenceCounted touch(Object hint) {
        final ReferenceCounted referenceCounted = this.getReferenceCounted();
        if (referenceCounted != null) {
            referenceCounted.touch(hint);
        }
        return this;
    }

    @Override
    public boolean release() {
        final ReferenceCounted referenceCounted = this.getReferenceCounted();
        return referenceCounted == null || referenceCounted.release();
    }

    @Override
    public boolean release(int decrement) {
        final ReferenceCounted referenceCounted = this.getReferenceCounted();
        return referenceCounted == null || referenceCounted.release(decrement);
    }

    @Nullable
    protected abstract ReferenceCounted getReferenceCounted();
}
