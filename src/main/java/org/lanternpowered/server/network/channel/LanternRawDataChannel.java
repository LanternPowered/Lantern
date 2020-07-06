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
package org.lanternpowered.server.network.channel;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.spongepowered.api.Platform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.plugin.PluginContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A simple raw data channel, the client side handlers will be ignored since
 * we will only provide a server implementation.
 */
final class LanternRawDataChannel extends LanternChannelBinding implements ChannelBinding.RawDataChannel {

    private final List<RawDataListener> serverDataListeners = new ArrayList<>();

    LanternRawDataChannel(LanternChannelRegistrar registrar, String name, PluginContainer owner) {
        super(registrar, name, owner);
    }

    @Override
    public void addListener(RawDataListener listener) {
        this.serverDataListeners.add(checkNotNull(listener, "listener"));
    }

    @Override
    public void addListener(Platform.Type side, RawDataListener listener) {
        checkNotNull(listener, "listener");
        checkNotNull(side, "side");
        if (side == Platform.Type.SERVER) {
            this.serverDataListeners.add(listener);
        }
    }

    @Override
    public void removeListener(RawDataListener listener) {
        checkState(this.bound);
        this.serverDataListeners.remove(checkNotNull(listener, "listener"));
    }

    @Override
    public void sendTo(Player player, Consumer<ChannelBuf> payload) {
        checkState(this.bound);
        getRegistrar().sendPayload(player, getName(), payload::accept);
    }

    @Override
    public void sendToServer(Consumer<ChannelBuf> payload) {
        checkState(this.bound);
        checkNotNull(payload, "payload");
    }

    @Override
    public void sendToAll(Consumer<ChannelBuf> payload) {
        checkState(this.bound);
        getRegistrar().sendPayloadToAll(getName(), payload::accept);
    }

    @Override
    void handlePayload(ByteBuffer buf, RemoteConnection connection) {
        for (RawDataListener listener : this.serverDataListeners) {
            // We slice the buffer, to preserve the reader index for all the listeners,
            // the buffer shouldn't be modified in any way
            listener.handlePayload(buf.slice(), connection, Platform.Type.SERVER);
        }
    }
}
