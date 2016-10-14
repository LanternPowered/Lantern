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
