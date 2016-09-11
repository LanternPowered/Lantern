/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
package org.lanternpowered.server.data.io.store;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.util.Identifiable;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ObjectSerializerFactory {

    public static <T extends Identifiable> ObjectSerializer<T> of(Class<T> type, Function<UUID, T> objectConstructor) {
        checkNotNull(type, "type");
        checkNotNull(objectConstructor, "objectConstructor");
        return new IdentifiableObjectSerializer<>(ObjectStoreRegistry.get().get(type)
                .orElseThrow(() -> new IllegalArgumentException("There is no object registry for " + type)), objectConstructor);
    }

    public static <T> ObjectSerializer<T> of(Class<T> type, Supplier<T> objectConstructor) {
        checkNotNull(type, "type");
        checkNotNull(objectConstructor, "objectConstructor");
        return new SimpleObjectSerializer<>(ObjectStoreRegistry.get().get(type)
                .orElseThrow(() -> new IllegalArgumentException("There is no object registry for " + type)), objectConstructor);
    }

    private ObjectSerializerFactory() {
    }
}
