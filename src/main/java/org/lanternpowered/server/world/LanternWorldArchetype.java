/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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

import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.lanternpowered.server.world.dimension.LanternDimensionType;
import org.lanternpowered.server.world.portal.LanternPortalAgentType;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.world.gen.GeneratorType;
import org.spongepowered.api.world.SerializationBehavior;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

import java.util.Collection;

import javax.annotation.Nullable;

final class LanternWorldArchetype extends DefaultCatalogType implements WorldArchetype {

    private final GameMode gameMode;
    private final Difficulty difficulty;
    private final LanternDimensionType<?> dimensionType;
    @Nullable final GeneratorType generatorType;
    private final Collection<WorldGeneratorModifier> generatorModifiers;
    @Nullable final DataContainer generatorSettings;
    private final SerializationBehavior serializationBehavior;
    private final LanternPortalAgentType portalAgentType;

    private final boolean hardcore;
    private final boolean enabled;
    private final boolean loadsOnStartup;
    private final boolean usesMapFeatures;
    private final boolean generateBonusChest;
    private final boolean commandsAllowed;
    private final boolean pvpEnabled;
    private final boolean generateSpawnOnLoad;
    private final boolean isSeedRandomized;
    @Nullable final Boolean allowPlayerRespawns;
    @Nullable final Boolean keepsSpawnLoaded;
    @Nullable final Boolean waterEvaporates;

    private final int buildHeight;
    private final long seed;

    LanternWorldArchetype(CatalogKey key, GameMode gameMode, LanternDimensionType<?> dimensionType, @Nullable GeneratorType generatorType,
            Collection<WorldGeneratorModifier> generatorModifiers, @Nullable DataContainer generatorSettings, Difficulty difficulty,
            SerializationBehavior serializationBehavior, LanternPortalAgentType portalAgentType, boolean hardcore, boolean enabled,
            boolean loadsOnStartup, @Nullable Boolean keepsSpawnLoaded, boolean usesMapFeatures, boolean pvpEnabled, boolean generateBonusChest,
            boolean commandsAllowed, @Nullable Boolean waterEvaporates, @Nullable Boolean allowPlayerRespawns, boolean generateSpawnOnLoad,
            boolean isSeedRandomized, long seed, int buildHeight) {
        super(key);
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
        this.isSeedRandomized = isSeedRandomized;
        this.buildHeight = buildHeight;
        this.pvpEnabled = pvpEnabled;
        this.difficulty = difficulty;
        this.hardcore = hardcore;
        this.gameMode = gameMode;
        this.enabled = enabled;
        this.seed = seed;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
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
        return this.keepsSpawnLoaded == null ? this.dimensionType.getKeepSpawnLoaded() : this.keepsSpawnLoaded;
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
    public boolean isSeedRandomized() {
        return this.isSeedRandomized;
    }

    @Override
    public GameMode getGameMode() {
        return this.gameMode;
    }

    @Override
    public GeneratorType getGeneratorType() {
        return this.generatorType == null ? this.dimensionType.getDefaultGeneratorType() : this.generatorType;
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
        return this.waterEvaporates == null ? this.dimensionType.getDoesWaterEvaporate() : this.waterEvaporates;
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
        return this.generatorSettings != null ? this.generatorSettings.copy() : getGeneratorType().getGeneratorSettings();
    }

    @Override
    public SerializationBehavior getSerializationBehavior() {
        return this.serializationBehavior;
    }

    public int getBuildHeight() {
        return this.buildHeight;
    }

    public boolean allowPlayerRespawns() {
        return this.allowPlayerRespawns == null ? this.dimensionType.getAllowsPlayerRespawns() : this.allowPlayerRespawns;
    }

    @Override
    public boolean isPVPEnabled() {
        return this.pvpEnabled;
    }

}
