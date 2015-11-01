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
package org.lanternpowered.server.game;

import java.util.Map;

import org.spongepowered.api.Platform;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;

public class LanternPlatform implements Platform {

    // The name of the platform
    private final String name = "Lantern";

    private final String apiVersion = MoreObjects.firstNonNull(LanternPlatform.class.getPackage().getSpecificationVersion(), "UNKNOWN");
    private final String version = MoreObjects.firstNonNull(LanternPlatform.class.getPackage().getImplementationVersion(), "UNKNOWN");

    @Override
    public Type getType() {
        return Type.SERVER;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public String getApiVersion() {
        return this.apiVersion;
    }

    @Override
    public LanternMinecraftVersion getMinecraftVersion() {
        return LanternMinecraftVersion.CURRENT;
    }

    @Override
    public Map<String, Object> asMap() {
        final Map<String, Object> map = Maps.newHashMap();
        map.put("Name", this.getName());
        map.put("Type", this.getType());
        map.put("ApiVersion", this.getApiVersion());
        map.put("ImplementationVersion", this.getVersion());
        map.put("MinecraftVersion", this.getMinecraftVersion());
        return map;
    }

    @Override
    public Type getExecutionType() {
        return Type.SERVER;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", this.getType())
                .add("executionType", this.getExecutionType())
                .add("version", this.getVersion())
                .add("apiVersion", this.getApiVersion())
                .add("minecraftVersion", this.getMinecraftVersion())
                .toString();
    }
}
