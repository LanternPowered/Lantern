package org.lanternpowered.server.world;

import java.util.Collection;

import org.lanternpowered.server.world.dimension.LanternDimensionType;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.TeleporterAgent;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

public class LanternWorldCreationSettings implements WorldCreationSettings {

    private final String name;
    private final GameMode gameMode;
    private final LanternDimensionType dimensionType;
    private final GeneratorType generatorType;
    private final Collection<WorldGeneratorModifier> generatorModifiers;
    private final DataContainer generatorSettings;
    private final TeleporterAgent teleporterAgent;

    private final boolean hardcore;
    private final boolean enabled;
    private final boolean loadsOnStartup;
    private final boolean keepsSpawnLoaded;
    private final boolean usesMapFeatures;
    private final boolean bonusChestEnabled;
    private final boolean commandsAllowed;
    private final boolean waterEvaporates;

    private final int buildHeight;
    private final long seed;

    LanternWorldCreationSettings(String name, GameMode gameMode, LanternDimensionType dimensionType, GeneratorType generatorType,
            Collection<WorldGeneratorModifier> generatorModifiers, DataContainer generatorSettings, TeleporterAgent teleporterAgent, 
            boolean hardcore, boolean enabled, boolean loadsOnStartup, boolean keepsSpawnLoaded, boolean usesMapFeatures,
            boolean bonusChestEnabled, boolean commandsAllowed, boolean waterEvaporates, long seed, int buildHeight) {
        this.generatorModifiers = generatorModifiers;
        this.generatorSettings = generatorSettings;
        this.bonusChestEnabled = bonusChestEnabled;
        this.keepsSpawnLoaded = keepsSpawnLoaded;
        this.usesMapFeatures = usesMapFeatures;
        this.commandsAllowed = commandsAllowed;
        this.teleporterAgent = teleporterAgent;
        this.waterEvaporates = waterEvaporates;
        this.loadsOnStartup = loadsOnStartup;
        this.dimensionType = dimensionType;
        this.generatorType = generatorType;
        this.buildHeight = buildHeight;
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

    public boolean waterEvaporates() {
        return this.waterEvaporates;
    }

    @Override
    public boolean bonusChestEnabled() {
        return this.bonusChestEnabled;
    }

    @Override
    public LanternDimensionType getDimensionType() {
        return this.dimensionType;
    }

    @Override
    public DataContainer getGeneratorSettings() {
        return this.generatorSettings;
    }

    public TeleporterAgent getTeleporterAgent() {
        return this.teleporterAgent;
    }

    public int getBuildHeight() {
        return this.buildHeight;
    }
}
