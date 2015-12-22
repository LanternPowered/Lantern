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
package org.lanternpowered.server.event;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.plugin.PluginContainer;

public class RegisteredHandler<T extends Event> implements LanternEventListener<T>, Comparable<RegisteredHandler<?>> {
    private final PluginContainer plugin;
    private final EventListener<? super T> handler;

    private final Class<T> eventClass;
    private final Order order;

    private final boolean ignoreCancelled;

    RegisteredHandler(PluginContainer plugin, Class<T> eventClass, Order order, EventListener<? super T> handler, boolean ignoreCancelled) {
        this.plugin = checkNotNull(plugin, "plugin");
        this.eventClass = checkNotNull(eventClass, "eventClass");
        this.order = checkNotNull(order, "order");
        this.handler = checkNotNull(handler, "handler");
        this.ignoreCancelled = ignoreCancelled;
    }

    public PluginContainer getPlugin() {
        return this.plugin;
    }

    public Class<T> getEventClass() {
        return this.eventClass;
    }

    public Order getOrder() {
        return this.order;
    }

    @Override
    public Object getHandle() {
        if (this.handler instanceof LanternEventListener) {
            return ((LanternEventListener<?>) this.handler).getHandle();
        }

        return this.handler;
    }

    @Override
    public void handle(T event) throws Exception {
        if (!this.ignoreCancelled && event instanceof Cancellable && ((Cancellable) event).isCancelled()) {
            return;
        }
        this.handler.handle(event);
    }

    @Override
    public int compareTo(RegisteredHandler<?> handler) {
        return this.order.compareTo(handler.order);
    }

}
