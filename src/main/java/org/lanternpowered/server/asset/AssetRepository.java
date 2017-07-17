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

import com.google.common.collect.Multimap;
import org.lanternpowered.api.asset.Asset;
import org.spongepowered.api.plugin.Plugin;

import java.util.Collection;
import java.util.Optional;

public interface AssetRepository {

    /**
     * Returns the {@link Asset} of the specified name for the specified
     * {@link Plugin} instance.
     *
     * @param plugin Plugin instance
     * @param name Name of resource to retrieve
     * @return Asset if present, empty otherwise
     */
    Optional<Asset> get(Object plugin, String name);

    /**
     * Returns the {@link Asset} of the specified id.
     *
     * @param id Id of resource to retrieve
     * @return Asset if present, empty otherwise
     */
    Optional<Asset> get(String id);

    /**
     * Gets the loaded {@link Asset}s.
     *
     * @return The loaded assets
     */
    Collection<Asset> getLoadedAssets();

    /**
     * Loads and gets all the {@link Asset}s in
     * the specified directory path.
     * <p>
     * The files in children directories can also be checked with the
     * {@code checkChildDirectories} parameter, otherwise will these
     * directories be ignored.
     * <p>
     * The path can be specified in multiple ways, depending on which 'plugin'
     * that should be targeted or if all the assets are required.
     * <p>
     * {@code *:path/to/directory} targets the directory within every 'plugin'
     * directory, for example: {@code data/lantern/path/to/directory} and
     * {@code data/minecraft/path/to/directory}
     * <p>
     * {@code path/to/directory} targets the directory within the minecraft
     * directory, for example: {@code data/minecraft/path/to/directory}
     * <p>
     * {@code lantern:path/to/directory} targets the directory within the lantern
     * directory, for example: {@code data/lantern/path/to/directory}
     *
     * @param path The path
     * @param checkChildDirectories Whether child directories of the
     *                              specified path also should be checked
     * @return The assets
     */
    Collection<Asset> getAssets(String path, boolean checkChildDirectories);

    /**
     * Loads and gets all the {@link Asset}s in the specified directory path
     * as {@link Multimap} structure. All the assets within the specified directory
     * path should be put in children directories, these directory names
     * are used as the keys in the {@link Multimap}. All the {@link Asset}s within
     * each child directory will be put in the values.
     * <p>
     * The files in children directories can also be checked with the
     * {@code checkChildDirectories} parameter, otherwise will these
     * directories be ignored.
     * <p>
     * The path can be specified in multiple ways, depending on which 'plugin'
     * that should be targeted or if all the assets are required.
     * <p>
     * {@code *:path/to/directory} targets the directory within every 'plugin'
     * directory, for example: {@code data/lantern/path/to/directory} and
     * {@code data/minecraft/path/to/directory}
     * <p>
     * {@code path/to/directory} targets the directory within the minecraft
     * directory, for example: {@code data/minecraft/path/to/directory}
     * <p>
     * {@code lantern:path/to/directory} targets the directory within the lantern
     * directory, for example: {@code data/lantern/path/to/directory}
     *
     * @param path The path
     * @param checkChildDirectories Whether child directories of the
     *                              specified path also should be checked
     * @return The assets
     */
    Multimap<String, Asset> getAssetsMap(String path, boolean checkChildDirectories);

    /**
     * Reloads the {@link Asset}s in this repository.
     */
    void reload();

    /**
     * Adds a {@link ReloadListener} to this asset repository.
     *
     * @param reloadListener The reload listener
     */
    void addReloadListener(ReloadListener reloadListener);

    /**
     * Removes the {@link ReloadListener} from this asset repository.
     *
     * @param reloadListener The reload listener
     */
    void removeReloadListener(ReloadListener reloadListener);
}
