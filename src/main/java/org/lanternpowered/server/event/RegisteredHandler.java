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
