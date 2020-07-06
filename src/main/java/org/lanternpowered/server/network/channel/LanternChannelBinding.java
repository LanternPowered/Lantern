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

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.plugin.PluginContainer;

abstract class LanternChannelBinding implements ChannelBinding {

    private final LanternChannelRegistrar registrar;
    private final PluginContainer owner;
    private final String name;

    boolean bound;

    LanternChannelBinding(LanternChannelRegistrar registrar, String name, PluginContainer owner) {
        this.registrar = registrar;
        this.owner = owner;
        this.name = name;
    }

    @Override
    public LanternChannelRegistrar getRegistrar() {
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

    abstract void handlePayload(ByteBuffer buf, RemoteConnection connection);

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("plugin", this.owner)
                .add("name", this.name)
                .toString();
    }
}
