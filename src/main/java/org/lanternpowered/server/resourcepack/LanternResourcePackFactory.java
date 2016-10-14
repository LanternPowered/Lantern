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
package org.lanternpowered.server.resourcepack;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.resourcepack.ResourcePackFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class LanternResourcePackFactory implements ResourcePackFactory {

    private static final class CacheKey {

        private final URI uri;
        private final boolean unchecked;

        CacheKey(URI uri, boolean unchecked) {
            this.unchecked = unchecked;
            this.uri = uri;
        }

        @Override
        public int hashCode() {
            return 31 * this.uri.hashCode() + (this.unchecked ? 1 : 0);
        }
    }

    private final Map<String, ResourcePack> resourcePacks = new ConcurrentHashMap<>();
    private final Map<CacheKey, ResourcePack> resourcePacksByKey = new ConcurrentHashMap<>();

    // The folder the level resource packs should be stored if
    // they should be hashed, not sure how sponge will handle it
    private final Path levelPacksFolder = Paths.get("resource-packs");

    private ResourcePack fromUri(URI uri, boolean unchecked) throws IOException {
        final CacheKey key = new CacheKey(uri, unchecked);
        if (this.resourcePacksByKey.containsKey(key)) {
            return this.resourcePacksByKey.get(key);
        }
        final String path = uri.toString();
        final String plainPath = path.replaceAll("[^\\p{L}\\p{Nd}]+", "");
        String hash = null;
        String id = "{URI:" + path;
        if (!unchecked) {
            if (path.startsWith("level://")) {
                final String path0 = path.replaceFirst("level://", "");
                final Path file = this.levelPacksFolder.resolve(path0);
                if (!Files.exists(file)) {
                    throw new FileNotFoundException("Cannot find the file: \"" + file.toAbsolutePath() + "\" which" +
                            " is required to generate the hash for \"" + path + "\"");
                }
                uri = file.toUri();
            }
            try (InputStream is = uri.toURL().openStream()) {
                hash = Hashing.sha1().hashBytes(ByteStreams.toByteArray(is)).toString();
            }
            id += ";Hash:" + hash;
        }
        id += "}";
        final ResourcePack resourcePack = new LanternResourcePack(uri, plainPath, id, Optional.ofNullable(hash));
        this.resourcePacks.put(id, resourcePack);
        this.resourcePacksByKey.put(key, resourcePack);
        return resourcePack;
    }

    public Optional<ResourcePack> getById(String id) {
        checkNotNull(id, "id");
        return Optional.ofNullable(this.resourcePacks.get(id));
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
