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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.asset.AssetManager;

import java.util.Optional;

@Singleton
public class LanternAssetManager implements AssetManager {

    private final AssetRepository assetRepository;

    @Inject
    public LanternAssetManager(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    public AssetRepository getRepository() {
        return this.assetRepository;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<Asset> getAsset(Object plugin, String name) {
        return (Optional) this.assetRepository.get(plugin, name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<Asset> getAsset(String name) {
        return (Optional) this.assetRepository.get(Lantern.getMinecraftPlugin(), name);
    }
}
