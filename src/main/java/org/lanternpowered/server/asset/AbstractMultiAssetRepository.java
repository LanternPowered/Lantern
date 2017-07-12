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

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import org.lanternpowered.api.asset.Asset;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

abstract class AbstractMultiAssetRepository implements AssetRepository {

    private final List<ReloadListener> reloadListeners = new CopyOnWriteArrayList<>();

    protected abstract Collection<AssetRepository> getRepositories();

    @Override
    public Optional<Asset> get(Object plugin, String name) {
        for (AssetRepository repository : getRepositories()) {
            final Optional<Asset> asset = repository.get(plugin, name);
            if (asset.isPresent()) {
                return asset;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Asset> get(String name) {
        for (AssetRepository repository : getRepositories()) {
            final Optional<Asset> asset = repository.get(name);
            if (asset.isPresent()) {
                return asset;
            }
        }
        return Optional.empty();
    }

    @Override
    public Collection<Asset> getLoadedAssets() {
        final ImmutableSet.Builder<Asset> builder = ImmutableSet.builder();
        for (AssetRepository repository : getRepositories()) {
            builder.addAll(repository.getLoadedAssets());
        }
        return builder.build();
    }

    @Override
    public Collection<Asset> getAssets(String path, boolean checkChildDirectories) {
        final Map<String, Asset> map = new HashMap<>();
        for (AssetRepository repository : getRepositories()) {
            final Collection<Asset> assets = repository.getAssets(path, checkChildDirectories);
            assets.forEach(asset -> map.putIfAbsent(asset.getId(), asset));
        }
        return Collections.unmodifiableCollection(map.values());
    }

    @Override
    public Multimap<String, Asset> getAssetsMap(String path, boolean checkChildDirectories) {
        final Map<String, Map<String, Asset>> map = new HashMap<>();
        for (AssetRepository repository : getRepositories()) {
            final Multimap<String, Asset> multimap = repository.getAssetsMap(path, checkChildDirectories);
            multimap.entries().forEach(entry -> {
                final Map<String, Asset> entries = map.computeIfAbsent(entry.getKey(), key -> new HashMap<>());
                entries.putIfAbsent(entry.getValue().getId(), entry.getValue());
            });
        }
        final ImmutableMultimap.Builder<String, Asset> builder = ImmutableMultimap.builder();
        map.entrySet().forEach(entry -> builder.putAll(entry.getKey(), entry.getValue().values()));
        return builder.build();
    }

    @Override
    public void reload() {
        getRepositories().forEach(AssetRepository::reload);
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