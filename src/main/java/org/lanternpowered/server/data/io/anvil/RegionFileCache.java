package org.lanternpowered.server.data.io.anvil;

import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import org.lanternpowered.server.game.LanternGame;

/**
 * A simple cache and wrapper for efficiently accessing multiple RegionFiles
 * simultaneously.
 */
public class RegionFileCache {

    private static final int MAX_CACHE_SIZE = 256;

    private final Map<File, Reference<RegionFile>> cache = new HashMap<>();
    private final String extension;
    private final File regionDir;

    public RegionFileCache(File basePath, String extension) {
        this.regionDir = new File(basePath, "region");
        this.extension = extension;
    }

    public RegionFile getRegionFile(int chunkX, int chunkZ) throws IOException {
        File file = new File(this.regionDir, "r." + (chunkX >> 5) + "." + (chunkZ >> 5) + this.extension);
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