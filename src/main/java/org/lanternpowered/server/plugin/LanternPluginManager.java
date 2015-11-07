/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

public final class LanternPluginManager implements PluginManager {

    private static final String PLUGIN_DESCRIPTOR = Type.getDescriptor(Plugin.class);
    private static final String CLASS_EXTENSION = ".class";

    private static final FilenameFilter ARCHIVE = (dir, name) -> name.endsWith(".jar") || name.endsWith(".zip");
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
    private final File pluginsFolder;

    public LanternPluginManager(LanternGame game, File pluginsFolder, PluginContainer minecraft) {
        this.pluginsFolder = checkNotNull(pluginsFolder, "pluginsFolder");
        this.game = checkNotNull(game, "game");

        // Register the minecraft plugin (the actual server)
        this.registerPlugin(checkNotNull(minecraft, "minecraft"));
    }

    private void registerPlugin(PluginContainer plugin) {
        this.plugins.put(plugin.getId(), plugin);
        this.pluginInstances.put(plugin.getInstance(), plugin);
    }

    private void addLibrary(URL url) {
        URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(sysLoader, url);
        } catch (ReflectiveOperationException e) {
            LanternGame.log().error("Failed to add to classpath: " + url, e);
        }
    }

    public void loadPlugins() {
        // Make the plugins folder is needed
        if (!this.pluginsFolder.exists()) {
            this.pluginsFolder.mkdirs();
        }

        List<PluginEntry> plugins = Lists.newArrayList();

        // Search for all the plugin jar/zip files
        for (File jar : this.pluginsFolder.listFiles(ARCHIVE)) {
            // Add the jar/zip to the class loader, even if the
            // jar doesn't contain a plugin, it may be used as
            // a library
            try {
                this.addLibrary(jar.toURI().toURL());
            } catch (MalformedURLException e) {
                LanternGame.log().warn("Unable to add the file {} to the class loader", jar);
                continue;
            }

            // Search the jar for plugins
            scanZip(jar, plugins);
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
                LanternPluginContainer container = new LanternPluginContainer(entry.id, entry.name, entry.version);
                Class<?> pluginClass;
                try {
                    pluginClass = Class.forName(entry.classPath);
                } catch (ClassNotFoundException e) {
                    LanternGame.log().error("Unable to load the plugin {} (from {})", entry.id, entry.classPath, e);
                    continue;
                }
                Injector injector = Guice.createInjector(new PluginModule(container, pluginClass, this.game));
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

        public String id;
        public String name;
        public String version;
        public String classPath;

        // The plugins that will be loaded after this one.
        public List<String> loadAfter;

        // The plugins that will be loaded before this one.
        public List<String> loadBefore;

        // The plugins that are required for this one to work.
        public List<String> required;
    }

    private static void scanZip(File file, List<PluginEntry> plugins) {
        if (!ARCHIVE.accept(null, file.getName())) {
            return;
        }

        // Open the zip file so we can scan for plugins
        try {
            ZipFile zip = new ZipFile(file);
            try {
                Enumeration<? extends ZipEntry> entries = zip.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (entry.isDirectory() || !entry.getName().endsWith(CLASS_EXTENSION)) {
                        continue;
                    }
                    InputStream in = zip.getInputStream(entry);
                    try {
                        PluginEntry plugin = findPlugin(in);
                        if (plugin != null) {
                            plugins.add(plugin);
                        }
                    } finally {
                        in.close();
                    }
                }
            } finally {
                zip.close();
            }
        } catch (IOException e) {
            LanternGame.log().error("Failed to load plugin JAR: {}", file, e);
        }
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

                    PluginEntry entry = new PluginEntry();
                    entry.id = (String) settings.get("id");
                    entry.name = (String) settings.get("name");
                    entry.version = settings.containsKey("version") ? (String) settings.get("version") : "unknown";
                    entry.classPath = classNode.name.replace('/', '.');

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
