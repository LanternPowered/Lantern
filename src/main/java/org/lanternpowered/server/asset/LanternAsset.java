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

import com.google.common.base.MoreObjects;
import org.lanternpowered.api.asset.Asset;
import org.spongepowered.api.plugin.PluginContainer;

import java.net.URL;
import java.nio.file.Path;

final class LanternAsset implements Asset {

    private final PluginContainer plugin;
    private final String id;
    private final URL url;
    final Path path;

    LanternAsset(PluginContainer plugin, String id, URL url, Path path) {
        this.plugin = plugin;
        this.url = url;
        this.id = id;
        this.path = path;
    }

    @Override
    public PluginContainer getOwner() {
        return this.plugin;
    }

    @Override
    public URL getUrl() {
        return this.url;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("plugin", this.plugin.getId())
                .add("id", this.id)
                .add("url", this.url.toString())
                .toString();
    }
}
