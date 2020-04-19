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

import static org.lanternpowered.server.config.ConfigConstants.DEFAULTS;
import static org.lanternpowered.server.config.ConfigConstants.ENABLED;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class WorldChunkLoading extends ChunkLoading {

    @Setting(value = ENABLED, comment =
            "Whether this configuration file should override the globally specified chunk\n " +
            "loading settings, if set false, this sections won't affect anything.")
    private boolean enabled = false;

    @Setting(value = DEFAULTS, comment = "Default configuration for chunk loading control.")
    private WorldChunkLoadingTickets defaults = new WorldChunkLoadingTickets();

    /**
     * Gets whether this section is enabled.
     * 
     * @return is enabled
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    protected WorldChunkLoadingTickets getDefaults() {
        return this.defaults;
    }

}
