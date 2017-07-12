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
