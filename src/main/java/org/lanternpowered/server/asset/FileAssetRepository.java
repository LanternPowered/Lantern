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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.lanternpowered.api.asset.Asset;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.plugin.LanternPluginManager;
import org.lanternpowered.server.util.PathUtils;
import org.lanternpowered.server.util.UncheckedThrowables;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.checkerframework.checker.nullness.qual.Nullable;

class FileAssetRepository extends AbstractAssetRepository implements Closeable {

    private final Path file;
    private final URL fileUrl;

    @Nullable private ZipFile zipFile;

    FileAssetRepository(LanternPluginManager pluginManager, Path file) {
        super(pluginManager);
        this.file = file;
        this.fileUrl = PathUtils.toURL(file);
    }

    public Path getFile() {
        return this.file;
    }

    private ZipFile getZipFile() {
        if (this.zipFile == null) {
            try {
                this.zipFile = new ZipFile(this.file.toFile());
            } catch (IOException e) {
                throw new IllegalStateException("Failed to open the asset zip file", e);
            }
        }
        return this.zipFile;
    }

    @Nullable
    @Override
    protected URL getAssetURL(Path path) {
        final String path0 = path.toString().replace(File.separatorChar, '/');
        // Check if the entry exists
        if (getZipFile().getEntry(path0) == null) {
            return null;
        }
        // Generate a asset url
        return generateAssetURL(path0);
    }

    private URL generateAssetURL(String path) {
        try {
            // Generate a URL that targets a file inside a zip file
            return new URL("jar:" + this.fileUrl + "!/" + path);
        } catch (MalformedURLException e) {
            throw UncheckedThrowables.throwUnchecked(e);
        }
    }

    @Override
    public Collection<Asset> getAssets(String path, boolean checkChildDirectories) {
        final ImmutableList.Builder<Asset> builder = ImmutableList.builder();
        final Enumeration<? extends ZipEntry> enumeration = getZipFile().entries();
        final Pattern pattern = Pattern.compile(generateRegex(path));
        while (enumeration.hasMoreElements()) {
            final ZipEntry zipEntry = enumeration.nextElement();
            final String name = zipEntry.getName().replace('\\', '/');
            final Matcher matcher = pattern.matcher(name);
            if (matcher.matches()) {
                final String id = matcher.group(2).toLowerCase(Locale.ENGLISH);
                int index;
                if (!checkChildDirectories && (index = id.indexOf('/')) != -1 && id.lastIndexOf('/') != index) {
                    continue;
                }
                final String plugin = matcher.group(1).toLowerCase(Locale.ENGLISH);
                builder.add(registerAsset(plugin, plugin + ':' + id,
                        generateAssetURL(name), Paths.get(name)));
            }
        }
        return builder.build();
    }

    @Override
    public Multimap<String, Asset> getAssetsMap(String path, boolean checkChildDirectories) {
        final ImmutableMultimap.Builder<String, Asset> builder = ImmutableMultimap.builder();
        final Enumeration<? extends ZipEntry> enumeration = getZipFile().entries();
        final Pattern pattern = Pattern.compile(generateRegex(path));
        while (enumeration.hasMoreElements()) {
            final ZipEntry zipEntry = enumeration.nextElement();
            final String name = zipEntry.getName().replace('\\', '/');
            final Matcher matcher = pattern.matcher(name);
            if (matcher.matches()) {
                final String id = matcher.group(2).toLowerCase(Locale.ENGLISH);
                final int index = id.indexOf('/');
                if (index == -1 || (checkChildDirectories && index != id.lastIndexOf('/'))) {
                    continue;
                }
                final String plugin = matcher.group(1).toLowerCase(Locale.ENGLISH);
                builder.put(id.substring(0, index), registerAsset(plugin, plugin + ':' + id,
                        generateAssetURL(name), Paths.get(name)));
            }
        }
        return builder.build();
    }

    @Override
    public void close() throws IOException {
        if (this.zipFile != null) {
            this.zipFile.close();
            this.zipFile = null;
        }
    }

    @Override
    public void reload() {
        try {
            close();
        } catch (IOException e) {
            Lantern.getLogger().error("Failed to close an asset repository file", e);
        }
        // Refresh all the assets
        this.loadedAssets.clear();
        super.reload();
    }
}
