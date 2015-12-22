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
package org.lanternpowered.server.network.message.codec.object.serializer;

import javax.annotation.Nullable;

public interface ObjectSerializers {

    /**
     * Registers a new object serializer.
     * 
     * @param type the object type
     * @param serializer the serializer
     */
    <T> void register(Class<T> type, ObjectSerializer<? super T> serializer);

    /**
     * Searches a codec serializer with exact the same type.
     * 
     * @param type the type
     * @return the codec serializer
     */
    @Nullable
    <T> ObjectSerializer<T> findExact(Class<T> type);

    /**
     * Searches a codec serializer with a type that matches, this can
     * be either a interface or superclass.
     * 
     * @param type the type
     * @return the codec serializer
     */
    @Nullable
    <T> ObjectSerializer<? super T> find(Class<T> type);
}
