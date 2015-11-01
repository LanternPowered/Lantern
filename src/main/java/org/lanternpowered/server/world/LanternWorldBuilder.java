/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.world;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import org.lanternpowered.server.catalog.CatalogTypeRegistry;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.world.dimension.LanternDimensionType;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.TeleporterAgent;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldBuilder;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import com.google.common.collect.Sets;

public class LanternWorldBuilder implements WorldBuilder {

    private static final Random RANDOM = new Random();

    private final LanternGame game;

    private String name;
    private GameMode gameMode;
    private LanternDimensionType dimensionType;
    private GeneratorType generatorType;
    private Collection<WorldGeneratorModifier> generatorModifiers;
    private DataContainer generatorSettings;
    private TeleporterAgent teleporterAgent;

    @Nullable private Boolean keepsSpawnLoaded;
    @Nullable private Boolean waterEvaporates; // Non-sponge property

    private int buildHeight; // Non-sponge property

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
    public LanternWorldBuilder fill(WorldCreationSettings settings) {
        LanternWorldCreationSettings settings0 = (LanternWorldCreationSettings) checkNotNull(settings, "settings");
        this.hardcore = settings0.isHardcore();
        this.enabled = settings0.isEnabled();
        this.gameMode = settings0.getGameMode();
        this.keepsSpawnLoaded = settings0.doesKeepSpawnLoaded();
        this.usesMapFeatures = settings0.usesMapFeatures();
        this.seed = settings0.getSeed();
        this.generatorModifiers = settings0.getGeneratorModifiers();
        this.name = settings0.getWorldName();
        this.dimensionType = settings0.getDimensionType();
        this.generatorType = settings0.getGeneratorType();
        this.bonusChestEnabled = settings0.bonusChestEnabled();
        this.commandsAllowed = settings0.commandsAllowed();
        this.teleporterAgent = settings0.getTeleporterAgent();
        this.waterEvaporates = settings0.waterEvaporates();
        this.buildHeight = settings0.getBuildHeight();
        return this;
    }

    @Override
    public LanternWorldBuilder fill(WorldProperties properties0) {
        LanternWorldProperties properties = (LanternWorldProperties) checkNotNull(properties0, "properties");
        this.hardcore = properties.isHardcore();
        this.enabled = properties.isEnabled();
        this.gameMode = properties.getGameMode();
        this.keepsSpawnLoaded = properties.doesKeepSpawnLoaded();
        this.usesMapFeatures = properties.usesMapFeatures();
        this.seed = properties.getSeed();
        this.generatorModifiers = properties.getGeneratorModifiers();
        this.name = properties.getWorldName();
        this.dimensionType = (LanternDimensionType) properties.getDimensionType();
        this.generatorType = properties.getGeneratorType();
        if (properties.creationSettings != null) {
            this.waterEvaporates = properties.waterEvaporates;
            this.buildHeight = properties.buildHeight;
        } else {
            
        }
        return this;
    }

    @Override
    public LanternWorldBuilder name(String name) {
        this.name = checkNotNull(name, "name");
        return this;
    }

    @Override
    public LanternWorldBuilder enabled(boolean state) {
        this.enabled = state;
        return null;
    }

    @Override
    public LanternWorldBuilder loadsOnStartup(boolean state) {
        this.loadsOnStartup = state;
        return this;
    }

    @Override
    public LanternWorldBuilder keepsSpawnLoaded(boolean state) {
        this.keepsSpawnLoaded = state;
        return this;
    }

    @Override
    public LanternWorldBuilder seed(long seed) {
        this.seed = seed;
        return this;
    }

    @Override
    public LanternWorldBuilder gameMode(GameMode gameMode) {
        this.gameMode = checkNotNull(gameMode, "gameMode");
        return this;
    }

    @Override
    public LanternWorldBuilder generator(GeneratorType type) {
        this.generatorType = checkNotNull(type, "type");
        return this;
    }

    @Override
    public LanternWorldBuilder generatorModifiers(WorldGeneratorModifier... modifiers) {
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
    public LanternWorldBuilder dimensionType(DimensionType type) {
        this.dimensionType = (LanternDimensionType) checkNotNull(type, "type");
        return this;
    }

    @Override
    public LanternWorldBuilder usesMapFeatures(boolean enabled) {
        this.usesMapFeatures = enabled;
        return this;
    }

    @Override
    public LanternWorldBuilder hardcore(boolean enabled) {
        this.hardcore = enabled;
        return this;
    }

    @Override
    public LanternWorldBuilder generatorSettings(DataContainer settings) {
        this.generatorSettings = checkNotNull(settings, "settings");
        return this;
    }

    @Override
    public LanternWorldBuilder teleporterAgent(TeleporterAgent agent) {
        this.teleporterAgent = checkNotNull(agent, "agent");
        return this;
    }

    public LanternWorldBuilder waterEvaporates(boolean evaporates) {
        this.waterEvaporates = evaporates;
        return this;
    }

    public LanternWorldBuilder buildHeight(int buildHeight) {
        checkState(buildHeight <= 256, "the build height cannot be greater then 256");
        this.buildHeight = buildHeight;
        return this;
    }

    @Override
    public LanternWorldBuilder reset() {
        this.usesMapFeatures = true;
        this.gameMode = GameModes.SURVIVAL;
        this.hardcore = false;
        this.keepsSpawnLoaded = null;
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
        this.waterEvaporates = null;
        this.buildHeight = 256;
        // This teleporter agent won't return anything useful
        this.teleporterAgent = new LanternTeleporterAgent();
        return null;
    }

    @Override
    public Optional<World> build() throws IllegalStateException {
        Optional<WorldProperties> worldProperties = this.game.getServer().createWorld(this.buildSettings());
        if (worldProperties.isPresent()) {
            return this.game.getServer().loadWorld(worldProperties.get());
        }
        return Optional.empty();
    }

    @Override
    public LanternWorldCreationSettings buildSettings() throws IllegalStateException {
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
        boolean waterEvaporates = this.waterEvaporates;
        if (this.waterEvaporates == null) {
            waterEvaporates = this.dimensionType.doesWaterEvaporate();
        }
        return new LanternWorldCreationSettings(this.name, this.gameMode, this.dimensionType, this.generatorType,
                this.generatorModifiers, generatorSettings, this.teleporterAgent, this.hardcore, this.enabled,
                this.loadsOnStartup, keepsSpawnLoaded, this.usesMapFeatures, this.bonusChestEnabled, this.commandsAllowed,
                waterEvaporates, this.seed, this.buildHeight);
    }
}
