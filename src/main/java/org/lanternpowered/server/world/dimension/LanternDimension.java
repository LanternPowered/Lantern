package org.lanternpowered.server.world.dimension;

import org.spongepowered.api.service.permission.context.Context;
import org.spongepowered.api.world.Dimension;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;

public class LanternDimension implements Dimension {

    @Override
    public Context getContext() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean allowsPlayerRespawns() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setAllowsPlayerRespawns(boolean allow) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getMinimumSpawnHeight() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean doesWaterEvaporate() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setWaterEvaporates(boolean evaporates) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean hasSky() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public DimensionType getType() {
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
    }

}
