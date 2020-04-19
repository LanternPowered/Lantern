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
package org.lanternpowered.server.plugin;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.lanternpowered.launch.LanternClassLoader;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.plugin.meta.PluginMetadata;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class InternalPluginContainer extends InfoPluginContainer {

    private static PluginMetadata readPluginInfo(String id, @Nullable String version) {
        try {
            final URL url = LanternClassLoader.get().getResource("data/" + id + "/plugin.info");
            checkNotNull(url, "Missing plugin.info file for internal plugin %s", id);
            final PluginMetadata metadata = InfoPluginContainer.readPluginInfo(id, url);
            // Allow the version to be overwritten
            if (version != null) {
                metadata.setVersion(version);
            }
            return metadata;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read plugin.info files for the internal plugins.");
        }
    }

    private InternalPluginContainer(String id, @Nullable String version) {
        super(id, readPluginInfo(id, version));
    }

    @Inject
    void init(LanternPluginManager pluginManager) {
        pluginManager.registerPlugin(this);
    }

    @Singleton
    public static final class Api extends InternalPluginContainer {

        @Inject private Game game;

        public Api() {
            super(InternalPluginsInfo.Api.IDENTIFIER, InternalPluginsInfo.Api.VERSION);
        }

        @Override
        public Optional<?> getInstance() {
            return Optional.of(this.game);
        }
    }

    @Singleton
    public static final class SpongePlatform extends InternalPluginContainer {

        @Inject private Game game;

        public SpongePlatform() {
            super(InternalPluginsInfo.SpongePlatform.IDENTIFIER, InternalPluginsInfo.SpongePlatform.VERSION);
        }

        @Override
        public Optional<?> getInstance() {
            return Optional.of(this.game);
        }
    }

    @Singleton
    public static final class Minecraft extends InternalPluginContainer {

        @Inject private Server server;

        public Minecraft() {
            super(InternalPluginsInfo.Minecraft.IDENTIFIER, InternalPluginsInfo.Minecraft.VERSION);
        }

        @Override
        public Optional<?> getInstance() {
            return Optional.of(this.server);
        }
    }

    @Singleton
    public static final class Implementation extends InternalPluginContainer {

        @Inject private Server server;

        public Implementation() {
            super(InternalPluginsInfo.Implementation.IDENTIFIER, InternalPluginsInfo.Implementation.VERSION);
        }

        @Override
        public Optional<?> getInstance() {
            return Optional.of(this.server);
        }
    }
}
