package org.lanternpowered.server.resourcepack;

import java.net.URI;

import org.spongepowered.api.resourcepack.ResourcePack;

import com.google.common.base.Optional;

public final class LanternResourcePack implements ResourcePack {

    private final URI uri;
    private final String name;
    private final String id;
    private final Optional<String> hash;

    LanternResourcePack(URI uri, String name, String id, Optional<String> hash) {
        this.hash = hash;
        this.name = name;
        this.uri = uri;
        this.id = id;
    }

    @Override
    public URI getUri() {
        return this.uri;
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
