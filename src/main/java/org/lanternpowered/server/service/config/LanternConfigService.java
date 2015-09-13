package org.lanternpowered.server.service.config;

import static org.lanternpowered.server.util.Conditions.checkPlugin;

import java.io.File;

import org.spongepowered.api.service.config.ConfigRoot;
import org.spongepowered.api.service.config.ConfigService;

public class LanternConfigService implements ConfigService {

    private final File configRoot;

    public LanternConfigService(File configRoot) {
        this.configRoot = configRoot;
    }

    @Override
    public ConfigRoot getSharedConfig(Object instance) {
        return new LanternConfigRoot(checkPlugin(instance, "instance").getId().toLowerCase(), this.configRoot);
    }

    @Override
    public ConfigRoot getPluginConfig(Object instance) {
        String name = checkPlugin(instance, "instance").getId().toLowerCase();
        return new LanternConfigRoot(name, new File(this.configRoot, name));
    }
}