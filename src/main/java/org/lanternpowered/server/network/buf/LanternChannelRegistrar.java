package org.lanternpowered.server.network.buf;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkPlugin;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutRegisterChannels;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelListener;
import org.spongepowered.api.network.ChannelRegistrar;
import org.spongepowered.api.network.ChannelRegistrationException;
import org.spongepowered.api.plugin.PluginContainer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class LanternChannelRegistrar implements ChannelRegistrar {

    private final Map<String, RegisteredChannel> channels = Maps.newConcurrentMap();
    private final Server server;

    public static class RegisteredChannel {

        private final ChannelListener listener;
        private final PluginContainer plugin;
        private final String channel;

        private RegisteredChannel(String channel, PluginContainer plugin, ChannelListener listener) {
            this.listener = listener;
            this.channel = channel;
            this.plugin = plugin;
        }

        public String getChannel() {
            return this.channel;
        }

        public ChannelListener getListener() {
            return this.listener;
        }

        public PluginContainer getPlugin() {
            return this.plugin;
        }
    }

    public LanternChannelRegistrar(Server server) {
        this.server = server;
    }

    @Override
    public void registerChannel(Object plugin, ChannelListener listener, String channel) throws ChannelRegistrationException {
        PluginContainer container = checkPlugin(plugin, "plugin");
        checkNotNull(listener, "listener");
        checkNotNull(channel, "channel");
        if (this.channels.containsKey(channel) || channel.startsWith("MC|") || channel.startsWith("\001") || channel.startsWith("FML")) {
            throw new ChannelRegistrationException("Channel with name \"" + channel + "\" is already registered!");
        }
        this.channels.put(channel, new RegisteredChannel(channel, container, listener));
        MessagePlayInOutRegisterChannels message = new MessagePlayInOutRegisterChannels(Sets.newHashSet(channel));
        for (Player player : this.server.getOnlinePlayers()) {
            ((Session) player.getConnection()).send(message);
        }
    }

    @Override
    public List<String> getRegisteredChannels() {
        return ImmutableList.copyOf(this.channels.keySet());
    }

    /**
     * Gets the registered channel instance for the specified channel
     * name, may return null if absent.
     * 
     * @param channel the channel name
     * @return the registered channel
     */
    @Nullable
    public RegisteredChannel getRegisteredChannel(String channel) {
        return this.channels.get(channel);
    }

}
