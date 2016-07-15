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
package org.lanternpowered.server.behavior;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import com.google.common.reflect.TypeToken;

import java.util.concurrent.atomic.AtomicInteger;

public final class Parameter<T> {

    public static <T> Parameter<T> of(Class<T> type, String name) {
        return of(TypeToken.of(type), name);
    }

    public static <T> Parameter<T> of(TypeToken<T> type, String name) {
        checkNotNull(type, "type");
        checkNotNullOrEmpty(name, "name");
        return new Parameter<>(type, name, counter.getAndIncrement());
    }

    private static final AtomicInteger counter = new AtomicInteger();

    private final TypeToken<T> type;
    private final String name;
    private final int index;

    private Parameter(TypeToken<T> type, String name, int index) {
        this.name = name;
        this.index = index;
        this.type = type;
    }

    int getIndex() {
        return this.index;
    }

    public TypeToken<T> getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }
}
