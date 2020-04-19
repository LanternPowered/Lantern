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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import org.spongepowered.api.network.RemoteConnection;

import java.net.InetSocketAddress;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class SimpleRemoteConnection implements RemoteConnection {

    private final InetSocketAddress address;
    @Nullable private final InetSocketAddress virtualHostAddress;

    public SimpleRemoteConnection(InetSocketAddress address, @Nullable InetSocketAddress virtualHostAddress) {
        this.address = checkNotNull(address, "address");
        this.virtualHostAddress = virtualHostAddress;
    }

    @Override
    public InetSocketAddress getAddress() {
        return this.address;
    }

    @Override
    public InetSocketAddress getVirtualHost() {
        return this.virtualHostAddress == null ? this.address : this.virtualHostAddress;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("address", this.address)
                .add("virtualHost", this.virtualHostAddress)
                .toString();
    }
}
