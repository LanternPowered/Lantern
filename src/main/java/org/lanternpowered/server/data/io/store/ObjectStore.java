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
package org.lanternpowered.server.data.io.store;

import org.spongepowered.api.data.persistence.DataView;

public interface ObjectStore<T> {

    /**
     * Deserializes a {@link DataView} and provides
     * the data to the object {@link T}.
     *
     * @param object The object
     * @param dataView The data view
     */
    void deserialize(T object, DataView dataView);

    /**
     * Serializes the object {@link T} and attaches the data
     * to the {@link DataView}.
     *
     * @param object The object
     * @param dataView The data view
     */
    void serialize(T object, DataView dataView);

}
