/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.resourcepack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.resourcepack.ResourcePackFactory;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;

public final class LanternResourcePackFactory implements ResourcePackFactory {

    private static class CacheKey {

        private final URI uri;
        private final boolean unchecked;

        public CacheKey(URI uri, boolean unchecked) {
            this.unchecked = unchecked;
            this.uri = uri;
        }

        @Override
        public int hashCode() {
            return 31 * this.uri.hashCode() + (this.unchecked ? 1 : 0);
        }
    }

    private final Map<String, ResourcePack> resourcePacks = Maps.newConcurrentMap();
    private final Map<CacheKey, ResourcePack> resourcePacksByKey = Maps.newConcurrentMap();
    private final Map<String, ResourcePack> resourcePacksByHash = Maps.newConcurrentMap();

    // The folder the level resource packs should be stored if
    // they should be hashed, not sure how sponge will handle it
    private final File levelPacksFolder = new File("resource-packs");

    public ResourcePack fromUri(URI uri, boolean unchecked) throws IOException {
        CacheKey key = new CacheKey(uri, unchecked);
        if (this.resourcePacksByKey.containsKey(key)) {
            return this.resourcePacksByKey.get(key);
        }
        String path = uri.toString();
        String plainPath = path.replaceAll("[^\\p{L}\\p{Nd}]+", "");
        String hash = null;
        String id = "{URI:" + path;
        if (!unchecked) {
            InputStream is;
            if (path.startsWith("level://")) {
                String path0 = path.replaceFirst("level://", "");
                File file = new File(this.levelPacksFolder, path0);
                if (!file.exists()) {
                    throw new FileNotFoundException("Cannot find the file: \"" + file.getAbsolutePath() + "\" which" +
                            " is required to generate the hash for \"" + path + "\"");
                }
                is = file.toURI().toURL().openStream();
            } else {
                is = uri.toURL().openStream();
            }
            hash = Hashing.sha1().hashBytes(ByteStreams.toByteArray(is)).toString();
            id += ";Hash:" + hash;
            is.close();
        }
        id += "}";
        ResourcePack resourcePack = new LanternResourcePack(uri, plainPath, id, Optional.ofNullable(hash));
        if (hash != null) {
            this.resourcePacksByHash.put(hash, resourcePack);
        }
        this.resourcePacks.put(id, resourcePack);
        this.resourcePacksByKey.put(key, resourcePack);
        return resourcePack;
    }

    public Optional<ResourcePack> getById(String id) {
        return Optional.ofNullable(this.resourcePacks.get(id));
    }

    public Optional<ResourcePack> getByHash(String hash) {
        return Optional.ofNullable(this.resourcePacksByHash.get(hash));
    }

    @Override
    public ResourcePack fromUri(URI uri) throws FileNotFoundException {
        try {
            return this.fromUri(uri, false);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResourcePack fromUriUnchecked(URI uri) {
        try {
            return this.fromUri(uri, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
