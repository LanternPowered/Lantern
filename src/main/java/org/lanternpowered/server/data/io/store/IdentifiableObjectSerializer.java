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
import org.spongepowered.api.util.Identifiable;

import java.util.UUID;
import java.util.function.Function;

final class IdentifiableObjectSerializer<T extends Identifiable> implements ObjectSerializer<T> {

    private final ObjectStore<T> objectStore;
    private final Function<UUID, T> objectBuilder;

    IdentifiableObjectSerializer(ObjectStore<T> objectStore, Function<UUID, T> objectBuilder) {
        this.objectBuilder = objectBuilder;
        this.objectStore = objectStore;
    }

    @Override
    public T deserialize(DataView dataView) throws InvalidDataException {
        final UUID uniqueId;
        if (this.objectStore instanceof IdentifiableObjectStore) {
            uniqueId = ((IdentifiableObjectStore) this.objectStore).deserializeUniqueId(dataView);
        } else {
            uniqueId = UUID.randomUUID();
        }
        final T object = this.objectBuilder.apply(uniqueId);
        this.objectStore.deserialize(object, dataView);
        return object;
    }

    @Override
    public DataView serialize(T object) {
        final DataView dataView = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
        if (this.objectStore instanceof IdentifiableObjectStore) {
            ((IdentifiableObjectStore) this.objectStore).serializeUniqueId(dataView, object.getUniqueId());
        }
        this.objectStore.serialize(object, dataView);
        return dataView;
    }
}
