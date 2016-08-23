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
package org.lanternpowered.server.asset;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultiAssetRepository implements AssetRepository {

    private final List<ReloadListener> reloadListeners = new CopyOnWriteArrayList<>();
    private final List<AssetRepository> repositories = new CopyOnWriteArrayList<>();

    /**
     * Adds a {@link AssetRepository} to this multi asset repository.
     *
     * @param assetRepository The asset repository
     */
    public void add(AssetRepository assetRepository) {
        this.add(0, checkNotNull(assetRepository, "assetRepository"));
    }

    /**
     * Adds a {@link AssetRepository} to this multi asset repository.
     *
     * @param index The index that the asset repository will be loaded
     * @param assetRepository The asset repository
     */
    public void add(int index, AssetRepository assetRepository) {
        if (index >= this.repositories.size()) {
            this.repositories.add(checkNotNull(assetRepository, "assetRepository"));
        } else {
            this.repositories.add(index, checkNotNull(assetRepository, "assetRepository"));
        }
    }

    @Override
    public Optional<Asset> get(Object plugin, String name) {
        for (AssetRepository repository : this.repositories) {
            Optional<Asset> asset = repository.get(plugin, name);
            if (asset.isPresent()) {
                return asset;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Asset> get(String name) {
        for (AssetRepository repository : this.repositories) {
            Optional<Asset> asset = repository.get(name);
            if (asset.isPresent()) {
                return asset;
            }
        }
        return Optional.empty();
    }

    @Override
    public Collection<Asset> getLoadedAssets() {
        final ImmutableList.Builder<Asset> builder = ImmutableList.builder();
        for (AssetRepository repository : this.repositories) {
            builder.addAll(repository.getLoadedAssets());
        }
        return builder.build();
    }

    @Override
    public void reload() {
        this.repositories.forEach(AssetRepository::reload);
        this.reloadListeners.forEach(ReloadListener::onReload);
    }

    @Override
    public void addReloadListener(ReloadListener reloadListener) {
        this.reloadListeners.add(checkNotNull(reloadListener, "reloadListener"));
    }

    @Override
    public void removeReloadListener(ReloadListener reloadListener) {
        this.reloadListeners.remove(checkNotNull(reloadListener, "reloadListener"));
    }
}
