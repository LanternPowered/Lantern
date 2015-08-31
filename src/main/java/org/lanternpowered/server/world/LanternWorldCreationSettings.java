package org.lanternpowered.server.world;

import java.util.Collection;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.entity.player.gamemode.GameMode;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

public class LanternWorldCreationSettings implements WorldCreationSettings {

    private final String name;
    private final GameMode gameMode;
    private final DimensionType dimensionType;
    private final GeneratorType generatorType;
    private final Collection<WorldGeneratorModifier> generatorModifiers;
    private final DataContainer generatorSettings;

    private final boolean hardcore;
    private final boolean enabled;
    private final boolean loadsOnStartup;
    private final boolean keepsSpawnLoaded;
    private final boolean usesMapFeatures;
    private final boolean bonusChestEnabled;
    private final boolean commandsAllowed;

    private final long seed;

    LanternWorldCreationSettings(String name, GameMode gameMode, DimensionType dimensionType, GeneratorType generatorType,
            Collection<WorldGeneratorModifier> generatorModifiers, DataContainer generatorSettings, boolean hardcore,
            boolean enabled, boolean loadsOnStartup, boolean keepsSpawnLoaded, boolean usesMapFeatures,
            boolean bonusChestEnabled, boolean commandsAllowed, long seed) {
        this.generatorModifiers = generatorModifiers;
        this.generatorSettings = generatorSettings;
        this.bonusChestEnabled = bonusChestEnabled;
        this.keepsSpawnLoaded = keepsSpawnLoaded;
        this.usesMapFeatures = usesMapFeatures;
        this.commandsAllowed = commandsAllowed;
        this.loadsOnStartup = loadsOnStartup;
        this.dimensionType = dimensionType;
        this.generatorType = generatorType;
        this.hardcore = hardcore;
        this.gameMode = gameMode;
        this.enabled = enabled;
        this.name = name;
        this.seed = seed;
    }

    @Override
    public String getWorldName() {
        return this.name;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean loadOnStartup() {
        return this.loadsOnStartup;
    }

    @Override
    public boolean doesKeepSpawnLoaded() {
        return this.keepsSpawnLoaded;
    }

    @Override
    public long getSeed() {
        return this.seed;
    }

    @Override
    public GameMode getGameMode() {
        return this.gameMode;
    }

    @Override
    public GeneratorType getGeneratorType() {
        return this.generatorType;
    }

    @Override
    public Collection<WorldGeneratorModifier> getGeneratorModifiers() {
        return this.generatorModifiers;
    }

    @Override
    public boolean usesMapFeatures() {
        return this.usesMapFeatures;
    }

    @Override
    public boolean isHardcore() {
        return this.hardcore;
    }

    @Override
    public boolean commandsAllowed() {
        return this.commandsAllowed;
    }

    @Override
    public boolean bonusChestEnabled() {
        return this.bonusChestEnabled;
    }

    @Override
    public DimensionType getDimensionType() {
        return this.dimensionType;
    }

    @Override
    public DataContainer getGeneratorSettings() {
        return this.generatorSettings;
    }
}
