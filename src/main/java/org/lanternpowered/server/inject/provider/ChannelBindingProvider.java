/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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
