package org.lanternpowered.server.world;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.Set;

import org.lanternpowered.server.catalog.CatalogTypeRegistry;
import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.entity.player.gamemode.GameMode;
import org.spongepowered.api.entity.player.gamemode.GameModes;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldBuilder;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;

public class LanternWorldBuilder implements WorldBuilder {

    private static final Random RANDOM = new Random();

    private final LanternGame game;

    private String name;
    private GameMode gameMode;
    private DimensionType dimensionType;
    private GeneratorType generatorType;
    private Collection<WorldGeneratorModifier> generatorModifiers;
    private DataContainer generatorSettings;
    private Boolean keepsSpawnLoaded;

    private boolean hardcore;
    private boolean enabled;
    private boolean loadsOnStartup;
    private boolean usesMapFeatures;
    private boolean bonusChestEnabled; // No builder method available
    private boolean commandsAllowed; // No builder method available

    private long seed;

    public LanternWorldBuilder(LanternGame game) {
        this.game = game;
        this.reset();
    }

    @Override
    public WorldBuilder fill(WorldCreationSettings settings) {
        checkNotNull(settings, "settings");
        this.hardcore = settings.isHardcore();
        this.enabled = settings.isEnabled();
        this.gameMode = settings.getGameMode();
        this.keepsSpawnLoaded = settings.doesKeepSpawnLoaded();
        this.usesMapFeatures = settings.usesMapFeatures();
        this.seed = settings.getSeed();
        this.generatorModifiers = settings.getGeneratorModifiers();
        this.name = settings.getWorldName();
        this.dimensionType = settings.getDimensionType();
        this.generatorType = settings.getGeneratorType();
        this.bonusChestEnabled = settings.bonusChestEnabled();
        this.commandsAllowed = settings.commandsAllowed();
        return this;
    }

    @Override
    public WorldBuilder fill(WorldProperties properties) {
        checkNotNull(properties, "properties");
        this.hardcore = properties.isHardcore();
        this.enabled = properties.isEnabled();
        this.gameMode = properties.getGameMode();
        this.keepsSpawnLoaded = properties.doesKeepSpawnLoaded();
        this.usesMapFeatures = properties.usesMapFeatures();
        this.seed = properties.getSeed();
        this.generatorModifiers = properties.getGeneratorModifiers();
        this.name = properties.getWorldName();
        this.dimensionType = properties.getDimensionType();
        this.generatorType = properties.getGeneratorType();
        return this;
    }

    @Override
    public WorldBuilder name(String name) {
        this.name = checkNotNull(name, "name");
        return this;
    }

    @Override
    public WorldBuilder enabled(boolean state) {
        this.enabled = state;
        return null;
    }

    @Override
    public WorldBuilder loadsOnStartup(boolean state) {
        this.loadsOnStartup = state;
        return this;
    }

    @Override
    public WorldBuilder keepsSpawnLoaded(boolean state) {
        this.keepsSpawnLoaded = state;
        return this;
    }

    @Override
    public WorldBuilder seed(long seed) {
        this.seed = seed;
        return this;
    }

    @Override
    public WorldBuilder gameMode(GameMode gameMode) {
        this.gameMode = checkNotNull(gameMode, "gameMode");
        return this;
    }

    @Override
    public WorldBuilder generator(GeneratorType type) {
        this.generatorType = checkNotNull(type, "type");
        return this;
    }

    @Override
    public WorldBuilder generatorModifiers(WorldGeneratorModifier... modifiers) {
        checkNotNull(modifiers, "modifiers");
        Set<WorldGeneratorModifier> entries = Sets.newHashSet();
        CatalogTypeRegistry<WorldGeneratorModifier> registry = this.game.getRegistry()
                .getWorldGeneratorModifierRegistry();
        for (WorldGeneratorModifier modifier : modifiers) {
            checkNotNull(modifier, "modifier");
            checkState(registry.has(modifier), "Modifier not registered: " + modifier.getId()
                        + " of type " + modifier.getClass().getName());
            entries.add(modifier);
        }
        this.generatorModifiers = entries;
        return this;
    }

    @Override
    public WorldBuilder dimensionType(DimensionType type) {
        this.dimensionType = checkNotNull(type, "type");
        return this;
    }

    @Override
    public WorldBuilder usesMapFeatures(boolean enabled) {
        this.usesMapFeatures = enabled;
        return this;
    }

    @Override
    public WorldBuilder hardcore(boolean enabled) {
        this.hardcore = enabled;
        return this;
    }

    @Override
    public WorldBuilder generatorSettings(DataContainer settings) {
        this.generatorSettings = checkNotNull(settings, "settings");
        return this;
    }

    @Override
    public WorldBuilder reset() {
        this.usesMapFeatures = true;
        this.gameMode = GameModes.SURVIVAL;
        this.hardcore = false;
        this.keepsSpawnLoaded = false;
        this.loadsOnStartup = false;
        this.enabled = true;
        this.bonusChestEnabled = false;
        this.commandsAllowed = true;
        this.name = null;
        this.generatorModifiers = Collections.emptySet();
        this.seed = RANDOM.nextLong();
        this.dimensionType = null;
        this.generatorType = null;
        this.generatorSettings = null;
        return null;
    }

    @Override
    public Optional<World> build() throws IllegalStateException {
        Optional<WorldProperties> worldProperties = this.game.getServer().createWorld(this.buildSettings());
        if (worldProperties.isPresent()) {
            return this.game.getServer().loadWorld(worldProperties.get());
        }
        return Optional.absent();
    }

    @Override
    public WorldCreationSettings buildSettings() throws IllegalStateException {
        checkState(this.name != null, "name is not set");
        checkState(this.dimensionType != null, "dimensionType is not set");
        checkState(this.generatorType != null, "generatorType is not set");
        DataContainer generatorSettings = this.generatorSettings;
        if (generatorSettings == null) {
            generatorSettings = this.generatorType.getGeneratorSettings();
        }
        boolean keepsSpawnLoaded = this.keepsSpawnLoaded;
        if (this.keepsSpawnLoaded == null) {
            keepsSpawnLoaded = this.dimensionType.doesKeepSpawnLoaded();
        }
        return new LanternWorldCreationSettings(this.name, this.gameMode, this.dimensionType, this.generatorType,
                this.generatorModifiers, generatorSettings, this.hardcore, this.enabled, this.loadsOnStartup,
                keepsSpawnLoaded, this.usesMapFeatures, this.bonusChestEnabled, this.commandsAllowed, this.seed);
    }
}
