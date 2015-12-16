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
package org.lanternpowered.server.config.world;

import java.io.IOException;
import java.nio.file.Path;

import ninja.leaping.configurate.objectmapping.Setting;

import org.lanternpowered.server.config.ConfigBase;
import org.lanternpowered.server.config.GlobalConfig;
import org.lanternpowered.server.config.world.chunk.ChunkLoading;
import org.lanternpowered.server.config.world.chunk.ChunkLoadingConfig;
import org.lanternpowered.server.config.world.chunk.ChunkLoadingTickets;
import org.lanternpowered.server.config.world.chunk.WorldChunkLoading;

public class WorldConfig extends ConfigBase implements ChunkLoadingConfig {

    @Setting(value = ChunkLoading.CHUNK_LOADING, comment = "Configuration for the chunk loading control.")
    private WorldChunkLoading chunkLoading = new WorldChunkLoading();

    @Setting(value = "pvp-enabled", comment = "Enable if this world allows PVP combat.")
    private boolean pvpEnabled = true;

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

    private final GlobalConfig globalConfig;

    public WorldConfig(GlobalConfig globalConfig, Path path) throws IOException {
        super(path);
        this.globalConfig = globalConfig;
    }

    @Override
    public ChunkLoadingTickets getChunkLoadingTickets(String plugin) {
        if (this.chunkLoading.isEnabled()) {
            return this.chunkLoading.getChunkLoadingTickets(plugin);
        }
        return this.globalConfig.getChunkLoadingTickets(plugin);
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
}