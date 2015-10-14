package org.lanternpowered.server.network.channel;

import io.netty.buffer.ByteBuf;

import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.ChannelRegistrar;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.plugin.PluginContainer;

public class LanternChannelBinding implements ChannelBinding {

    final LanternChannelRegistrar registrar;
    final PluginContainer owner;
    final String name;

    boolean bound;

    LanternChannelBinding(LanternChannelRegistrar registrar, String name, PluginContainer owner) {
        this.registrar = registrar;
        this.owner = owner;
        this.name = name;
    }

    @Override
    public ChannelRegistrar getRegistrar() {
        return this.registrar;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public PluginContainer getOwner() {
        return this.owner;
    }

    void handlePayload(ByteBuf buf, RemoteConnection connection) {
    }
}
