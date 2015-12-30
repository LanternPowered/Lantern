/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.TeleporterAgent;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

import java.util.Collection;

public final class LanternWorldCreationSettings implements WorldCreationSettings {

    private final String name;
    private final GameMode gameMode;
    private final Difficulty difficulty;
    private final LanternDimensionType<?> dimensionType;
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
    private final boolean allowPlayerRespawns;
    private final boolean pvpEnabled;

    private final int buildHeight;
    private final long seed;

    LanternWorldCreationSettings(String name, GameMode gameMode, LanternDimensionType<?> dimensionType, GeneratorType generatorType,
            Collection<WorldGeneratorModifier> generatorModifiers, DataContainer generatorSettings, TeleporterAgent teleporterAgent, 
            Difficulty difficulty, boolean hardcore, boolean enabled, boolean loadsOnStartup, boolean keepsSpawnLoaded, boolean usesMapFeatures,
            boolean pvpEnabled, boolean bonusChestEnabled, boolean commandsAllowed, boolean waterEvaporates, boolean allowPlayerRespawns,
            long seed, int buildHeight) {
        this.allowPlayerRespawns = allowPlayerRespawns;
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
        this.pvpEnabled = pvpEnabled;
        this.difficulty = difficulty;
        this.hardcore = hardcore;
        this.gameMode = gameMode;
        this.enabled = enabled;
        this.name = name;
        this.seed = seed;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
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
    public LanternDimensionType<?> getDimensionType() {
        return this.dimensionType;
    }

    @Override
    public DataContainer getGeneratorSettings() {
        return this.generatorSettings.copy();
    }

    public TeleporterAgent getTeleporterAgent() {
        return this.teleporterAgent;
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
