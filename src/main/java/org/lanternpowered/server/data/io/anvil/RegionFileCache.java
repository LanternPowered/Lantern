/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
/*
 * Copyright (c) 2011-2014 Glowstone - Tad Hardesty
 * Copyright (c) 2010-2011 Lightstone - Graham Edgecombe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.data.io.anvil;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.lanternpowered.server.game.Lantern;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

/**
 * A simple cache and wrapper for efficiently accessing multiple RegionFiles
 * simultaneously.
 */
public class RegionFileCache {

    public static final String DEFAULT_REGION_FILE_EXTENSION = "mca";

    public static final int REGION_COORDINATE_BITS = 5;
    public static final int REGION_SIZE = 1 << REGION_COORDINATE_BITS;
    public static final int REGION_AREA = REGION_SIZE * REGION_SIZE;
    public static final int REGION_MASK = REGION_SIZE - 1;

    private static final int MAX_CACHE_SIZE = 256;

    private final Cache<Long, RegionFile> cache;
    private final String extension;
    private final Pattern filePattern;
    private final Path regionDir;

    public RegionFileCache(Path basePath) {
        this(basePath, DEFAULT_REGION_FILE_EXTENSION);
    }

    public RegionFileCache(Path basePath, String extension) {
        this.extension = extension;
        this.regionDir = basePath.resolve("region");
        this.filePattern = Pattern.compile("^r\\.([-]?[0-9]+)\\.([-]?[0-9]+)\\." + extension + "$");

        try {
            Files.createDirectories(this.regionDir);
        } catch (IOException e){
            Lantern.getLogger().warn("Failed to create directory: " + this.regionDir);
        }

        this.cache = CacheBuilder.<Long, RegionFile>newBuilder()
                .concurrencyLevel(4)
                .maximumSize(MAX_CACHE_SIZE)
                .softValues()
                .removalListener(new RemovalListener<Long, RegionFile>() {
                    @Override
                    public void onRemoval(RemovalNotification<Long, RegionFile> not) {
                        if (not.getValue() != null) {
                            try {
                                not.getValue().close();
                            } catch (IOException ignored) {
                            }
                        }
                    }
                })
                .build();
    }

    public File[] getRegionFiles() {
        return this.regionDir.toFile().listFiles(file -> this.filePattern.matcher(file.getName()).matches());
    }

    public RegionFile getRegionFileByChunk(int chunkX, int chunkZ) throws IOException {
        final int regionX = chunkX >> REGION_COORDINATE_BITS;
        final int regionZ = chunkZ >> REGION_COORDINATE_BITS;

        return this.getRegionFile(regionX, regionZ);
    }

    public RegionFile getRegionFile(int regionX, int regionZ) throws IOException {
        final long coords = (regionZ & 0xffffffffL) << 32 | regionX & 0xffffffffL;

        try {
            return cache.get(coords, () -> new RegionFile(this.regionDir.resolve("r." + regionX + "." + regionZ + "." + this.extension),
                    regionX, regionZ));
        } catch (ExecutionException e) {
            throw new IOException("Unable to load region file (" + regionX + ";" + regionZ + ")");
        }
    }

    public void clear() {
        this.cache.invalidateAll();
    }

    public Pattern getFilePattern() {
        return this.filePattern;
    }
}
