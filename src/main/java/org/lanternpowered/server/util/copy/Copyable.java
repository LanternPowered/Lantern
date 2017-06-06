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
package org.lanternpowered.server.util.copy;

import com.google.common.reflect.TypeToken;

import java.util.Optional;
import java.util.function.Function;

/**
 * Represents a object that can be copied.
 *
 * @param <T> The type of the object
 */
public interface Copyable<T> {

    /**
     * Creates a copy of this object.
     *
     * @return The copy
     */
    T copy();

    /**
     * Attempts to create a copy of the given object.
     *
     * @param object The object
     * @param <T> The object type
     * @return The copy if successful, otherwise {@link Optional#empty()}
     */
    static <T> Optional<T> copy(T object) {
        return CopyableTypes.copy(object);
    }

    /**
     * Attempts to create a copy of the given object.
     *
     * @param object The object
     * @param <T> The object type
     * @return The copy if successful, otherwise {@link Optional#empty()}
     */
    static <T> T copyOrSelf(T object) {
        return CopyableTypes.copyOrSelf(object);
    }

    /**
     * Registers a copy function for the given
     * {@link Class}.
     *
     * @param type The type token
     * @param copyFunction The copy function
     * @param <T> The copied type
     */
    static <T> void register(Class<T> type, Function<T, T> copyFunction) {
        register(TypeToken.of(type), copyFunction);
    }

    /**
     * Registers a copy function for the given
     * {@link TypeToken}.
     *
     * @param type The type token
     * @param copyFunction The copy function
     * @param <T> The copied type
     */
    static <T> void register(TypeToken<T> type, Function<T, T> copyFunction) {
        CopyableTypes.register(type, copyFunction);
    }
}
