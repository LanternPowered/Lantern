package org.lanternpowered.server.catalog;

import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

public abstract class LanternPluginCatalogType implements PluginCatalogType {

    private final String pluginId;
    private final String name;

    public LanternPluginCatalogType(String pluginId, String name) {
        this.pluginId = checkNotNullOrEmpty(pluginId, "pluginId").toLowerCase();
        this.name = checkNotNullOrEmpty(name, "name").toLowerCase();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPluginId() {
        return this.pluginId;
    }
}
