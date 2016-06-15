/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.Sets;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.type.world.GeneratorModifierRegistryModule;
import org.lanternpowered.server.world.dimension.LanternDimensionType;
import org.lanternpowered.server.world.portal.LanternPortalAgentType;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.registry.CatalogTypeAlreadyRegisteredException;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.PortalAgentType;
import org.spongepowered.api.world.PortalAgentTypes;
import org.spongepowered.api.world.SerializationBehavior;
import org.spongepowered.api.world.SerializationBehaviors;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

public final class LanternWorldArchetypeBuilder implements WorldArchetype.Builder {

    private GameMode gameMode;
    private Difficulty difficulty;
    private LanternPortalAgentType portalAgentType;
    @Nullable private LanternDimensionType<?> dimensionType;
    // If not specified, fall back to dimension default
    @Nullable private GeneratorType generatorType;
    private Collection<WorldGeneratorModifier> generatorModifiers;
    @Nullable private DataContainer generatorSettings;
    private SerializationBehavior serializationBehavior;

    @Nullable private Boolean keepsSpawnLoaded;
    @Nullable private Boolean waterEvaporates; // Non-sponge property
    @Nullable private Boolean allowPlayerRespawns; // Non-sponge property

    private int buildHeight; // Non-sponge property

    private boolean hardcore;
    private boolean enabled;
    private boolean loadsOnStartup;
    private boolean usesMapFeatures;
    private boolean generateBonusChest;
    private boolean commandsAllowed; // No builder method available
    private boolean pvpEnabled;
    private boolean generateSpawnOnLoad;

    private long seed;

    public LanternWorldArchetypeBuilder() {
        this.reset();
    }

    @Override
    public LanternWorldArchetypeBuilder from(WorldArchetype archetype) {
        final LanternWorldArchetype archetype0 = (LanternWorldArchetype)
                checkNotNull(archetype, "archetype");
        this.difficulty = archetype0.getDifficulty();
        this.hardcore = archetype0.isHardcore();
        this.enabled = archetype0.isEnabled();
        this.gameMode = archetype0.getGameMode();
        this.keepsSpawnLoaded = archetype0.doesKeepSpawnLoaded();
        this.usesMapFeatures = archetype0.usesMapFeatures();
        this.seed = archetype0.getSeed();
        this.generatorModifiers = archetype0.getGeneratorModifiers();
        this.dimensionType = archetype0.getDimensionType();
        this.generatorType = archetype0.getGeneratorType();
        this.generatorSettings = archetype0.getGeneratorSettings();
        this.generateBonusChest = archetype0.doesGenerateBonusChest();
        this.commandsAllowed = archetype0.areCommandsAllowed();
        this.waterEvaporates = archetype0.waterEvaporates();
        this.buildHeight = archetype0.getBuildHeight();
        this.allowPlayerRespawns = archetype0.allowPlayerRespawns();
        this.pvpEnabled = archetype0.isPVPEnabled();
        this.generateSpawnOnLoad = archetype0.doesGenerateSpawnOnLoad();
        this.portalAgentType = archetype0.getPortalAgentType();
        return this;
    }

    @Override
    public LanternWorldArchetypeBuilder from(WorldProperties properties) {
        final LanternWorldProperties properties0 = (LanternWorldProperties)
                checkNotNull(properties, "properties");
        this.difficulty = properties0.getDifficulty();
        this.hardcore = properties0.isHardcore();
        this.enabled = properties0.isEnabled();
        this.gameMode = properties0.getGameMode();
        this.keepsSpawnLoaded = properties0.doesKeepSpawnLoaded();
        this.usesMapFeatures = properties0.usesMapFeatures();
        this.seed = properties0.getSeed();
        this.generatorModifiers = properties0.getGeneratorModifiers();
        this.dimensionType = properties0.getDimensionType();
        this.generatorType = properties0.getGeneratorType();
        this.generatorSettings = properties0.getGeneratorSettings().copy();
        this.generateBonusChest = properties0.doesGenerateBonusChest();
        this.waterEvaporates = properties0.doesWaterEvaporate();
        this.buildHeight = properties0.getBuildHeight();
        this.pvpEnabled = properties0.isPVPEnabled();
        this.generateSpawnOnLoad = properties0.doesGenerateSpawnOnLoad();
        this.portalAgentType = properties0.getPortalAgentType();
        return this;
    }

    @Override
    public LanternWorldArchetypeBuilder enabled(boolean state) {
        this.enabled = state;
        return this;
    }

    @Override
    public LanternWorldArchetypeBuilder loadsOnStartup(boolean state) {
        this.loadsOnStartup = state;
        return this;
    }

    @Override
    public LanternWorldArchetypeBuilder keepsSpawnLoaded(boolean state) {
        this.keepsSpawnLoaded = state;
        return this;
    }

    @Override
    public LanternWorldArchetypeBuilder generateSpawnOnLoad(boolean state) {
        this.generateSpawnOnLoad = state;
        return this;
    }

    @Override
    public LanternWorldArchetypeBuilder seed(long seed) {
        this.seed = seed;
        return this;
    }

    @Override
    public LanternWorldArchetypeBuilder gameMode(GameMode gameMode) {
        this.gameMode = checkNotNull(gameMode, "gameMode");
        return this;
    }

    @Override
    public LanternWorldArchetypeBuilder generator(GeneratorType type) {
        this.generatorType = checkNotNull(type, "type");
        return this;
    }

