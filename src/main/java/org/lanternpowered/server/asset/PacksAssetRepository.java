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

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.plugin.LanternPluginManager;
import org.spongepowered.api.util.Coerce;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PacksAssetRepository extends AbstractMultiAssetRepository {

    private static final Gson GSON = new Gson();
    private static final String PACK_META = "pack.mcmeta";

    private static final class PackMetaRoot {

        @SerializedName("pack") public Pack pack;

        private static final class Pack {

            /**
             * The pack format, normal numbering means vanilla. The
             * current supported version is 3.
             * <p>
             * (@code lantern} can also be specified as version, vanilla
             * servers/clients will ignore this but the lantern server
             * will load them.
             * {@link #version} is also supported in this case.
             */
            @SerializedName("pack_format") public String format;

            /**
             * Only usable in case {@link #format} equals {@code lantern}.
             */
            @SerializedName("pack_version") public int version;
        }
    }

    private final Path directory;
    private final LanternPluginManager pluginManager;
    private final Map<Path, AssetRepository> repositories = new ConcurrentHashMap<>();

    public PacksAssetRepository(LanternPluginManager pluginManager, Path directory) {
        this.pluginManager = pluginManager;
        this.directory = directory;
        load();
    }

    @Override
    protected Collection<AssetRepository> getRepositories() {
        return this.repositories.values();
    }

    @Override
    public void reload() {
        final Iterator<Map.Entry<Path, AssetRepository>> it = this.repositories.entrySet().iterator();
        while (it.hasNext()) {
            // Remove references to deleted directories
            final Map.Entry<Path, AssetRepository> entry = it.next();
            final AssetRepository repo = entry.getValue();
            Path path = null;
            if (repo instanceof DirectoryAssetRepository) {
                path = ((DirectoryAssetRepository) repo).getDirectory();
            } else if (repo instanceof FileAssetRepository) {
                path = ((FileAssetRepository) repo).getFile();
            }
            if (path != null && !Files.exists(path)) {
                it.remove();
            }
        }
        load();
        super.reload();
    }

    private void load() {
        if (!Files.exists(this.directory)) {
            try {
                Files.createDirectories(this.directory);
            } catch (IOException e) {
                Lantern.getLogger().error("Failed to create the asset repository directory", e);
            }
        }
        try {
            Files.list(this.directory).forEach(path -> {
                if (this.repositories.containsKey(path)) {
                    return;
                }
                AbstractAssetRepository assetRepository = null;
                if (Files.isDirectory(path)) {
                    assetRepository = new DirectoryAssetRepository(this.pluginManager, path);
                } else if (path.toString().endsWith(".zip")) {
                    assetRepository = new FileAssetRepository(this.pluginManager, path);
                }
                if (assetRepository != null) {
                    final URL url = assetRepository.getAssetURL(Paths.get(PACK_META));
                    if (url == null) {
                        Lantern.getLogger().warn("The '{}' file is missing in the data pack: {}", PACK_META, path);
                    } else {
                        try (InputStream is = url.openStream()) {
                            final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                            final PackMetaRoot.Pack pack = GSON.fromJson(reader, PackMetaRoot.class).pack;

                            final Optional<Integer> optFormat = Coerce.asInteger(pack.format);
                            if (optFormat.isPresent()) {
                                final int format = optFormat.get();
                                if (format < 3) {
                                    Lantern.getLogger().warn("The minecraft data pack version '{}' is no longer supported, in the data pack {}",
                                            format, path);
                                }
                            } else if (!pack.format.equals("lantern")) {
                                Lantern.getLogger().warn("The data pack format '{}' is unknown, it must be a number or 'lantern',"
                                        + "in the data pack {}", pack.format, path);
                            } else {
                                this.repositories.put(path, assetRepository);
                                Lantern.getLogger().debug("Registered a data pack: " + path);
                            }
                        } catch (IOException e) {
                            Lantern.getLogger().error("Failed to read the data pack info for: {}", path, e);
                        }
                    }
                }
            });
        } catch (IOException e) {
            Lantern.getLogger().error("Failed to search for repository directories", e);
        }
    }
}
