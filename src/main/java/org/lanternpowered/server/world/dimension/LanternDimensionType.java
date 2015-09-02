package org.lanternpowered.server.world.dimension;

import org.spongepowered.api.world.Dimension;
import org.spongepowered.api.world.DimensionType;

public class LanternDimensionType implements DimensionType {

    private final String name;
    private final Class<? extends Dimension> dimensionClass;
    private final boolean keepSpawnLoaded;
    private final boolean waterEvaporates;
    private final int internalId;

    public LanternDimensionType(String name, int internalId, Class<? extends Dimension> dimensionClass,
            boolean keepSpawnLoaded, boolean waterEvaporates) {
        this.keepSpawnLoaded = keepSpawnLoaded;
        this.waterEvaporates = waterEvaporates;
        this.dimensionClass = dimensionClass;
        this.internalId = internalId;
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

    public boolean doesWaterEvaporate() {
        return this.waterEvaporates;
    }

    public int getInternalId() {
        return this.internalId;
    }
}
