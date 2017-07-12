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
package org.lanternpowered.server.asset;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.lanternpowered.api.asset.Asset;
import org.lanternpowered.server.plugin.LanternPluginManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

public class DirectoryAssetRepository extends AbstractAssetRepository {

    private final Path directory;

    public DirectoryAssetRepository(LanternPluginManager pluginManager, Path directory) {
        super(pluginManager);
        this.directory = checkNotNull(directory, "directory");
    }

    public Path getDirectory() {
        return this.directory;
    }

    @Nullable
    @Override
    protected URL getAssetURL(Path path) {
        path = this.directory.resolve(path);
        try {
            return Files.exists(path) ? path.toUri().toURL() : null;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Collection<Asset> getAssets(String path, boolean checkChildDirectories) {
        final ImmutableList.Builder<Asset> builder = ImmutableList.builder();
        final Pattern pattern = Pattern.compile(generateRegex(path));
        final int length = this.directory.toString().length() + 1;
        try {
            Files.walkFileTree(this.directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    final String file0 = file.toString().substring(length).replace('\\', '/');
                    final Matcher matcher = pattern.matcher(file0);
                    if (matcher.matches()) {
                        final String id = matcher.group(2).toLowerCase(Locale.ENGLISH);
                        int index;
                        if (!checkChildDirectories && (index = id.indexOf('/')) != -1 && id.lastIndexOf('/') != index) {
                            return FileVisitResult.CONTINUE;
                        }
                        final String plugin = matcher.group(1).toLowerCase(Locale.ENGLISH);
                        builder.add(registerAsset(plugin, plugin + ':' + id, file));
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return builder.build();
    }

    @Override
    public Multimap<String, Asset> getAssetsMap(String path, boolean checkChildDirectories) {
        final ImmutableMultimap.Builder<String, Asset> builder = ImmutableMultimap.builder();
        final Pattern pattern = Pattern.compile(generateRegex(path));
        final int length = this.directory.toString().length() + 1;
        try {
            Files.walkFileTree(this.directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    final String file0 = file.toString().substring(length).replace('\\', '/');
                    final Matcher matcher = pattern.matcher(file0);
                    if (matcher.matches()) {
                        final String id = matcher.group(2).toLowerCase(Locale.ENGLISH);
                        final int index = id.indexOf('/');
                        if (index == -1 || (checkChildDirectories && index != id.lastIndexOf('/'))) {
                            return FileVisitResult.CONTINUE;
                        }
                        final String plugin = matcher.group(1).toLowerCase(Locale.ENGLISH);
                        builder.put(id.substring(0, index), registerAsset(plugin, plugin + ':' + id, file));
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return builder.build();
    }

    @Override
    public void reload() {
        final Iterator<Map.Entry<String, Optional<Asset>>> it = this.loadedAssets.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<String, Optional<Asset>> entry = it.next();
            if (entry.getValue().isPresent()) {
                final LanternAsset asset = (LanternAsset) entry.getValue().get();
                // The file was removed from the repo
                if (getAssetURL(asset.path) == null) {
                    it.remove();
                }
            } else {
                // Clear the path since it can be rechecked
                it.remove();
            }
        }
        super.reload();
    }
}
