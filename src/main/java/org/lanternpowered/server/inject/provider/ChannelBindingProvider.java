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
package org.lanternpowered.server.inject.provider;

import static org.lanternpowered.server.inject.provider.ProviderHelper.provideName;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.lanternpowered.api.inject.InjectionPoint;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.ChannelId;
import org.spongepowered.api.network.ChannelRegistrar;
import org.spongepowered.api.plugin.PluginContainer;

public abstract class ChannelBindingProvider<B extends ChannelBinding> implements Provider<B> {

    @Inject protected ChannelRegistrar registrar;
    @Inject protected PluginContainer container;
    @Inject protected Provider<InjectionPoint> point;

    protected String getChannel() {
        final InjectionPoint injectionPoint = this.point.get();
        final ChannelId channelId = injectionPoint.getAnnotation(ChannelId.class);
        if (channelId != null) {
            return channelId.value();
        }
        return provideName(injectionPoint).orElse(this.container.getId());
    }

    public static class Indexed extends ChannelBindingProvider<ChannelBinding.IndexedMessageChannel> {

        @Override
        public ChannelBinding.IndexedMessageChannel get() {
            return this.registrar.getOrCreate(this.container, getChannel());
        }
    }

    public static class Raw extends ChannelBindingProvider<ChannelBinding.RawDataChannel> {

        @Override
        public ChannelBinding.RawDataChannel get() {
            return this.registrar.getOrCreateRaw(this.container, getChannel());
        }
    }

}
