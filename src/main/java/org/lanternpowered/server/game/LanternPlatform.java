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
package org.lanternpowered.server.game;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.MoreObjects;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.lanternpowered.server.game.version.LanternMinecraftVersion;
import org.lanternpowered.server.plugin.InternalPluginsInfo;
import org.spongepowered.api.Platform;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class LanternPlatform implements Platform {

    private final PluginContainer apiContainer;
    private final PluginContainer implContainer;
    private final PluginContainer minecraftContainer;

    private final Map<String, Object> platformMap = new HashMap<String, Object>() {

        private static final long serialVersionUID = -4950319352163911882L;

        @Override
        public Object put(String key, Object value) {
            checkArgument(!containsKey(key), "Cannot set the value of the existing key %s", key);
            return super.put(key, value);
        }
    };

    @Inject
    private LanternPlatform(
            @Named(InternalPluginsInfo.Api.IDENTIFIER) PluginContainer apiContainer,
            @Named(InternalPluginsInfo.Implementation.IDENTIFIER) PluginContainer implContainer,
            @Named(InternalPluginsInfo.Minecraft.IDENTIFIER) PluginContainer minecraftContainer) {
        this.minecraftContainer = minecraftContainer;
        this.implContainer = implContainer;
        this.apiContainer = apiContainer;

        this.platformMap.put("Type", getType());
        this.platformMap.put("ApiName", apiContainer.getName());
        this.platformMap.put("ApiVersion", apiContainer.getVersion());
        this.platformMap.put("ImplementationName", implContainer.getName());
        this.platformMap.put("ImplementationVersion", implContainer.getVersion());
        this.platformMap.put("MinecraftVersion", getMinecraftVersion());
    }

    @Override
    public Type getType() {
        return Type.SERVER;
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
    public PluginContainer getContainer(Component component) {
        switch (component) {
            case GAME:
                return this.minecraftContainer;
            case API:
                return this.apiContainer;
            case IMPLEMENTATION:
                return this.implContainer;
        }
        throw new IllegalArgumentException("Unknown Component: " + component);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", getType())
                .add("executionType", getExecutionType())
                .add("version", this.implContainer.getVersion())
                .add("apiVersion", this.apiContainer.getVersion())
                .add("minecraftVersion", getMinecraftVersion())
                .toString();
    }
}
