/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
package org.lanternpowered.server.plugin;

import org.lanternpowered.launch.LaunchClassLoader;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.Nullable;

import org.lanternpowered.server.game.LanternGame;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@NonnullByDefault
public final class LanternPluginManager implements PluginManager {

    private static final String PLUGIN_DESCRIPTOR = Type.getDescriptor(Plugin.class);
    private static final String CLASS_EXTENSION = ".class";

    private static final Predicate<Path> ARCHIVE = path -> path.toFile().getName().endsWith(".jar") ||
            path.toFile().getName().endsWith(".zip");
    private static final Comparator<PluginEntry> ENTRY_COMPARATOR = (x, y) -> {
        if (x.loadAfter != null && x.loadAfter.contains(y.id)) {
            return 1;
        }
        if (y.loadAfter != null && y.loadAfter.contains(x.id)) {
            return -1;
        }
        if (x.loadBefore != null && x.loadBefore.contains(y.id)) {
            return -1;
        }
        if (y.loadBefore != null && y.loadBefore.contains(x.id)) {
            return 1;
        }
        return 0;
    };

    private final Map<String, PluginContainer> plugins = Maps.newHashMap();
    private final Map<Object, PluginContainer> pluginInstances = Maps.newIdentityHashMap();

    private final LanternGame game;
    private final Path pluginsFolder;

    public LanternPluginManager(LanternGame game, Path pluginsFolder,
            PluginContainer... preInstalledPlugins) {
        this.pluginsFolder = checkNotNull(pluginsFolder, "pluginsFolder");
        this.game = checkNotNull(game, "game");

        // Register the inbuilt plugins
        for (PluginContainer pluginContainer : preInstalledPlugins) {
            this.registerPlugin(checkNotNull(pluginContainer, "pluginContainer"));
        }
    }

    private void registerPlugin(PluginContainer plugin) {
        this.plugins.put(plugin.getId(), plugin);
        // Plugin instances shouldn't be null
        this.pluginInstances.put(plugin.getInstance().get(), plugin);
    }

    public void loadPlugins() throws IOException {
        // Make the plugins folder is needed
        if (!Files.exists(this.pluginsFolder)) {
            Files.createDirectories(this.pluginsFolder);
        }

        List<PluginEntry> plugins = Lists.newArrayList();

        // Search for all the plugin jar/zip files
        for (Path jar : Files.list(this.pluginsFolder).filter(ARCHIVE).collect(Collectors.toList())) {
            // Search the jar for plugins
            if (scanZip(jar, plugins)) {
                // Add the jar/zip to the class loader, even if the
                // jar doesn't contain a plugin, it may be used as
                // a library
                try {
                    ((LaunchClassLoader) this.getClass().getClassLoader()).addURL(jar.toFile().toURI().toURL());
                } catch (MalformedURLException e) {
                    LanternGame.log().warn("Unable to add the file {} to the class loader", jar);
                }
            }
        }

        // Get all the unique identifiers and notify if duplicates are found
        Map<String, PluginEntry> identifiers = Maps.newHashMap();

        for (PluginEntry entry : plugins) {
            if (identifiers.containsKey(entry.id)) {
                LanternGame.log().warn("Duplicate identifier {}: {} and {}, ignoring second one.", entry.id,
                        identifiers.get(entry.id).classPath, entry.classPath);
                continue;
            } else if (this.plugins.containsKey(entry.id)) {
                LanternGame.log().warn("Duplicate identifier {}: {} and {}, ignoring second one.", entry.id,
                        this.plugins.get(entry.id).getInstance().getClass().getName(), entry.classPath);
                continue;
            }
            identifiers.put(entry.id, entry);
        }

        plugins.clear();
        plugins.addAll(identifiers.values());

        // Sort the plugins by load order
        Collections.sort(plugins, ENTRY_COMPARATOR);

        // Check for required dependencies and loading instances
        for (PluginEntry entry : plugins) {
            boolean flag = false;

            if (entry.required != null) {
                for (String require : entry.required) {
                    if (!this.plugins.containsKey(entry.id)) {
                        LanternGame.log().error("The plugin {} (from {}) is missing the dependency {}",
                                entry.id, entry.classPath, require);
                        flag = true;
                        break;
                    }
                }
            }

            if (flag) {
                continue;
            }

            try {
                final LanternPluginContainer container = new LanternPluginContainer(entry.id, entry.name, entry.version);
                Class<?> pluginClass;
                try {
                    pluginClass = Class.forName(entry.classPath);
                } catch (ClassNotFoundException e) {
                    LanternGame.log().error("Unable to load the plugin {} (from {})", entry.id, entry.classPath, e);
                    continue;
                }
                final Injector injector = Guice.createInjector(new PluginModule(container, pluginClass, this.game));
                container.setInjector(injector);
                Object instance = injector.getInstance(pluginClass);
                container.setInstance(instance);

                this.registerPlugin(container);
                this.game.getEventManager().registerListeners(container, instance);

                LanternGame.log().info("Loaded plugin: {} {} (from {})", container.getName(), container.getVersion(), entry.classPath);
            } catch (Throwable e) {
                LanternGame.log().error("Failed to load plugin: {} (from {})", entry.id, entry.classPath, e);
            }
        }
    }

