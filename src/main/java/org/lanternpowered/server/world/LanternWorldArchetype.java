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

import org.lanternpowered.server.world.dimension.LanternDimensionType;
import org.lanternpowered.server.world.portal.LanternPortalAgentType;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.SerializationBehavior;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

import java.util.Collection;

public final class LanternWorldArchetype implements WorldArchetype {

    private final String id;
    private final String name;
    private final GameMode gameMode;
    private final Difficulty difficulty;
    private final LanternDimensionType<?> dimensionType;
    private final GeneratorType generatorType;
    private final Collection<WorldGeneratorModifier> generatorModifiers;
    private final DataContainer generatorSettings;
    private final SerializationBehavior serializationBehavior;
    private final LanternPortalAgentType portalAgentType;

    private final boolean hardcore;
    private final boolean enabled;
    private final boolean loadsOnStartup;
    private final boolean keepsSpawnLoaded;
    private final boolean usesMapFeatures;
    private final boolean generateBonusChest;
    private final boolean commandsAllowed;
    private final boolean waterEvaporates;
    private final boolean allowPlayerRespawns;
    private final boolean pvpEnabled;
    private final boolean generateSpawnOnLoad;

    private final int buildHeight;
    private final long seed;

    LanternWorldArchetype(String id, String name, GameMode gameMode, LanternDimensionType<?> dimensionType, GeneratorType generatorType,
            Collection<WorldGeneratorModifier> generatorModifiers, DataContainer generatorSettings, Difficulty difficulty,
            SerializationBehavior serializationBehavior, LanternPortalAgentType portalAgentType, boolean hardcore, boolean enabled,
            boolean loadsOnStartup, boolean keepsSpawnLoaded, boolean usesMapFeatures, boolean pvpEnabled, boolean generateBonusChest,
            boolean commandsAllowed, boolean waterEvaporates, boolean allowPlayerRespawns, boolean generateSpawnOnLoad, long seed, int buildHeight) {
        this.serializationBehavior = serializationBehavior;
        this.generateSpawnOnLoad = generateSpawnOnLoad;
        this.allowPlayerRespawns = allowPlayerRespawns;
        this.generatorModifiers = generatorModifiers;
        this.generatorSettings = generatorSettings;
        this.generateBonusChest = generateBonusChest;
        this.keepsSpawnLoaded = keepsSpawnLoaded;
        this.usesMapFeatures = usesMapFeatures;
        this.portalAgentType = portalAgentType;
        this.commandsAllowed = commandsAllowed;
        this.waterEvaporates = waterEvaporates;
        this.loadsOnStartup = loadsOnStartup;
        this.dimensionType = dimensionType;
        this.generatorType = generatorType;
        this.buildHeight = buildHeight;
        this.pvpEnabled = pvpEnabled;
        this.difficulty = difficulty;
        this.hardcore = hardcore;
        this.gameMode = gameMode;
        this.enabled = enabled;
        this.name = name;
        this.seed = seed;
        this.id = id;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
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
    public boolean doesGenerateSpawnOnLoad() {
        return this.generateSpawnOnLoad;
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
    public boolean areCommandsAllowed() {
        return this.commandsAllowed;
    }

    @Override
    public boolean doesGenerateBonusChest() {
        return this.generateBonusChest;
    }

    public boolean waterEvaporates() {
        return this.waterEvaporates;
    }

    @Override
    public LanternDimensionType<?> getDimensionType() {
        return this.dimensionType;
    }

    @Override
    public LanternPortalAgentType getPortalAgentType() {
        return this.portalAgentType;
    }

    @Override
    public DataContainer getGeneratorSettings() {
        return this.generatorSettings.copy();
    }

    @Override
    public SerializationBehavior getSerializationBehavior() {
        return this.serializationBehavior;
    }

    public int getBuildHeight() {
        return this.buildHeight;
    }

    public boolean allowPlayerRespawns() {
        return this.allowPlayerRespawns;
    }

    @Override
    public boolean isPVPEnabled() {
        return this.pvpEnabled;
    }

}
