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
package org.lanternpowered.server.game.version;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.network.protocol.Protocol;
import org.lanternpowered.server.plugin.InternalPluginsInfo;
import org.spongepowered.api.MinecraftVersion;

public class LanternMinecraftVersion implements MinecraftVersion {

    private static final String UNKNOWN_NAME = "unknown";

    public static final LanternMinecraftVersion UNKNOWN = new LanternMinecraftVersion(
            UNKNOWN_NAME, -1, false);
    public static final LanternMinecraftVersion UNKNOWN_LEGACY = new LanternMinecraftVersion(
            UNKNOWN_NAME, -1, true);

    public static final LanternMinecraftVersion CURRENT = new LanternMinecraftVersion(
            InternalPluginsInfo.Minecraft.VERSION, Protocol.CURRENT_VERSION, false);

    private final String name;
    private final int protocol;
    private final boolean legacy;

    public LanternMinecraftVersion(String name, int protocol, boolean legacy) {
        this.protocol = protocol;
        this.legacy = legacy;
        this.name = name;
    }

    /**
     * Gets the protocol version.
     * 
     * @return the protocol version
     */
    public int getProtocol() {
        return this.protocol;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isLegacy() {
        return this.legacy;
    }

    @Override
    public int compareTo(MinecraftVersion o) {
        if (equals(o) || (isLegacy() && o.isLegacy())) {
            return 0;
        } else if (this.isLegacy()) {
            return -1;
        } else if (o.isLegacy()) {
            return 1;
        } else {
            return this.protocol - ((LanternMinecraftVersion) o).protocol;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LanternMinecraftVersion)) {
            return false;
        }
        final LanternMinecraftVersion that = (LanternMinecraftVersion) o;
        return this.protocol == that.protocol && this.legacy == that.legacy && this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int hash = this.protocol;
        hash = 31 * hash + (this.legacy ? 1 : 0);
        hash = 31 * hash + this.name.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", this.name)
                .add("legacy", this.legacy)
                .add("protocol", this.protocol)
                .toString();
    }
}
