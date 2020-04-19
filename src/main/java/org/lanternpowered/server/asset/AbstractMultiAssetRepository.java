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
        map.forEach((key, value) -> builder.putAll(key, value.values()));
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