package org.lanternpowered.server.network.channel;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.Conditions.checkPlugin;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

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

public final class LanternChannelRegistrar implements ChannelRegistrar {

    private final Map<String, RegisteredChannel> channels = Maps.newConcurrentMap();
    private final Server server;

    public static final class RegisteredChannel {

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
        checkNotNullOrEmpty(channel, "channel");
        checkArgument(channel.length() <= 20, "channel length may not be longer then 20");
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
        return this.channels.get(checkNotNull(channel, "channel"));
    }

    /**
     * Validates whether there is a specified channel registered
     * with the specified plugin. Throws a exception when it is invalid.
     * 
     * @param plugin the plugin
     * @param channel the channel
     * @throws IllegalStateException
     */
    public void validateChannel(Object plugin, String channel) {
        PluginContainer container = checkPlugin(plugin, "plugin");
        checkNotNull(channel, "channel");
        checkState(this.channels.containsKey(channel), "Channel " + channel + " is not registered.");
        RegisteredChannel registration = this.channels.get(channel);
        checkState(registration.plugin == container, "The provided plugin doesn't match the one"
                + " of the registration. (Got " + container.getId() + ", expected " + registration.plugin.getId() + ")");
    }
}
