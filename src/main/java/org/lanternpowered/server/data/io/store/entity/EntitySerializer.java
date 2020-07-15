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
package org.lanternpowered.server.data.io.store.entity;

import org.lanternpowered.server.data.io.store.IdentifiableObjectStore;
import org.lanternpowered.server.data.io.store.ObjectSerializer;
import org.lanternpowered.server.data.io.store.ObjectStore;
import org.lanternpowered.server.data.io.store.ObjectStoreRegistry;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.entity.LanternEntityType;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;
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
                .getType(EntityType.class, ResourceKey.resolve(id))
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
