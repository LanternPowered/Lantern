/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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
package org.lanternpowered.server.network.channel;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.function.Consumer;

import org.spongepowered.api.Platform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.network.ChannelBinding;

import com.google.common.collect.Lists;

/**
 * A simple raw data channel, the client side handlers will be ignored since
 * we will only provide a server implementation.
 */
public class LanternRawDataChannel extends LanternChannelBinding implements ChannelBinding.RawDataChannel {

    private final List<RawDataListener> serverDataListeners = Lists.newArrayList();

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
        this.registrar.sendPayloadChannelBuf(player, this.name, payload);
    }

    @Override
    public void sendToServer(Consumer<ChannelBuf> payload) {
        checkState(this.bound);
        checkNotNull(payload, "payload");
    }

    @Override
    public void sendToAll(Consumer<ChannelBuf> payload) {
        checkState(this.bound);
        this.registrar.sendPayloadToAllChannelBuf(this.name, payload);
    }

    @Override
    void handlePayload(ByteBuf buf, RemoteConnection connection) {
        for (RawDataListener listener : this.serverDataListeners) {
            listener.handlePayload(new LanternChannelBuf(buf.copy()), connection, Platform.Type.SERVER);
        }
    }
}
