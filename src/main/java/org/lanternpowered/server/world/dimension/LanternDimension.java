package org.lanternpowered.server.world.dimension;

import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.service.permission.context.Context;
import org.spongepowered.api.world.Dimension;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;

public class LanternDimension implements Dimension {

    private final String name;
    private final DimensionType dimensionType;
    private final LanternWorld world;
    private final boolean hasSky;

    private volatile Context dimContext;

    private boolean allowPlayerRespawns = true;
    private boolean waterEvaporates = false;

    public LanternDimension(LanternWorld world, String name, DimensionType dimensionType,
            boolean hasSky) {
        this.dimensionType = dimensionType;
        this.hasSky = hasSky;
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
        return this.hasSky;
    }

    @Override
    public DimensionType getType() {
        return this.dimensionType;
    }

    @Override
    public int getHeight() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getBuildHeight() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public GeneratorType getGeneratorType() {
        // TODO: Is this supposed to return this?
        return this.world.getProperties().getGeneratorType();
    }
}
