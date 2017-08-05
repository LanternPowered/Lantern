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
package org.lanternpowered.server.inventory.behavior;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.inventory.behavior.event.ContainerEvent;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.spongepowered.api.util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractContainerInteractionBehavior implements ContainerInteractionBehavior {

    private final List<Tuple<Class, Consumer>> eventHandlers = new ArrayList<>();

    /**
     * Registers a event handler ({@link Consumer} for the
     * specified {@link ContainerEvent} type.
     *
     * @param eventType The container event type
     * @param handler The event handler
     * @param <T> The container event type
     */
    public <T extends ContainerEvent> void registerEventHandler(Class<T> eventType, Consumer<T> handler) {
        checkNotNull(eventType, "eventType");
        checkNotNull(handler, "handler");
        this.eventHandlers.add(new Tuple<>(eventType, handler));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleEvent(ClientContainer clientContainer, ContainerEvent event) {
        this.eventHandlers.stream().filter(entry -> entry.getFirst().isInstance(event)).forEach(entry -> entry.getSecond().accept(event));
    }
}
