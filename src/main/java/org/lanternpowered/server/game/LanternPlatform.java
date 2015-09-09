package org.lanternpowered.server.game;

import java.util.Map;

import org.spongepowered.api.MinecraftVersion;
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
    public MinecraftVersion getMinecraftVersion() {
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
}
