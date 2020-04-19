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
package org.lanternpowered.server.inject.provider;

import static org.lanternpowered.server.inject.provider.ProviderHelper.provideName;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.lanternpowered.api.inject.InjectionPoint;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.asset.AssetId;
import org.spongepowered.api.asset.AssetManager;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.NoSuchElementException;

public class PluginAssetProvider implements Provider<Asset> {

    @Inject private PluginContainer container;
    @Inject private AssetManager assetManager;
    @Inject private InjectionPoint point;

    @Override
    public Asset get() {
        final AssetId assetId = this.point.getAnnotation(AssetId.class);
        String name;
        if (assetId != null) {
            name = assetId.value();
        } else {
            name = provideName(this.point).orElseThrow(
                    () -> new IllegalStateException("Missing @AssetId or @Named annotation."));
        }
        if (name.indexOf(':') == -1) {
            name = this.container.getId() + ':' + name;
        }
        final String name1 = name;
        return this.assetManager.getAsset(name)
                .orElseThrow(() -> new NoSuchElementException("Cannot find asset " + name1));
    }
}
