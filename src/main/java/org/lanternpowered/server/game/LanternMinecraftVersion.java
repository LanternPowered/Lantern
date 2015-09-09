package org.lanternpowered.server.game;

import org.spongepowered.api.MinecraftVersion;

import com.google.common.base.MoreObjects;

public class LanternMinecraftVersion implements MinecraftVersion {

    public static final LanternMinecraftVersion CURRENT = new LanternMinecraftVersion("1.8", 47, false);

    public static final LanternMinecraftVersion V1_3 = new LanternMinecraftVersion("<=1.3", 39, true);
    public static final LanternMinecraftVersion V1_5 = new LanternMinecraftVersion("1.4-1.5", 61, true);
    public static final LanternMinecraftVersion V1_6 = new LanternMinecraftVersion("1.6", 78, true);

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
        if (this.equals(o) || (this.isLegacy() && o.isLegacy())) {
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
        LanternMinecraftVersion that = (LanternMinecraftVersion) o;
        return this.getProtocol() == that.getProtocol();
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
