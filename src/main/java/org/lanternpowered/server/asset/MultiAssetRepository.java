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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultiAssetRepository extends AbstractMultiAssetRepository {

    private final List<AssetRepository> repositories = new CopyOnWriteArrayList<>();

    /**
     * Adds a {@link AssetRepository} to this multi asset repository.
     *
     * @param assetRepository The asset repository
     */
    public void add(AssetRepository assetRepository) {
        add(0, checkNotNull(assetRepository, "assetRepository"));
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
    protected Collection<AssetRepository> getRepositories() {
        return this.repositories;
    }
}
