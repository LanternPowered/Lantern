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
package org.lanternpowered.server.config.world.chunk;

import static org.lanternpowered.server.config.ConfigConstants.OVERRIDES;

import com.google.common.collect.Maps;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.lanternpowered.server.game.LanternGame;

import java.util.Map;

@ConfigSerializable
public abstract class ChunkLoading implements ChunkLoadingConfig {

    public static final String MAXIMUM_CHUNKS_PER_TICKET = "maximum-chunks-per-ticket";
    public static final String MAXIMUM_TICKET_COUNT = "maximum-ticket-count";
    public static final String PLAYER_TICKET_COUNT = "player-ticket-count";
    public static final String CHUNK_LOADING = "chunk-loading";

    private static final MinecraftChunkLoadingTickets MINECRAFT = new MinecraftChunkLoadingTickets();

    @Setting(value = OVERRIDES, comment = "Plugin specific configuration for chunk loading control.")
    private Map<String, PluginChunkLoadingTickets> pluginOverrides = Maps.newHashMap();

    protected abstract ChunkLoadingTickets getDefaults();

    @Override
    public ChunkLoadingTickets getChunkLoadingTickets(String plugin) {
        // Minecraft has no limits
        if (plugin.equalsIgnoreCase(LanternGame.MINECRAFT_ID)) {
            return MINECRAFT;
        }
        // Check for overridden configuration
        if (this.pluginOverrides.containsKey(plugin)) {
            return this.pluginOverrides.get(plugin);
        }
        // Fall back to default if not found
        return this.getDefaults();
    }

}
