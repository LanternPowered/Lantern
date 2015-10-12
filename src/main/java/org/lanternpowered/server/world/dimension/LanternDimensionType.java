package org.lanternpowered.server.world.dimension;

import org.lanternpowered.server.catalog.LanternPluginCatalogType;
import org.spongepowered.api.world.Dimension;
import org.spongepowered.api.world.DimensionType;

public class LanternDimensionType extends LanternPluginCatalogType implements DimensionType {

    private final Class<? extends Dimension> dimensionClass;
    private final boolean keepSpawnLoaded;
    private final boolean waterEvaporates;
    private final boolean hasSky;
    private final int internalId;

    public LanternDimensionType(String name, int internalId, Class<? extends Dimension> dimensionClass,
            boolean keepSpawnLoaded, boolean waterEvaporates, boolean hasSky) {
        this("minecraft", name, internalId, dimensionClass, keepSpawnLoaded, waterEvaporates, hasSky);
    }

    public LanternDimensionType(String pluginId, String name, int internalId, Class<? extends Dimension> dimensionClass,
            boolean keepSpawnLoaded, boolean waterEvaporates, boolean hasSky) {
        super(pluginId, name);
        this.keepSpawnLoaded = keepSpawnLoaded;
        this.waterEvaporates = waterEvaporates;
        this.dimensionClass = dimensionClass;
        this.internalId = internalId;
        this.hasSky = hasSky;
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

    public boolean hasSky() {
        return this.hasSky;
    }

    public int getInternalId() {
        return this.internalId;
    }
}
