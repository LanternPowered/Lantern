package org.lanternpowered.server.network.rcon;

import java.net.InetSocketAddress;

import org.spongepowered.api.network.RemoteConnection;

public class RconConnection implements RemoteConnection {

    private final InetSocketAddress address;
    private final InetSocketAddress virtualHost;

    public RconConnection(InetSocketAddress address, InetSocketAddress virtualHost) {
        this.virtualHost = virtualHost;
        this.address = address;
    }

    @Override
    public InetSocketAddress getAddress() {
        return this.address;
    }

    @Override
    public InetSocketAddress getVirtualHost() {
        return this.virtualHost;
    }
}
