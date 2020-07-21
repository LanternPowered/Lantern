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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;
import static org.lanternpowered.server.util.Conditions.checkPlugin;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.NetworkSession;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.ByteBufferAllocator;
import org.lanternpowered.server.network.packet.Packet;
import org.lanternpowered.server.network.vanilla.packet.type.play.ChannelPayloadPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInOutRegisterChannels;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInOutUnregisterChannels;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.ChannelBinding.IndexedMessageChannel;
import org.spongepowered.api.network.ChannelRegistrar;
import org.spongepowered.api.network.ChannelRegistrationException;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.plugin.PluginContainer;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Singleton
public final class LanternChannelRegistrar implements ChannelRegistrar {

    public static final int MAX_NAME_LENGTH = 20;

    private final Map<String, LanternChannelBinding> bindings = new ConcurrentHashMap<>();
    private final Server server;

    @Inject
    public LanternChannelRegistrar(Server server) {
        this.server = server;
    }

    @Override
    public IndexedMessageChannel createChannel(Object plugin, String channel) throws ChannelRegistrationException {
        return (IndexedMessageChannel) create(plugin, channel, false);
    }

    @Override
    public LanternRawDataChannel createRawChannel(Object plugin, String channel) throws ChannelRegistrationException {
        return (LanternRawDataChannel) create(plugin, channel, true);
    }

    @Override
    public Optional<ChannelBinding> getChannel(String channel) {
        checkNotNull(channel, "channel");
        return Optional.ofNullable(this.bindings.get(channel));
    }

    private LanternChannelBinding create(Object plugin, String channel, boolean rawChannel) throws ChannelRegistrationException {
        final PluginContainer container = checkPlugin(plugin, "plugin");
        checkNotNullOrEmpty(channel, "channel");
        checkArgument(channel.length() <= MAX_NAME_LENGTH, "channel length may not be longer then 20");
        if (!isChannelAvailable(channel)) {
            throw new ChannelRegistrationException("Channel with name \"" + channel + "\" is already registered!");
        }
        final LanternChannelBinding binding;
        if (rawChannel) {
            binding = new LanternRawDataChannel(this, channel, container);
        } else {
            binding = new LanternIndexedMessageChannel(this, channel, container);
        }
        binding.bound = true;
        final PacketPlayInOutRegisterChannels message = new PacketPlayInOutRegisterChannels(Sets.newHashSet(channel));
        for (Player player : this.server.getOnlinePlayers()) {
            ((NetworkSession) player.getConnection()).send(message);
        }
        return binding;
    }

    @Override
    public void unbindChannel(ChannelBinding channel) {
        final LanternChannelBinding binding = (LanternChannelBinding) checkNotNull(channel, "channel");
        if (binding.bound) {
            binding.bound = false;
            this.bindings.remove(channel.getName());
            final PacketPlayInOutUnregisterChannels message = new PacketPlayInOutUnregisterChannels(Collections.singleton(channel.getName()));
            for (Player player : this.server.getOnlinePlayers()) {
                ((NetworkSession) player.getConnection()).send(message);
            }
        }
    }

    @Override
    public Set<String> getRegisteredChannels(Platform.Type side) {
        return ImmutableSet.copyOf(this.bindings.keySet());
    }

    @Override
    public boolean isChannelAvailable(String channelName) {
        return !(this.bindings.containsKey(channelName) || channelName.startsWith("MC|") || channelName.startsWith("\001") ||
                channelName.startsWith("FML") || channelName.equals("REGISTER") || channelName.equals("UNREGISTER"));
    }

    void sendPayload(Player player, String channel, Consumer<ByteBuffer> payload) {
        checkNotNull(player, "player");
        checkNotNull(payload, "payload");
        final NetworkSession session = ((LanternPlayer) player).getConnection();
        if (session.getRegisteredChannels().contains(channel)) {
            final ByteBuffer buf = ByteBufferAllocator.unpooled().buffer();
            payload.accept(buf);
            session.send(new ChannelPayloadPacket(channel, buf));
        }
    }

    void sendPayloadToAll(String channel, Consumer<ByteBuffer> payload) {
        checkNotNull(payload, "payload");
        final Iterator<Player> players = this.server.getOnlinePlayers().stream().filter(
                player -> ((LanternPlayer) player).getConnection().getRegisteredChannels().contains(channel)).iterator();
        if (players.hasNext()) {
            final ByteBuffer buf = ByteBufferAllocator.unpooled().buffer();
            payload.accept(buf);
            final Packet msg = new ChannelPayloadPacket(channel, buf);
            players.forEachRemaining(player -> ((LanternPlayer) player).getConnection().send(msg));
        }
    }

    public void handlePayload(ByteBuffer buf, String channel, RemoteConnection connection) {
        final LanternChannelBinding binding = this.bindings.get(channel);
        if (binding != null) {
            binding.handlePayload(buf, connection);
        }
    }
}
