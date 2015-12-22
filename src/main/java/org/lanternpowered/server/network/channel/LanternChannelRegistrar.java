/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.network.channel;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;
import static org.lanternpowered.server.util.Conditions.checkPlugin;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutRegisterChannels;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.ChannelBinding.IndexedMessageChannel;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.ChannelRegistrar;
import org.spongepowered.api.network.ChannelRegistrationException;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.plugin.PluginContainer;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class LanternChannelRegistrar implements ChannelRegistrar {

    private final Map<String, LanternChannelBinding> bindings = Maps.newConcurrentMap();
    private final Server server;

    public LanternChannelRegistrar(Server server) {
        this.server = server;
    }

    @Override
    public IndexedMessageChannel createChannel(Object plugin, String channel) throws ChannelRegistrationException {
        return (IndexedMessageChannel) this.create(plugin, channel, false);
    }

    @Override
    public LanternRawDataChannel createRawChannel(Object plugin, String channel) throws ChannelRegistrationException {
        return (LanternRawDataChannel) this.create(plugin, channel, true);
    }

    private LanternChannelBinding create(Object plugin, String channel, boolean rawChannel) throws ChannelRegistrationException {
        PluginContainer container = checkPlugin(plugin, "plugin");
        checkNotNullOrEmpty(channel, "channel");
        checkArgument(channel.length() <= 20, "channel length may not be longer then 20");
        if (!this.isChannelAvailable(channel)) {
            throw new ChannelRegistrationException("Channel with name \"" + channel + "\" is already registered!");
        }
        LanternChannelBinding binding;
        if (rawChannel) {
            binding = new LanternRawDataChannel(this, channel, container);
        } else {
            binding = new LanternIndexedMessageChannel(this, channel, container);
        }
        binding.bound = true;
        MessagePlayInOutRegisterChannels message = new MessagePlayInOutRegisterChannels(Sets.newHashSet(channel));
        for (Player player : this.server.getOnlinePlayers()) {
            ((Session) player.getConnection()).send(message);
        }
        return binding;
    }

    @Override
    public void unbindChannel(ChannelBinding channel) {
        LanternChannelBinding binding = (LanternChannelBinding) checkNotNull(channel, "channel");
        if (binding.bound) {
            binding.bound = false;
            this.bindings.remove(channel.getName());
        }
    }

    @Override
    public Set<String> getRegisteredChannels(Platform.Type side) {
        return ImmutableSet.copyOf(this.bindings.keySet());
    }

    @Override
    public boolean isChannelAvailable(String channelName) {
        if (this.bindings.containsKey(channelName) || channelName.startsWith("MC|") ||
                channelName.startsWith("\001") || channelName.startsWith("FML")) {
            return false;
        }
        return true;
    }

    void sendPayloadChannelBuf(Player player, String channel, Consumer<ChannelBuf> payload) {
        this.sendPayloadToAll(channel, buf -> payload.accept(new LanternChannelBuf(buf)));
    }

    void sendPayload(Player player, String channel, Consumer<ByteBuf> payload) {
        checkNotNull(player, "player");
        checkNotNull(payload, "payload");
        Session session = ((LanternPlayer) player).getConnection();
        if (session.getRegisteredChannels().contains(channel)) {
            ByteBuf buf = Unpooled.buffer();
            payload.accept(buf);
            session.send(new MessagePlayInOutChannelPayload(channel, buf));
        }
    }

    void sendPayloadToAllChannelBuf(String channel, Consumer<ChannelBuf> payload) {
        this.sendPayloadToAll(channel, buf -> payload.accept(new LanternChannelBuf(buf)));
    }

    void sendPayloadToAll(String channel, Consumer<ByteBuf> payload) {
        checkNotNull(payload, "payload");
        Iterator<Player> players = this.server.getOnlinePlayers().stream().filter(
                player -> ((LanternPlayer) player).getConnection().getRegisteredChannels().contains(channel)).iterator();
        if (players.hasNext()) {
            ByteBuf buf = Unpooled.buffer();
            payload.accept(buf);
            Message msg = new MessagePlayInOutChannelPayload(channel, buf);
            players.forEachRemaining(player -> ((LanternPlayer) player).getConnection().send(msg));
        }
    }

    public void handlePlayload(ByteBuf buf, String channel, RemoteConnection connection) {
        LanternChannelBinding binding = this.bindings.get(channel);
        if (binding != null) {
            binding.handlePayload(buf, connection);
        }
    }
}
