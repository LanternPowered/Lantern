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
package org.lanternpowered.server.data.persistence;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.data.persistence.DataTranslator;

import java.util.Optional;
import java.util.function.Function;

public interface DataTypeSerializerCollection {

    DataTypeSerializerCollection registerTypeSerializerFactory(Function<TypeToken<?>, Optional<DataTypeSerializer<?,?>>> factory);

    /**
     * Registers a {@link DataTranslator}.
     *
     * @param translator The translator to register
     */
    <T> DataTypeSerializerCollection registerTranslator(DataTranslator<T> translator);

    /**
     * Registers a {@link DataViewTypeSerializer} for the desired {@link TypeToken}.
     *
     * @param objectType The type of the object type being managed
     * @param serializer The serializer for the desired class object
     * @param <T> The type of object
     */
    <T, D> DataTypeSerializerCollection registerTypeSerializer(TypeToken<T> objectType, DataTypeSerializer<T, D> serializer);

    /**
     * Gets the desired {@link DataTypeSerializer} for the provided type token.
     *
     * @param objectType The type of the object
     * @param <T> The type of object
     * @param <D> The serialized data format
     * @return The data serializer, if available
     */
    <T, D> Optional<DataTypeSerializer<T, D>> getTypeSerializer(TypeToken<T> objectType);

    /**
     * Gets the desired {@link DataViewTypeSerializer} for the provided class.
     *
     * @param objectClass The class of the object
     * @param <T> The type of object
     * @param <D> The serialized data format
     * @return The data serializer, if available
     */
    default <T, D> Optional<DataTypeSerializer<T, D>> getTypeSerializer(Class<T> objectClass) {
        return this.getTypeSerializer(TypeToken.of(objectClass));
    }

    /**
     * Gets the desired {@link DataTranslator} for the provided type token.
     *
     * @param objectType The type of the object
     * @param <T> The type of object
     * @return The data serializer, if available
     */
    <T> Optional<DataTranslator<T>> getTranslator(TypeToken<T> objectType);

    /**
     * Gets the desired {@link DataTranslator} for the provided class.
     *
     * @param objectClass The class of the object
     * @param <T> The type of object
     * @return The data serializer, if available
     */
    default <T> Optional<DataTranslator<T>> getTranslator(Class<T> objectClass) {
        return this.getTranslator(TypeToken.of(objectClass));
    }
}
