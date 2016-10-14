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

import org.lanternpowered.api.asset.Asset;
import org.spongepowered.api.plugin.PluginContainer;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

public class DirectoryAssetRepository extends AbstractAssetRepository {

    private final List<ReloadListener> reloadListeners = new ArrayList<>();
    private final Path directory;

    public DirectoryAssetRepository(Path directory) {
        this.directory = checkNotNull(directory, "directory");
    }

    @Nullable
    @Override
    protected URL getAssetURL(PluginContainer plugin, Path path) {
        path = this.directory.resolve(path);
        try {
            return Files.exists(path) ? path.toUri().toURL() : null;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void reload() {
        final Iterator<Map.Entry<Path, Optional<Asset>>> it = this.loadedAssets.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<Path, Optional<Asset>> entry = it.next();
            if (entry.getValue().isPresent()) {
                final LanternAsset asset = (LanternAsset) entry.getValue().get();
                // The file was removed from the repo
                if (this.getAssetURL(asset.getOwner(), asset.path) == null) {
                    it.remove();
                }
            } else {
                // Clear the path since it can be rechecked
                it.remove();
            }
        }
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
