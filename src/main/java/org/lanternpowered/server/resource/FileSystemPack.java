/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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
package org.lanternpowered.server.resource;

import static com.google.common.base.Preconditions.checkNotNull;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.resource.Resource;
import org.spongepowered.api.resource.ResourcePath;
import org.spongepowered.api.text.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

public final class FileSystemPack extends LanternPack {

    private final Path dataRoot;
    private final Cache<LanternResourcePath, LanternResource> cache = Caffeine.newBuilder().build();

    FileSystemPack(Text name, @Nullable DataView metadata,
            @Nullable PluginContainer plugin, Path root) {
        super(name, metadata, plugin);
        // Already go inside the data directory
        // data/<namespace>/path/to/file
        this.dataRoot = root.resolve("data");
    }

    @Override
    void reload() {
        this.cache.cleanUp();
    }

    private LanternResource load(LanternResourcePath resourcePath, Path path) {
        final Path mcmetaPath = path.resolveSibling(path.getFileName().toString() + ".mcmeta");
        DataView mcmeta = null;
        if (Files.exists(mcmetaPath)) {
            try (BufferedReader reader = Files.newBufferedReader(mcmetaPath)) {
                mcmeta = DataFormats.JSON.readFrom(reader);
            } catch (IOException e) {
                Lantern.getLogger().warn("Unable to load mcmeta file for: {} (path: {})", resourcePath, path, e);
            }
        }
        return new LanternResource(resourcePath, this, new FileSystemResourceData(path, mcmeta));
    }

    @Override
    public Stream<ResourcePath> walkResourcePaths(ResourcePath path, int maxDepth) {
        checkNotNull(path, "path");
        final LanternResourcePath path1 = (LanternResourcePath) path;
        if (path1.isFile() || maxDepth <= 0) {
            return Stream.empty();
        }
        Stream<ResourcePath> stream = null;
        try {
            // If a wildcard is used, there is some special handling required
            if (path1.getNamespace().isEmpty()) {
                // A list with all the namespace directories
                for (Path namespacePath : Files.list(this.dataRoot).collect(Collectors.toList())) {
                    final String namespace = namespacePath.getFileName().toString();
                    // Check if the namespace is valid, don't want to break things
                    if (LanternResourcePath.NAMESPACE_PATTERN.matcher(namespace).matches()) {
                        Lantern.getLogger().warn("Found a invalid namespace {} within the {} pack.", namespace, getName().toPlain());
                    } else {
                        final LanternResourcePath newPath = LanternResourcePath.uncheckedOf(namespace, path.getPath());
                        final Stream<ResourcePath> extraStream = walkResourcePaths(
                                newPath, namespacePath.resolve(path.getPath()), maxDepth);
                        if (extraStream != null) {
                            stream = stream == null ? extraStream : Stream.concat(stream, extraStream);
                        }
                    }
                }
            } else {
                stream = walkResourcePaths(path1, this.dataRoot.resolve(path.getNamespace()).resolve(path.getPath()), maxDepth);
            }
        } catch (IOException e) {
            Lantern.getLogger().error("Failed to walk resource path: {}", path, e);
        }
        return stream == null ? Stream.empty() : stream;
    }

    @Nullable
    private Stream<ResourcePath> walkResourcePaths(LanternResourcePath resourcePath, Path start, int maxDepth) {
        if (!Files.exists(start)) {
            return null;
        }
        try {
            return Files.walk(start, maxDepth).map(path -> {
                final Path relPath = path.relativize(start);
                // Backslashes are not supported so replace them
                final String path1 = relPath.toString().replace('\\', '/');
                // Check if it's a invalid path for a resource and ignore .mcmeta files
                if (path1.endsWith(".mcmeta") ||
                        !LanternResourcePath.PATH_PATTERN.matcher(path1).matches()) {
                    return null;
                }
                // Create the new path
                return LanternResourcePath.uncheckedOf(resourcePath.getNamespace(),
                        resourcePath.getPath() + '/' + path1);
            });
        } catch (IOException e) {
            Lantern.getLogger().error("Failed to walk resource path: {}", resourcePath, e);
        }
        return null;
    }

    @Override
    public Stream<LanternResource> walkResources(ResourcePath path, int maxDepth) {
        checkNotNull(path, "path");
        final LanternResourcePath path1 = (LanternResourcePath) path;
        if (path1.isFile() || maxDepth <= 0) {
            return Stream.empty();
        }
        Stream<LanternResource> stream = null;
        try {
            // If a wildcard is used, there is some special handling required
            if (path1.getNamespace().isEmpty()) {
                // A list with all the namespace directories
                for (Path namespacePath : Files.list(this.dataRoot).collect(Collectors.toList())) {
                    final String namespace = namespacePath.getFileName().toString();
                    // Check if the namespace is valid, don't want to break things
                    if (LanternResourcePath.NAMESPACE_PATTERN.matcher(namespace).matches()) {
                        Lantern.getLogger().warn("Found a invalid namespace {} within the {} pack.", namespace, getName().toPlain());
                    } else {
                        final LanternResourcePath newPath = LanternResourcePath.uncheckedOf(namespace, path.getPath());
                        final Stream<LanternResource> extraStream = walkResources(
                                newPath, namespacePath.resolve(path.getPath()), maxDepth);
                        if (extraStream != null) {
                            stream = stream == null ? extraStream : Stream.concat(stream, extraStream);
                        }
                    }
                }
            } else {
                stream = walkResources(path1, this.dataRoot.resolve(path.getNamespace()).resolve(path.getPath()), maxDepth);
            }
        } catch (IOException e) {
            Lantern.getLogger().error("Failed to walk resource path: {}", path, e);
        }
        return stream == null ? Stream.empty() : stream;
    }

    @Nullable
    private Stream<LanternResource> walkResources(LanternResourcePath resourcePath, Path start, int maxDepth) {
        if (!Files.exists(start)) {
            return null;
        }
        try {
            return Files.walk(start, maxDepth).map(path -> {
                final Path relPath = path.relativize(start);
                // Backslashes are not supported so replace them
                final String path1 = relPath.toString().replace('\\', '/');
                // Check if it's a invalid path for a resource and ignore .mcmeta files
                if (path1.endsWith(".mcmeta") ||
                        !LanternResourcePath.PATH_PATTERN.matcher(path1).matches()) {
                    return null;
                }
                final LanternResourcePath newResourcePath = LanternResourcePath.uncheckedOf(resourcePath.getNamespace(),
                        resourcePath.getPath() + '/' + path1);
                // Create the new path
                return this.cache.get(newResourcePath, newResourcePath1 -> load(newResourcePath1, path));
            });
        } catch (IOException e) {
            Lantern.getLogger().error("Failed to walk resource path: {}", resourcePath, e);
        }
        return null;
    }

    @Override
    public Collection<Resource> getAllResources() {
        return walkResources(LanternResourcePath.ALL).collect(Collectors.toList());
    }

    @Override
    public Optional<Resource> getResource(ResourcePath path) {
        checkNotNull(path, "path");
        final LanternResourcePath path1 = (LanternResourcePath) path;
        if (path1.isDirectory() || path1.getNamespace().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.cache.get(path1, path2 -> {
            final Path filePath = this.dataRoot.resolve(path.getNamespace()).resolve(path.getPath());
            if (!Files.exists(filePath)) {
                return null;
            }
            return load(path1, filePath);
        }));
    }
}
