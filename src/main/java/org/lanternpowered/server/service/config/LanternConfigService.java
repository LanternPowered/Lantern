package org.lanternpowered.server.service.config;

import static org.lanternpowered.server.util.Conditions.checkPlugin;

import java.io.File;

import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.config.ConfigRoot;
import org.spongepowered.api.service.config.ConfigService;

public class LanternConfigService implements ConfigService {

    private final static File CONFIG_ROOT = new File("config");

    @Override
    public ConfigRoot getSharedConfig(Object instance) {
        return getSharedRoot(checkPlugin(instance, "instance"));
    }

    @Override
    public ConfigRoot getPluginConfig(Object instance) {
        return getPrivateRoot(checkPlugin(instance, "instance"));
    }

    public static ConfigRoot getSharedRoot(PluginContainer container) {
        final String name = container.getId().toLowerCase();
        return new LanternConfigRoot(name, CONFIG_ROOT);
    }

    public static ConfigRoot getPrivateRoot(PluginContainer container) {
        final String name = container.getId().toLowerCase();
        return new LanternConfigRoot(name, new File(CONFIG_ROOT, name));
    }
}