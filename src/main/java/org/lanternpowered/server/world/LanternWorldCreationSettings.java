package org.lanternpowered.server.world;

import java.util.Collection;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.entity.player.gamemode.GameMode;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

public class LanternWorldCreationSettings implements WorldCreationSettings {

    @Override
    public String getWorldName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean loadOnStartup() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean doesKeepSpawnLoaded() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public long getSeed() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public GameMode getGameMode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GeneratorType getGeneratorType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<WorldGeneratorModifier> getGeneratorModifiers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean usesMapFeatures() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isHardcore() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean commandsAllowed() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean bonusChestEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public DimensionType getDimensionType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataContainer getGeneratorSettings() {
        // TODO Auto-generated method stub
        return null;
    }

}
