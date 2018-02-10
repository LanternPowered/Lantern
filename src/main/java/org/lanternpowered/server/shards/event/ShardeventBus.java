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
package org.lanternpowered.server.shards.event;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Represents the bus to handle {@link Shardevent}s.
 */
public interface ShardeventBus {

    /**
     * Posts the {@link Shardevent}.
     *
     * @param event The event
     */
    void post(Shardevent event);

    /**
     * Posts the {@link Shardevent} provided by the given {@link Supplier} if
     * there are handlers registered for the given {@link Shardevent} type.
     *
     * @param eventType The event type
     * @param supplier The event supplier
     * @param <T> The event type
     */
    <T extends Shardevent> void post(Class<T> eventType, Supplier<T> supplier);

    /**
     * Registers all the methods of the object annotated with
     * {@link ShardeventListener} as event handlers.
     *
     * @param object The object
     */
    void register(Object object);

    /**
     * Registers the {@link Consumer} as a handler for
     * the given {@link Shardevent} type.
     *
     * @param eventType The event type
     * @param handler The handler
     * @param <T> The event type
     */
    <T extends Shardevent> void register(Class<T> eventType, Consumer<? super T> handler);

    /**
     * Unregisters all the handlers methods that
     * got registered by using the object.
     *
     * @param object The object
     */
    void unregister(Object object);

    /**
     * Unregisters the given handler from the
     * given {@link Shardevent} type.
     *
     * @param eventType The event type
     * @param handler The handler
     * @param <T> The event type
     */
    <T extends Shardevent> void unregister(Class<T> eventType, Consumer<? super T> handler);
}
