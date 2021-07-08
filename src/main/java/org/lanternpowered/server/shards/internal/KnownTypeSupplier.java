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
package org.lanternpowered.server.shards.internal;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.reflect.TypeToken;

import java.util.function.Supplier;

import javax.annotation.Nullable;

/**
 * Represents a {@link Supplier} of which the
 * returned object {@link TypeToken} is known.
 *
 * @param <T> The type
 */
@SuppressWarnings("unchecked")
public final class KnownTypeSupplier<T> implements Supplier<T> {

    @Nullable private TypeToken<T> typeToken;
    private final Supplier<T> supplier;

    public KnownTypeSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        final T object = this.supplier.get();
        if (this.typeToken != null) {
            checkState(this.typeToken.getRawType().isInstance(object));
        } else {
            this.typeToken = TypeToken.of((Class<T>) object.getClass());
        }
        return object;
    }

    public TypeToken<?> getObjectType() {
        if (this.typeToken == null) {
            this.typeToken = TypeToken.of((Class<T>) this.supplier.get().getClass());
        }
        return this.typeToken;
    }
}
