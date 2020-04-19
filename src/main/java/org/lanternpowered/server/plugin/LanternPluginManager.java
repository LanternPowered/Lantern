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

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.lanternpowered.launch.LanternClassLoader;
import org.lanternpowered.server.game.DirectoryKeys;
import org.lanternpowered.server.game.Lantern;
import org.slf4j.Logger;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.plugin.meta.PluginMetadata;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Singleton
public final class LanternPluginManager implements PluginManager {

    private final Map<String, PluginContainer> plugins = new HashMap<>();
    private final Map<Object, PluginContainer> pluginInstances = new IdentityHashMap<>();

    private final Logger logger;
    private final EventManager eventManager;
    private final Path pluginsFolder;
    private final Injector injector;

    @Inject
    private LanternPluginManager(EventManager eventManager, Logger logger, Injector injector,
            @Named(DirectoryKeys.PLUGINS) Path pluginsFolder) {
        this.injector = injector.getParent();
        this.pluginsFolder = pluginsFolder;
        this.eventManager = eventManager;
        this.logger = logger;
    }

    public void registerPlugin(PluginContainer plugin) {
        checkNotNull(plugin, "plugin");
        this.plugins.put(plugin.getId(), plugin);
    }

    private void registerPluginInstance(PluginContainer plugin) {
        checkNotNull(plugin, "plugin");
        this.pluginInstances.put(plugin.getInstance().orElseThrow(
                () -> new IllegalStateException("Plugin instance missing.")), plugin);
    }

    public void registerPluginInstances() {
        for (Map.Entry<String, PluginContainer> entry : this.plugins.entrySet()) {
            entry.getValue().getInstance().ifPresent(instance -> registerPluginInstance(entry.getValue()));
        }
    }

    public void loadPlugins(boolean scanClasspath) throws IOException {
        this.logger.info("Searching for plugins...");

        final PluginScanner pluginScanner = new PluginScanner();
        if (scanClasspath) {
            Lantern.getLogger().info("Scanning classpath for plugins...");

            final ClassLoader loader = LanternPluginManager.class.getClassLoader();
            if (loader instanceof URLClassLoader) {
                pluginScanner.scanClassPath((URLClassLoader) loader);
            } else {
                this.logger.error("Cannot search for plugins on classpath: Unsupported class loader: {}",
                        loader.getClass().getName());
            }
        }

        if (Files.exists(this.pluginsFolder)) {
            pluginScanner.scanDirectory(this.pluginsFolder);
        } else {
            // Create plugin folder
            Files.createDirectories(this.pluginsFolder);
        }

        final Map<String, PluginCandidate> plugins = pluginScanner.getPlugins();
        this.logger.info("{} plugin(s) found", plugins.size());

        try {
            PluginHelper.sort(checkRequirements(plugins)).forEach(this::loadPlugin);
        } catch (Throwable e) {
            throw new RuntimeException("An error occurred while loading the plugins", e);
        }
    }

    private Set<PluginCandidate> checkRequirements(Map<String, PluginCandidate> candidates) {
        final Set<PluginCandidate> successfulCandidates = new HashSet<>(candidates.size());
        final List<PluginCandidate> failedCandidates = new ArrayList<>();

        for (PluginCandidate candidate : candidates.values()) {
            if (candidate.collectDependencies(this.plugins, candidates)) {
                successfulCandidates.add(candidate);
            } else {
                failedCandidates.add(candidate);
            }
        }

        if (failedCandidates.isEmpty()) {
            return successfulCandidates; // Nothing to do, all requirements satisfied
        }

        PluginCandidate candidate;
        boolean updated;
        while (true) {
            updated = false;
            Iterator<PluginCandidate> itr = successfulCandidates.iterator();
            while (itr.hasNext()) {
                candidate = itr.next();
                if (candidate.updateRequirements()) {
                    updated = true;
                    itr.remove();
                    failedCandidates.add(candidate);
                }
            }

            if (updated) {
                // Update failed candidates as well
                failedCandidates.forEach(PluginCandidate::updateRequirements);
            } else {
                break;
            }
        }

        for (PluginCandidate failed : failedCandidates) {
            if (failed.isInvalid()) {
                this.logger.error("Plugin '{}' from {} cannot be loaded because it is invalid",
                        failed.getId(), failed.getDisplaySource());
            } else {
                this.logger.error("Cannot load plugin '{}' from {} because it is missing the required dependencies {}",
                        failed.getId(), failed.getDisplaySource(), PluginHelper.formatRequirements(failed.getMissingRequirements()));
            }
        }

        return successfulCandidates;
    }

    private void loadPlugin(PluginCandidate candidate) {
        final String id = candidate.getId();
        final LanternClassLoader classLoader = LanternClassLoader.get();

        if (candidate.getSource().isPresent()) {
            try {
                classLoader.addBaseURL(candidate.getSource().get().toUri().toURL());
            } catch (MalformedURLException e) {
                throw new RuntimeException("Failed to add plugin '" + id + "' from " + candidate.getDisplaySource() + " to classpath", e);
            }
        }

        final PluginMetadata metadata = candidate.getMetadata();
        checkNotNull(metadata, "metadata");
        final String name = firstNonNull(metadata.getName(), id);
        final String version = firstNonNull(metadata.getVersion(), "unknown");

        try {
            final Class<?> pluginClass = Class.forName(candidate.getPluginClass());
            final PluginContainer container = new LanternPluginContainer(id,
                    this.injector, pluginClass, metadata, candidate.getSource().orElse(null));
            registerPlugin(container);
            registerPluginInstance(container);
            this.eventManager.registerListeners(container, container.getInstance().get());

            this.logger.info("Loaded plugin: {} {} (from {})", name, version, candidate.getDisplaySource());
        } catch (Throwable e) {
            this.logger.error("Failed to load plugin: {} {} (from {})", name, version, candidate.getDisplaySource(), e);
        }
    }

    @Override
    public Optional<PluginContainer> fromInstance(Object instance) {
        checkNotNull(instance, "instance");
        if (instance instanceof PluginContainer) {
            return Optional.of((PluginContainer) instance);
        }
        return Optional.ofNullable(this.pluginInstances.get(instance));
    }

    @Override
    public Optional<PluginContainer> getPlugin(String id) {
        return Optional.ofNullable(this.plugins.get(checkNotNull(id, "identifier")));
    }

    @Override
    public Collection<PluginContainer> getPlugins() {
        return ImmutableList.copyOf(this.plugins.values());
    }

    @Override
    public boolean isLoaded(String id) {
        return this.plugins.containsKey(checkNotNull(id, "identifier"));
    }

}
