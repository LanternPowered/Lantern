/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
