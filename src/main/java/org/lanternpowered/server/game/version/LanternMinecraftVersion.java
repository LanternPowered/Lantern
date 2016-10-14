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
package org.lanternpowered.server.game.version;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.network.protocol.Protocol;
import org.spongepowered.api.MinecraftVersion;

public class LanternMinecraftVersion implements MinecraftVersion {

    private static final String UNKNOWN_NAME = "unknown";

    public static final LanternMinecraftVersion UNKNOWN = new LanternMinecraftVersion(
            UNKNOWN_NAME, -1, false);
    public static final LanternMinecraftVersion UNKNOWN_LEGACY = new LanternMinecraftVersion(
            UNKNOWN_NAME, -1, true);

    public static final LanternMinecraftVersion CURRENT = new LanternMinecraftVersion(
            LanternGame.MINECRAFT_VERSION, Protocol.CURRENT_VERSION, false);

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
