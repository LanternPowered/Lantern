package org.lanternpowered.server.world.dimension;

import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.gen.LanternGeneratorType;
import org.spongepowered.api.service.permission.context.Context;
import org.spongepowered.api.world.Dimension;
import org.spongepowered.api.world.DimensionType;

public class LanternDimension implements Dimension {

    private final String name;
    private final LanternDimensionType dimensionType;
    private final LanternWorld world;
    private final int buildHeight;

    private volatile Context dimContext;

    private boolean allowPlayerRespawns = true;
    private boolean waterEvaporates = false;

    public LanternDimension(LanternWorld world, String name, LanternDimensionType dimensionType,
            int buildHeight) {
        this.dimensionType = dimensionType;
        this.buildHeight = buildHeight;
        this.world = world;
        this.name = name;
    }

    @Override
    public Context getContext() {
        if (this.dimContext == null) {
            this.dimContext = new Context(Context.DIMENSION_KEY, this.getName());
        }
        return this.dimContext;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean allowsPlayerRespawns() {
        return this.allowPlayerRespawns;
    }

    @Override
    public void setAllowsPlayerRespawns(boolean allow) {
        this.allowPlayerRespawns = allow;
    }

    @Override
    public int getMinimumSpawnHeight() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean doesWaterEvaporate() {
        return this.waterEvaporates;
    }

    @Override
    public void setWaterEvaporates(boolean evaporates) {
        this.waterEvaporates = evaporates;
    }

    @Override
    public boolean hasSky() {
        return this.dimensionType.hasSky();
    }

    @Override
    public DimensionType getType() {
        return this.dimensionType;
    }

    @Override
    public int getHeight() {
        return this.getGeneratorType().getGeneratorHeight();
    }

    @Override
    public int getBuildHeight() {
        return this.buildHeight;
    }

    @Override
    public LanternGeneratorType getGeneratorType() {
        // TODO: Is this supposed to return this?
        return this.world.getProperties().getGeneratorType();
    }
}