    @Override
    public LanternWorldArchetypeBuilder generatorModifiers(WorldGeneratorModifier... modifiers) {
        checkNotNull(modifiers, "modifiers");
        Set<WorldGeneratorModifier> entries = Sets.newHashSet();
        GeneratorModifierRegistryModule registry = Lantern.getGame().getRegistry().getWorldGeneratorModifierRegistry();
        for (WorldGeneratorModifier modifier : modifiers) {
            checkNotNull(modifier, "modifier");
            checkState(registry.getById(modifier.getId()).isPresent(), "Modifier not registered: " + modifier.getId()
                        + " of type " + modifier.getClass().getName());
            entries.add(modifier);
        }
        this.generatorModifiers = entries;
        return this;
    }

    @Override
    public LanternWorldArchetypeBuilder dimension(DimensionType type) {
        this.dimensionType = (LanternDimensionType<?>) checkNotNull(type, "type");
        return this;
    }

    @Override
    public LanternWorldArchetypeBuilder difficulty(Difficulty difficulty) {
        this.difficulty = checkNotNull(difficulty, "difficulty");
        return this;
    }

    @Override
    public LanternWorldArchetypeBuilder usesMapFeatures(boolean enabled) {
        this.usesMapFeatures = enabled;
        return this;
    }

    @Override
    public LanternWorldArchetypeBuilder hardcore(boolean enabled) {
        this.hardcore = enabled;
        return this;
    }

    @Override
    public LanternWorldArchetypeBuilder generatorSettings(DataContainer settings) {
        this.generatorSettings = checkNotNull(settings, "settings");
        return this;
    }

    @Override
    public WorldArchetype.Builder portalAgent(PortalAgentType type) {
        this.portalAgentType = (LanternPortalAgentType) checkNotNull(type, "type");
        return this;
    }

    public LanternWorldArchetypeBuilder waterEvaporates(boolean evaporates) {
        this.waterEvaporates = evaporates;
        return this;
    }

    public LanternWorldArchetypeBuilder buildHeight(int buildHeight) {
        checkState(buildHeight <= 256, "the build height cannot be greater then 256");
        this.buildHeight = buildHeight;
        return this;
    }

    @Override
    public LanternWorldArchetypeBuilder pvp(boolean enabled) {
        this.pvpEnabled = enabled;
        return this;
    }

    @Override
    public LanternWorldArchetypeBuilder commandsAllowed(boolean state) {
        this.commandsAllowed = state;
        return this;
    }

    @Override
    public LanternWorldArchetypeBuilder generateBonusChest(boolean state) {
        this.generateBonusChest = state;
        return this;
    }

    @Override
    public LanternWorldArchetypeBuilder serializationBehavior(SerializationBehavior behavior) {
        this.serializationBehavior = checkNotNull(behavior, "behavior");
        return this;
    }

    @Override
    public LanternWorldArchetype build(String id, String name) throws IllegalArgumentException, CatalogTypeAlreadyRegisteredException {
        checkNotNull(id, "id");
        checkNotNull(name, "name");
        checkArgument(this.dimensionType != null, "Dimension type must be set");
        GeneratorType generatorType = this.generatorType;
        if (generatorType == null) {
            generatorType = this.dimensionType.getDefaultGeneratorType();
        }
        DataContainer generatorSettings = this.generatorSettings;
        if (generatorSettings == null) {
            generatorSettings = generatorType.getGeneratorSettings();
        }
        final boolean keepsSpawnLoaded = this.keepsSpawnLoaded == null ?
                this.dimensionType.doesKeepSpawnLoaded() : this.keepsSpawnLoaded;
        final boolean waterEvaporates = this.waterEvaporates == null ?
                this.dimensionType.doesWaterEvaporate() : this.waterEvaporates;
        final boolean allowPlayerRespawns = this.allowPlayerRespawns == null ?
                this.dimensionType.allowsPlayerRespawns() : this.allowPlayerRespawns;
        return new LanternWorldArchetype(id, name, this.gameMode, this.dimensionType, generatorType,
                this.generatorModifiers, generatorSettings, this.difficulty, this.serializationBehavior, this.portalAgentType,
                this.hardcore, this.enabled, this.loadsOnStartup, keepsSpawnLoaded, this.usesMapFeatures, this.pvpEnabled,
                this.generateBonusChest, this.commandsAllowed, waterEvaporates, allowPlayerRespawns, this.generateSpawnOnLoad,
                this.seed, this.buildHeight);
    }

    @Override
    public LanternWorldArchetypeBuilder reset() {
        this.usesMapFeatures = true;
        this.gameMode = GameModes.SURVIVAL;
        this.difficulty = Difficulties.NORMAL;
        this.portalAgentType = (LanternPortalAgentType) PortalAgentTypes.DEFAULT;
        this.hardcore = false;
        this.keepsSpawnLoaded = null;
        this.loadsOnStartup = false;
        this.generateSpawnOnLoad = true;
        this.enabled = true;
        this.generateBonusChest = false;
        this.commandsAllowed = true;
        this.dimensionType = null;
        this.generatorModifiers = Collections.emptySet();
        this.seed = new Random().nextLong();
        this.generatorType = null;
        this.generatorSettings = null;
        this.waterEvaporates = null;
        this.buildHeight = 256;
        this.serializationBehavior = SerializationBehaviors.AUTOMATIC;
        return this;
    }
}