    @Override
    public Optional<PluginContainer> fromInstance(Object instance) {
        if (checkNotNull(instance, "instance") instanceof PluginContainer) {
            return Optional.of((PluginContainer) instance);
        }
        return Optional.ofNullable(this.pluginInstances.get(instance));
    }

    @Override
    public Optional<PluginContainer> getPlugin(String id) {
        return Optional.ofNullable(this.plugins.get(checkNotNull(id, "identifier")));
    }

    @Override
    public Logger getLogger(PluginContainer plugin) {
        return LoggerFactory.getLogger(checkNotNull(plugin, "plugin").getId());
    }

    @Override
    public Collection<PluginContainer> getPlugins() {
        return ImmutableList.copyOf(this.plugins.values());
    }

    @Override
    public boolean isLoaded(String id) {
        return this.plugins.containsKey(checkNotNull(id, "identifier"));
    }

    private static class PluginEntry {

        public final String id;
        public final String name;
        public final String version;
        public final String classPath;

        public PluginEntry(String id, String name, String version, String classPath) {
            this.classPath = classPath;
            this.version = version;
            this.name = name;
            this.id = id;
        }

        // The plugins that will be loaded after this one.
        @Nullable public List<String> loadAfter;

        // The plugins that will be loaded before this one.
        @Nullable public List<String> loadBefore;

        // The plugins that are required for this one to work.
        @Nullable public List<String> required;
    }

    private static boolean scanZip(Path file, List<PluginEntry> plugins) {
        try {
            try (ZipFile zip = new ZipFile(file.toFile())) {
                Enumeration<? extends ZipEntry> entries = zip.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (entry.isDirectory() || !entry.getName().endsWith(CLASS_EXTENSION)) {
                        continue;
                    }
                    try (InputStream in = zip.getInputStream(entry)) {
                        PluginEntry plugin = findPlugin(in);
                        if (plugin != null) {
                            plugins.add(plugin);
                        }
                    }
                }
            }
        } catch (IOException e) {
            LanternGame.log().error("Failed to load plugin/library JAR: {}", file, e);
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static PluginEntry findPlugin(InputStream in) throws IOException {
        ClassReader reader = new ClassReader(in);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

        if (classNode.visibleAnnotations != null) {
            for (Object node0 : classNode.visibleAnnotations) {
                AnnotationNode node = (AnnotationNode) node0;
                if (node.desc.equals(PLUGIN_DESCRIPTOR)) {
                    Map<String, Object> settings = Maps.newHashMap();

                    List<Object> objects = node.values;
                    for (int i = 0; i < objects.size(); i++) {
                        settings.put((String) objects.get(i), objects.get(++i));
                    }

                    final String id = (String) settings.get("id");
                    final String name = (String) settings.get("name");
                    final String version = settings.containsKey("version") ? (String) settings.get("version") : "unknown";
                    final String classPath = classNode.name.replace('/', '.');
                    PluginEntry entry = new PluginEntry(id, name, version, classPath);

                    String dependencies = (String) settings.get("dependencies");
                    if (dependencies != null && !dependencies.isEmpty()) {
                        List<String> parts0 = Splitter.on(';').splitToList(dependencies);
                        for (String part0 : parts0) {
                            List<String> parts1 = Splitter.on(':').limit(2).splitToList(part0);

                            String key = parts1.get(0);
                            String value = parts1.get(1);

                            int index = value.indexOf('@');
                            if (index != -1) {
                                value = value.substring(0, index);
                            }

                            boolean flag = false;

                            if (key.equals("after") || key.equals("required-after")) {
                                if (entry.loadAfter == null) {
                                    entry.loadAfter = Lists.newArrayList();
                                }
                                entry.loadAfter.add(value);
                                flag = true;
                            }
                            if (key.equals("before") || key.equals("required-before")) {
                                if (entry.loadBefore == null) {
                                    entry.loadBefore = Lists.newArrayList();
                                }
                                entry.loadBefore.add(value);
                                flag = true;
                            }
                            if (key.equals("required-after") || key.equals("required-before")) {
                                if (entry.required == null) {
                                    entry.required = Lists.newArrayList();
                                }
                                entry.required.add(value);
                                flag = true;
                            }
                            if (!flag) {
                                LanternGame.log().error("Failed to parse dependency entry {} for plugin {} ({})",
                                        part0, entry.id, entry.classPath);
                            }
                        }
                    }

                    return entry;
                }
            }
        }

        return null;
    }
}
