/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
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
        return getTypeSerializer(TypeToken.of(objectClass));
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
        return getTranslator(TypeToken.of(objectClass));
    }
}
