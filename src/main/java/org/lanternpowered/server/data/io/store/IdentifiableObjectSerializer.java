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
