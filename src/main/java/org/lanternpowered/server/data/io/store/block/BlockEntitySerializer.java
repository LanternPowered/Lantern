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
package org.lanternpowered.server.data.io.store.block;

import org.lanternpowered.server.block.entity.LanternBlockEntity;
import org.lanternpowered.server.block.entity.LanternBlockEntityType;
import org.lanternpowered.server.data.io.store.ObjectSerializer;
import org.lanternpowered.server.data.io.store.ObjectStore;
import org.lanternpowered.server.data.io.store.ObjectStoreRegistry;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.entity.BlockEntityType;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.persistence.InvalidDataException;

public class BlockEntitySerializer implements ObjectSerializer<LanternBlockEntity> {

    private static final DataQuery ID = DataQuery.of("id");

    @Override
    public LanternBlockEntity deserialize(DataView dataView) throws InvalidDataException {
        final String id = dataView.getString(ID).get();
        dataView.remove(ID);

        final LanternBlockEntityType tileEntityType = (LanternBlockEntityType) Sponge.getRegistry()
                .getType(BlockEntityType.class, ResourceKey.resolve(id))
                .orElseThrow(() -> new InvalidDataException("Unknown block entity id: " + id));
        //noinspection unchecked
        final ObjectStore<LanternBlockEntity> store = (ObjectStore)
                ObjectStoreRegistry.get().get(tileEntityType.getBlockEntityType()).get();
        final LanternBlockEntity entity = tileEntityType.construct();
        store.deserialize(entity, dataView);
        return entity;
    }

    @Override
    public DataView serialize(LanternBlockEntity object) {
        final DataView dataView = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
        dataView.set(ID, object.getType().getKey());
        //noinspection unchecked
        final ObjectStore<LanternBlockEntity> store = (ObjectStore) ObjectStoreRegistry.get().get(object.getClass()).get();
        store.serialize(object, dataView);
        return dataView;
    }
}
