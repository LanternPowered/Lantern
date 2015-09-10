package org.lanternpowered.server.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.plugin.PluginContainer;

public class LanternPluginContainer implements PluginContainer {

    private final String id;
    private final String name;
    private final String version;

    // The instance of the plugin
    private Object instance;

    public LanternPluginContainer(String id, String name, String version) {
        this.version = version;
        this.name = name;
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
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
    public Object getInstance() {
        return this.instance;
    }

    /**
     * Gets the logger of the plugin.
     * 
     * @return the logger
     */
    public Logger getLogger() {
        return LoggerFactory.getLogger(this.id);
    }

    public void setInstance(Object instance) {
        if (this.instance != null) {
            throw new IllegalStateException("Instance for (" + this.getId() + ") can only be set once!");
        }
        this.instance = instance;
    }

}
