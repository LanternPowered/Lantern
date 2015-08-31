package org.lanternpowered.server.resourcepack;

import java.net.URL;

import org.spongepowered.api.resourcepack.ResourcePack;

import com.google.common.base.Optional;

public class LanternResourcePack implements ResourcePack {

    private final URL url;
    private final String name;
    private final String id;
    private final Optional<String> hash;

    public LanternResourcePack(URL url, String name, String id, Optional<String> hash) {
        this.hash = hash;
        this.name = name;
        this.url = url;
        this.id = id;
    }

    @Override
    public URL getUrl() {
        return this.url;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Optional<String> getHash() {
        return this.hash;
    }
}
