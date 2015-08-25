package org.lanternpowered.server.event;

import java.util.EnumMap;
import java.util.List;

import org.spongepowered.api.event.Order;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import static com.google.common.base.Preconditions.checkNotNull;

public class RegisteredHandlerCache {

    private final List<RegisteredHandler<?>> handlers;
    private final EnumMap<Order, List<RegisteredHandler<?>>> handlersByOrder;

    RegisteredHandlerCache(List<RegisteredHandler<?>> handlers) {
        this.handlers = handlers;

        this.handlersByOrder = Maps.newEnumMap(Order.class);
        for (Order order : Order.values()) {
            this.handlersByOrder.put(order, Lists.<RegisteredHandler<?>>newArrayList());
        }
        for (RegisteredHandler<?> handler : handlers) {
            this.handlersByOrder.get(handler.getOrder()).add(handler);
        }
    }

    public List<RegisteredHandler<?>> getHandlers() {
        return this.handlers;
    }

    /**
     * TODO: Do we need this method?
     */
    public List<RegisteredHandler<?>> getHandlersByOrder(Order order) {
        return this.handlersByOrder.get(checkNotNull(order, "order"));
    }

}
