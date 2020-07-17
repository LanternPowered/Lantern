/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.config.world.chunk;

import static org.lanternpowered.server.config.ConfigConstants.OVERRIDES;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public abstract class ChunkLoading implements ChunkLoadingConfig {

    public static final String MAXIMUM_CHUNKS_PER_TICKET = "maximum-chunks-per-ticket";
    public static final String MAXIMUM_TICKET_COUNT = "maximum-ticket-count";
    public static final String PLAYER_TICKET_COUNT = "player-ticket-count";
    public static final String CHUNK_LOADING = "chunk-loading";

    private static final MinecraftChunkLoadingTickets MINECRAFT = new MinecraftChunkLoadingTickets();

    @Setting(value = OVERRIDES, comment = "Plugin specific configuration for chunk loading control.")
    private Map<String, PluginChunkLoadingTickets> pluginOverrides = new HashMap<>();

    protected abstract ChunkLoadingTickets getDefaults();

    @Override
    public ChunkLoadingTickets getChunkLoadingTickets(String plugin) {
        // Minecraft has no limits
        if (plugin.equalsIgnoreCase(InternalPluginsInfo.Minecraft.IDENTIFIER)) {
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
