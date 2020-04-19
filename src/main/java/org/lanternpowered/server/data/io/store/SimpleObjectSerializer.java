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

import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.function.Supplier;

final class SimpleObjectSerializer<T> implements ObjectSerializer<T> {

    private final ObjectStore<T> objectStore;
    private final Supplier<T> objectBuilder;

    SimpleObjectSerializer(ObjectStore<T> objectStore, Supplier<T> objectBuilder) {
        this.objectBuilder = objectBuilder;
        this.objectStore = objectStore;
    }

    @Override
    public T deserialize(DataView dataView) throws InvalidDataException {
        final T object = this.objectBuilder.get();
        this.objectStore.deserialize(object, dataView);
        return object;
    }

    @Override
    public DataView serialize(T object) {
        final DataView dataView = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
        this.objectStore.serialize(object, dataView);
        return dataView;
    }
}
