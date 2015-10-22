/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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
package org.lanternpowered.server.data.io.anvil;

import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.lanternpowered.server.game.LanternGame;

/**
 * A simple cache and wrapper for efficiently accessing multiple RegionFiles
 * simultaneously.
 */
public class RegionFileCache {

    public static final String REGION_FILE_EXTENSION = "mca";
    public static final Pattern REGION_FILE_PATTERN = Pattern.compile("r\\.([-+]?[0-9])+.([-+]?[0-9])+\\." + REGION_FILE_EXTENSION);

    private static final int MAX_CACHE_SIZE = 256;

    private final Map<File, Reference<RegionFile>> cache = new HashMap<>();
    private final File regionDir;

    public RegionFileCache(File basePath, String extension) {
        this.regionDir = new File(basePath, "region");
    }

    public RegionFile getRegionFile(int chunkX, int chunkZ) throws IOException {
        File file = new File(this.regionDir, "r." + (chunkX >> 5) + "." + (chunkZ >> 5) + REGION_FILE_EXTENSION);
        Reference<RegionFile> ref = this.cache.get(file);

        if (ref != null && ref.get() != null) {
            return ref.get();
        }
        if (!this.regionDir.isDirectory() && !this.regionDir.mkdirs()) {
            LanternGame.log().warn("Failed to create directory: " + this.regionDir);
        }
        if (this.cache.size() >= MAX_CACHE_SIZE) {
            this.clear();
        }

        RegionFile reg = new RegionFile(file);
        this.cache.put(file, new SoftReference<>(reg));
        return reg;
    }

    public void clear() throws IOException {
        for (Reference<RegionFile> ref : this.cache.values()) {
            RegionFile value = ref.get();
            if (value != null) {
                value.close();
            }
        }
        this.cache.clear();
    }

}