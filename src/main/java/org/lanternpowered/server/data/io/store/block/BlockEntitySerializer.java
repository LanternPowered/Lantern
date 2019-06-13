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
package org.lanternpowered.server.data.io.store.block;

import org.lanternpowered.server.block.entity.LanternBlockEntity;
import org.lanternpowered.server.block.entity.LanternBlockEntityType;
import org.lanternpowered.server.data.io.store.ObjectSerializer;
import org.lanternpowered.server.data.io.store.ObjectStore;
import org.lanternpowered.server.data.io.store.ObjectStoreRegistry;
import org.spongepowered.api.CatalogKey;
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
                .getType(BlockEntityType.class, CatalogKey.resolve(id))
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
