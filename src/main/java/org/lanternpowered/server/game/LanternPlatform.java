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
import org.spongepowered.api.plugin.PluginContainer;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;

import static com.google.common.base.MoreObjects.firstNonNull;

public class LanternPlatform implements Platform {

    public static final String API_NAME = firstNonNull(Platform.class.getPackage()
            .getSpecificationTitle(), LanternGame.API_NAME);
    public static final String API_VERSION = firstNonNull(Platform.class.getPackage()
            .getSpecificationVersion(), LanternGame.API_VERSION);

    public static final String IMPL_NAME = firstNonNull(LanternPlatform.class.getPackage()
            .getSpecificationTitle(), LanternGame.IMPL_NAME);
    public static final String IMPL_VERSION = firstNonNull(LanternPlatform.class.getPackage()
            .getSpecificationVersion(), LanternGame.IMPL_VERSION);

    private final PluginContainer apiContainer;
    private final PluginContainer implContainer;

    public LanternPlatform(PluginContainer apiContainer, PluginContainer implContainer) {
        this.implContainer = implContainer;
        this.apiContainer = apiContainer;
    }

    @Override
    public Type getType() {
        return Type.SERVER;
    }

    @Override
    public PluginContainer getApi() {
        return this.apiContainer;
    }

    @Override
    public PluginContainer getImplementation() {
        return this.implContainer;
    }

    @Override
    public LanternMinecraftVersion getMinecraftVersion() {
        return LanternMinecraftVersion.CURRENT;
    }

    @Override
    public Map<String, Object> asMap() {
        final Map<String, Object> map = Maps.newHashMap();
        map.put("Name", this.implContainer.getName());
        map.put("Type", this.getType());
        map.put("ApiVersion", this.apiContainer.getVersion());
        map.put("ImplementationVersion", this.implContainer.getVersion());
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
                .add("version", this.implContainer.getVersion())
                .add("apiVersion", this.apiContainer.getVersion())
                .add("minecraftVersion", this.getMinecraftVersion())
                .toString();
    }
}
