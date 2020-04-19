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
package org.lanternpowered.server.network;

import org.spongepowered.api.network.PlayerConnection;
import org.spongepowered.api.network.RemoteConnection;

import java.net.InetSocketAddress;

/**
 * A {@link RemoteConnection} that wraps around another {@link RemoteConnection}. Mainly
 * used to hide the {@link PlayerConnection} when passing in a {@link NetworkSession}
 * in status events.
 */
public final class WrappedRemoteConnection implements RemoteConnection {

    private final RemoteConnection remoteConnection;

    public WrappedRemoteConnection(RemoteConnection remoteConnection) {
        this.remoteConnection = remoteConnection;
    }

    @Override
    public InetSocketAddress getAddress() {
        return this.remoteConnection.getAddress();
    }

    @Override
    public InetSocketAddress getVirtualHost() {
        return this.remoteConnection.getVirtualHost();
    }

    @Override
    public String toString() {
        return this.remoteConnection.toString();
    }
}
