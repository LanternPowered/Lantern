package org.lanternpowered.server.world.dimension;

import org.spongepowered.api.world.Dimension;
import org.spongepowered.api.world.DimensionType;

public class LanternDimensionType implements DimensionType {

    private final String name;
    private final Class<? extends Dimension> dimensionClass;
    private final boolean keepSpawnLoaded;

    public LanternDimensionType(String name, Class<? extends Dimension> dimensionClass,
            boolean keepSpawnLoaded) {
        this.keepSpawnLoaded = keepSpawnLoaded;
        this.dimensionClass = dimensionClass;
        this.name = name;
    }

    @Override
    public String getId() {
        return this.name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean doesKeepSpawnLoaded() {
        return this.keepSpawnLoaded;
    }

    @Override
    public Class<? extends Dimension> getDimensionClass() {
        return this.dimensionClass;
    }
}
