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
package org.lanternpowered.server.data.persistence;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.persistence.InvalidDataException;

public interface DataTypeSerializer<T, D> {

    /**
     * Attempts to deserialize the {@code T} object from the provided
     * {@link DataView}.
     *
     * @param type The type token of the deserialized object
     * @param data The data to deserialize the object from
     * @return The deserialized object, if available
     */
    T deserialize(TypeToken<?> type, DataTypeSerializerContext ctx, D data) throws InvalidDataException;

    /**
     * Serializes the provided object to a {@link DataContainer}.
     *
     * @param type The type token of the serialized object
     * @param obj The object to serialize
     * @return The serialized data object
     * @throws InvalidDataException If the desired object is not supported
     *     for any reason
     */
    D serialize(TypeToken<?> type, DataTypeSerializerContext ctx, T obj) throws InvalidDataException;
}
