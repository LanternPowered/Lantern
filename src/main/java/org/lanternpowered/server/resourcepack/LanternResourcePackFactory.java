package org.lanternpowered.server.resourcepack;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.resourcepack.ResourcePackFactory;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;

public class LanternResourcePackFactory implements ResourcePackFactory {

    private static class CacheKey {

        private final URL url;
        private final boolean unchecked;

        public CacheKey(URL url, boolean unchecked) {
            this.unchecked = unchecked;
            this.url = url;
        }

        @Override
        public int hashCode() {
            return 31 * this.url.hashCode() + (this.unchecked ? 1 : 0);
        }
    }

    private final Map<String, ResourcePack> resourcePacks = Maps.newConcurrentMap();
    private final Map<CacheKey, ResourcePack> resourcePacksByKey = Maps.newConcurrentMap();

    public ResourcePack fromUrl(URL url, boolean unchecked) throws IOException {
        CacheKey key = new CacheKey(url, unchecked);
        if (this.resourcePacksByKey.containsKey(key)) {
            return this.resourcePacksByKey.get(key);
        }
        String url0 = url.getFile();
        url0 = url0.substring(url0.lastIndexOf('/') + 1).split("\\?")[0].split("#")[0];
        int index = url0.lastIndexOf('.');
        if (index != -1) {
            url0 = url0.substring(0, index);
        }
        String hash = null;
        if (!unchecked) {
            InputStream is = url.openConnection().getInputStream();
            hash = Hashing.sha1().hashBytes(ByteStreams.toByteArray(is)).toString();
            is.close();
        }
        String name = url0.replaceAll("[^\\p{L}\\p{Nd}]+", "");
        // TODO: Better identifier generation
        ResourcePack resourcePack = new LanternResourcePack(url, name, "ResourcePack{HashCode=" +
                url.hashCode() + ",Unchecked=" + unchecked,
                Optional.fromNullable(hash));
        this.resourcePacks.put(resourcePack.getId(), resourcePack);
        this.resourcePacksByKey.put(key, resourcePack);
        return resourcePack;
    }

    public Optional<ResourcePack> getIfPresent(String id) {
        return Optional.fromNullable(this.resourcePacks.get(id));
    }

    @Override
    public ResourcePack fromUrl(URL url) throws FileNotFoundException {
        try {
            return this.fromUrl(url, false);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResourcePack fromUrlUnchecked(URL url) {
        try {
            return this.fromUrl(url, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
