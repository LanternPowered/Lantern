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
package org.lanternpowered.server.asset;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.lanternpowered.api.asset.Asset;
import org.lanternpowered.server.plugin.LanternPluginManager;
import org.lanternpowered.server.util.PathUtils;

import java.io.IOException;
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

import org.checkerframework.checker.nullness.qual.Nullable;

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
        return Files.exists(path) ? PathUtils.toURL(path) : null;
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
