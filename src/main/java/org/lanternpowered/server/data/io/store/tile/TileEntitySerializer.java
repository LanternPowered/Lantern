/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.data.io.store.tile;

import org.lanternpowered.server.block.tile.LanternTileEntity;
import org.lanternpowered.server.block.tile.LanternTileEntityType;
import org.lanternpowered.server.data.io.store.ObjectSerializer;
import org.lanternpowered.server.data.io.store.ObjectStore;
import org.lanternpowered.server.data.io.store.ObjectStoreRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.TileEntityType;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.HashMap;
import java.util.Map;

public class TileEntitySerializer implements ObjectSerializer<LanternTileEntity> {

    private static final DataQuery ID = DataQuery.of("id");

    @Override
    public LanternTileEntity deserialize(DataView dataView) throws InvalidDataException {
        final String id = fixTileId(dataView, dataView.getString(ID).get());
        dataView.remove(ID);

        final LanternTileEntityType tileEntityType = (LanternTileEntityType) Sponge.getRegistry().getType(TileEntityType.class, id).orElseThrow(
                () -> new InvalidDataException("Unknown tile entity id: " + id));
        //noinspection unchecked
        final ObjectStore<LanternTileEntity> store = (ObjectStore)
                ObjectStoreRegistry.get().get(tileEntityType.getTileEntityType()).get();
        //noinspection unchecked
        final LanternTileEntity entity = (LanternTileEntity) tileEntityType.getTileEntityConstructor().get();
        store.deserialize(entity, dataView);
        return entity;
    }

    @Override
    public DataView serialize(LanternTileEntity object) {
        final DataView dataView = new MemoryDataContainer(DataView.SafetyMode.NO_DATA_CLONED);
        dataView.set(ID, object.getType().getId());
        //noinspection unchecked
        final ObjectStore<LanternTileEntity> store = (ObjectStore) ObjectStoreRegistry.get().get(object.getClass()).get();
        store.serialize(object, dataView);
        return dataView;
    }

    private static final Map<String, String> OLD_TO_NEW_ID_MAPPINGS = new HashMap<>();

    private static void put(String newId, String oldId) {
        OLD_TO_NEW_ID_MAPPINGS.put(oldId, newId);
    }

    static {
        put("minecraft:banner", "Banner");
        put("minecraft:beacon", "Beacon");
        put("minecraft:brewing_stand", "Cauldron");
        put("minecraft:chest", "Chest");
        put("minecraft:command_block", "Control");
        put("minecraft:comparator", "Comparator");
        put("minecraft:daylight_detector", "DLDetector");
        put("minecraft:dispenser", "Trap");
        put("minecraft:dropper", "Dropper");
        put("minecraft:enchanting_table", "EnchantTable");
        put("minecraft:end_gateway", "EndGateway");
        put("minecraft:end_portal", "Airportal");
        put("minecraft:ender_chest", "EnderChest");
        put("minecraft:flower_pot", "FlowerPot");
        put("minecraft:furnace", "Furnace");
        put("minecraft:hopper", "Hopper");
        put("minecraft:jukebox", "RecordPlayer");
        put("minecraft:mob_spawner", "MobSpawner");
        put("minecraft:noteblock", "Music");
        put("minecraft:piston", "Piston");
        put("minecraft:sign", "Sign");
        put("minecraft:skull", "Skull");
        put("minecraft:structure_block", "Structure");
    }

    private static String fixTileId(DataView dataView, String id) {
        final String id1 = OLD_TO_NEW_ID_MAPPINGS.get(id);
        return id1 == null ? id : id1;
    }
}
