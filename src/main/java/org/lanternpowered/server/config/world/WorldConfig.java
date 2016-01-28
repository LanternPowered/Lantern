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
package org.lanternpowered.server.config.world;

import static com.google.common.base.Preconditions.checkNotNull;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.lanternpowered.server.config.ConfigBase;
import org.lanternpowered.server.config.GlobalConfig;
import org.lanternpowered.server.config.world.chunk.ChunkLoading;
import org.lanternpowered.server.config.world.chunk.ChunkLoadingConfig;
import org.lanternpowered.server.config.world.chunk.ChunkLoadingTickets;
import org.lanternpowered.server.config.world.chunk.WorldChunkLoading;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.difficulty.Difficulty;

import java.io.IOException;
import java.nio.file.Path;

public class WorldConfig extends ConfigBase implements ChunkLoadingConfig {

    @Setting(value = "chunks")
    private Chunks chunks = new Chunks();

    @ConfigSerializable
    private static class Chunks {

        @Setting(value = ChunkLoading.CHUNK_LOADING, comment = "Configuration for the chunk loading control.")
        private WorldChunkLoading chunkLoading = new WorldChunkLoading();

        @Setting(value = "clumping-threshold", comment =
                "Controls the number threshold at which the chunk data message\n " +
                "is preferred over the multi block change message.")
        private int clumpingThreshold = 64;
    }

    @Setting(value = "pvp-enabled", comment = "Enable if this world allows PVP combat.")
    private boolean pvpEnabled = true;

    @Setting(value = "hardcore", comment = "Enable if this world should use the hardcore mode.")
    private boolean hardcore = true;

    @Setting(value = "world-enabled", comment = "Enable if this world should be allowed to load.")
    private boolean worldEnabled = false;

    @Setting(value = "load-on-startup", comment = "Enable if this world should load on startup.")
    private boolean loadOnStartup = false;

    @Setting(value = "keep-spawn-loaded", comment = "Enable if this world's spawn should remain loaded with no players.")
    private boolean keepSpawnLoaded = true;

    @Setting(value = "water-evaporates", comment = "Enable if the water in this world should evaporate.")
    private boolean waterEvaporates = true;

    @Setting(value = "allow-player-respawns", comment = "Enable if the player may respawn in this world.")
    private boolean allowPlayerRespawns = true;

    @Setting(value = "max-build-height", comment = "The maximum buid height of this world.")
    private int maxBuildHeight = 255;

    @Setting(value = "difficulty", comment = "The difficulty this world.")
    private Difficulty difficulty = Difficulties.NORMAL;

    @Setting(value = "generation", comment = "The generation settings of this world.")
    private WorldGeneration generation = new WorldGeneration();

    @Setting(value = "game-mode", comment = "The game mode settings of this world.")
    private WorldGameMode gameMode = new WorldGameMode();

    @ConfigSerializable
    private static class WorldGameMode {

        @Setting(value = "mode", comment = "The default game mode of this world.")
        private GameMode mode = GameModes.SURVIVAL;

        @Setting(value = "force", comment = "Whether players are forced to the default gamemode on join.")
        private boolean force = false;
    }

    private final GlobalConfig globalConfig;

    public WorldConfig(GlobalConfig globalConfig, Path path) throws IOException {
        super(path);
        this.globalConfig = globalConfig;
    }

    @Override
    public ChunkLoadingTickets getChunkLoadingTickets(String plugin) {
        if (this.chunks.chunkLoading.isEnabled()) {
            return this.chunks.chunkLoading.getChunkLoadingTickets(plugin);
        }
        return this.globalConfig.getChunkLoadingTickets(plugin);
    }

    public int getChunkClumpingThreshold() {
        return this.chunks.clumpingThreshold;
    }

    public GameMode getGameMode() {
        return this.gameMode.mode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode.mode = checkNotNull(gameMode, "gameMode");
    }

    public boolean isGameModeForced() {
        return this.gameMode.force;
    }

    public void setGameModeForced(boolean forced) {
        this.gameMode.force = forced;
    }

    public WorldGeneration getGeneration() {
        return this.generation;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public int getMaxBuildHeight() {
        return this.maxBuildHeight;
    }

    public void setMaxBuildHeight(int maxBuildHeight) {
        this.maxBuildHeight = maxBuildHeight;
    }

    public boolean allowPlayerRespawns() {
        return this.allowPlayerRespawns;
    }

    public void setAllowPlayerRespawns(boolean allow) {
        this.allowPlayerRespawns = allow;
    }

    public boolean doesWaterEvaporate() {
        return this.waterEvaporates;
    }

    public void setDoesWaterEvaporate(boolean waterEvaporates) {
        this.waterEvaporates = waterEvaporates;
    }

    public boolean isWorldEnabled() {
        return this.worldEnabled;
    }

    public void setWorldEnabled(boolean enabled) {
        this.worldEnabled = enabled;
    }

    public boolean loadOnStartup() {
        return this.loadOnStartup;
    }

    public void setLoadOnStartup(boolean state) {
        this.loadOnStartup = state;
    }

    public boolean getKeepSpawnLoaded() {
        return this.keepSpawnLoaded;
    }

    public void setKeepSpawnLoaded(boolean loaded) {
        this.keepSpawnLoaded = loaded;
    }

    public boolean getPVPEnabled() {
        return this.pvpEnabled;
    }

    public void setPVPEnabled(boolean allow) {
        this.pvpEnabled = allow;
    }

    public boolean isHardcore() {
        return this.hardcore;
    }

    public void setHardcore(boolean hardcore) {
        this.hardcore = hardcore;
    }

}
