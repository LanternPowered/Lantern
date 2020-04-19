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
