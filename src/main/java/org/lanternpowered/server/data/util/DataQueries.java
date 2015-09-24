package org.lanternpowered.server.data.util;

import static org.spongepowered.api.data.DataQuery.of;

import org.spongepowered.api.data.DataQuery;

public final class DataQueries {

    private DataQueries() {
    }

    // General DataQueries
    public static final DataQuery UNSAFE_NBT = of("UnsafeData");
    public static final DataQuery DATA_MANIPULATORS = of("Data");
    public static final DataQuery DATA_CLASS = of("DataClass");
    public static final DataQuery INTERNAL_DATA = of("ManipulatorData");

    // Snapshots
    public static final DataQuery SNAPSHOT_WORLD_POSITION = of("Position");

    // Blocks
    public static final DataQuery BLOCK_STATE = of("BlockState");
    public static final DataQuery BLOCK_TYPE = of("BlockType");
    public static final DataQuery BLOCK_STATE_UNSAFE_META = of("UnsafeMeta");

}
