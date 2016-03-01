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
package org.lanternpowered.server.game;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.MoreObjects;
import org.spongepowered.api.Platform;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@NonnullByDefault
public class LanternPlatform implements Platform {

    public static final String API_NAME = firstNonNull(Platform.class.getPackage()
            .getSpecificationTitle(), LanternGame.API_NAME);
    public static final Optional<String> API_VERSION = Optional.ofNullable(Platform.class.getPackage()
            .getSpecificationVersion());

    public static final String IMPL_NAME = firstNonNull(LanternPlatform.class.getPackage()
            .getImplementationTitle(), LanternGame.IMPL_NAME);
    public static final Optional<String> IMPL_VERSION = Optional.ofNullable(LanternPlatform.class.getPackage()
            .getImplementationVersion());

    private final PluginContainer apiContainer;
    private final PluginContainer implContainer;

    private final Map<String, Object> platformMap = new HashMap<String, Object>() {

        private static final long serialVersionUID = -4950319352163911882L;

        @Override
        public Object put(String key, Object value) {
            checkArgument(!this.containsKey(key), "Cannot set the value of the existing key %s", key);
            return super.put(key, value);
        }
    };

    public LanternPlatform(PluginContainer apiContainer, PluginContainer implContainer) {
        this.implContainer = implContainer;
        this.apiContainer = apiContainer;

        this.platformMap.put("Type", this.getType());
        this.platformMap.put("ApiName", apiContainer.getName());
        this.platformMap.put("ApiVersion", apiContainer.getVersion());
        this.platformMap.put("ImplementationName", implContainer.getName());
        this.platformMap.put("ImplementationVersion", implContainer.getVersion());
        this.platformMap.put("MinecraftVersion", this.getMinecraftVersion());
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
        return this.platformMap;
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
