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
package org.lanternpowered.server.config;

import static org.lanternpowered.server.util.Conditions.checkPlugin;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import ninja.leaping.configurate.objectmapping.DefaultObjectMapperFactory;
import ninja.leaping.configurate.objectmapping.GuiceObjectMapperFactory;
import ninja.leaping.configurate.objectmapping.ObjectMapperFactory;
import org.lanternpowered.server.game.DirectoryKeys;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.config.ConfigRoot;
import org.spongepowered.plugin.PluginContainer;

import java.nio.file.Path;

@Singleton
public final class LanternConfigManager implements ConfigManager {

    private final Path configFolder;

    @Inject
    public LanternConfigManager(@Named(DirectoryKeys.CONFIG) Path configFolder) {
        this.configFolder = configFolder;
    }

    @Override
    public ConfigRoot getSharedConfig(Object instance) {
        final PluginContainer pluginContainer = checkPlugin(instance, "instance");
        final String name = pluginContainer.getId().toLowerCase();
        return new LanternConfigRoot(getMapperFactory(pluginContainer), name, this.configFolder);
    }

    @Override
    public ConfigRoot getPluginConfig(Object instance) {
        final PluginContainer pluginContainer = checkPlugin(instance, "instance");
        final String name = pluginContainer.getId().toLowerCase();
        return new LanternConfigRoot(getMapperFactory(pluginContainer), name, this.configFolder.resolve(name));
    }

    private static ObjectMapperFactory getMapperFactory(PluginContainer container) {
        if (container instanceof LanternPluginContainer) {
            return ((LanternPluginContainer) container).getInjector().getInstance(GuiceObjectMapperFactory.class);
        }
        return DefaultObjectMapperFactory.getInstance();
    }
}
