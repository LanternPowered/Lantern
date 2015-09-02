package org.lanternpowered.server.status;

import java.net.InetSocketAddress;

import javax.annotation.Nullable;

import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.status.StatusClient;

import com.google.common.base.Optional;

public class LanternStatusClient implements StatusClient {

    private final InetSocketAddress address;
    private final Optional<InetSocketAddress> virtualHost;
    private final MinecraftVersion version;

    public LanternStatusClient(InetSocketAddress address, MinecraftVersion version, @Nullable InetSocketAddress virtualHost) {
        this.virtualHost = Optional.fromNullable(virtualHost);
        this.address = address;
        this.version = version;
    }

    @Override
    public InetSocketAddress getAddress() {
        return this.address;
    }

    @Override
    public MinecraftVersion getVersion() {
        return this.version;
    }

    @Override
    public Optional<InetSocketAddress> getVirtualHost() {
        return this.virtualHost;
    }
}
