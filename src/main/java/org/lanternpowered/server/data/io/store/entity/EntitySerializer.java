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
package org.lanternpowered.server.data.io.store.entity;

import org.lanternpowered.server.data.io.store.IdentifiableObjectStore;
import org.lanternpowered.server.data.io.store.ObjectSerializer;
import org.lanternpowered.server.data.io.store.ObjectStore;
import org.lanternpowered.server.data.io.store.ObjectStoreRegistry;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.entity.LanternEntityType;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.entity.EntityType;

import java.util.UUID;

public class EntitySerializer implements ObjectSerializer<LanternEntity> {

    private static final DataQuery DATA_VERSION = DataQuery.of("DataVersion");
    private static final DataQuery ID = DataQuery.of("id");

    @Override
    public LanternEntity deserialize(DataView dataView) throws InvalidDataException {
        final String id = dataView.getString(ID).get();
        dataView.remove(ID);

        final LanternEntityType entityType = (LanternEntityType) Sponge.getRegistry()
                .getType(EntityType.class, CatalogKey.resolve(id))
                .orElseThrow(() -> new InvalidDataException("Unknown entity id: " + id));
        //noinspection unchecked
        final ObjectStore<LanternEntity> store = (ObjectStore) ObjectStoreRegistry.get().get(entityType.getEntityClass()).get();
        final UUID uniqueId;
        if (store instanceof IdentifiableObjectStore) {
            uniqueId = ((IdentifiableObjectStore) store).deserializeUniqueId(dataView);
        } else {
            uniqueId = UUID.randomUUID();
        }
        //noinspection unchecked
        final LanternEntity entity = (LanternEntity) entityType.constructEntity(uniqueId);
        store.deserialize(entity, dataView);
        return entity;
    }

    @Override
    public DataView serialize(LanternEntity object) {
        final DataView dataView = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
        dataView.set(ID, object.getType().getKey());
        //noinspection unchecked
        final ObjectStore<LanternEntity> store = (ObjectStore) ObjectStoreRegistry.get().get(object.getClass()).get();
        store.serialize(object, dataView);
        if (store instanceof IdentifiableObjectStore) {
            ((IdentifiableObjectStore) store).serializeUniqueId(dataView, object.getUniqueId());
        }
        return dataView;
    }
}
