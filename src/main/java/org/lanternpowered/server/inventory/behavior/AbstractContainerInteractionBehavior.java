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
