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

import static com.google.common.base.Preconditions.checkNotNull;

import io.netty.util.ReferenceCounted;
import org.lanternpowered.server.network.IReferenceCounted;

/**
 * A abstract {@link Message} implementation which
 * can hold a {@link ReferenceCounted} object.
 *
 * @param <T> The reference counted object type
 */
public abstract class AbstractReferenceCountedMessage<T extends ReferenceCounted> implements Message, IReferenceCounted {

    protected final T object;

    protected AbstractReferenceCountedMessage(T object) {
        checkNotNull(object, "object");
        this.object = object;
    }

    @Override
    public int refCnt() {
        return this.object.refCnt();
    }

    @Override
    public ReferenceCounted retain(int increment) {
        return this.object.retain(increment);
    }

    @Override
    public ReferenceCounted touch(Object hint) {
        return this.object.touch(hint);
    }

    @Override
    public boolean release(int decrement) {
        return this.object.release(decrement);
    }
}
