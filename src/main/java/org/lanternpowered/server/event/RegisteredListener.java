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
package org.lanternpowered.server.event;

import com.google.common.base.MoreObjects;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.plugin.PluginContainer;

public final class RegisteredListener<T extends Event> implements LanternEventListener<T>, Comparable<RegisteredListener<?>> {

    private final PluginContainer plugin;
    private final EventListener<? super T> handler;

    private final EventType<T> eventType;
    private final Order order;

    RegisteredListener(PluginContainer plugin, EventType<T> eventType, Order order, EventListener<? super T> handler) {
        this.plugin = plugin;
        this.eventType = eventType;
        this.order = order;
        this.handler = handler;
    }

    public PluginContainer getPlugin() {
        return this.plugin;
    }

    public EventType<T> getEventType() {
        return this.eventType;
    }

    public Order getOrder() {
        return this.order;
    }

    public EventListener<? super T> getHandler() {
        return this.handler;
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
        this.handler.handle(event);
    }

    @Override
    public int compareTo(RegisteredListener<?> handler) {
        return this.order.compareTo(handler.order);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("plugin", this.plugin.getId())
                .add("eventType", this.eventType)
                .add("order", this.order.toString())
                .toString();
    }
}
