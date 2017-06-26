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
package co.aikar.timings;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.plugin.PluginContainer;

import java.lang.reflect.Method;

// TODO Revise this
public class TimedEventListener<T extends Event> implements EventListener<T> {

    private final EventListener<T> listener;
    private final Timing timings;

    /**
     * Wraps an event executor and associates a timing handler to it.
     *
     * @param listener The event listener to wrap
     * @param plugin The plugin the listener belongs to
     * @param method The actual mod of the listener
     * @param eventClass The class of the event
     */
    public TimedEventListener(EventListener<T> listener, PluginContainer plugin, Method method, Class<? extends Event> eventClass) {
        this.listener = listener;

        if (method == null) {
            method = listener.getClass().getEnclosingMethod();
        }

        this.timings = LanternTimingsFactory.ofSafe(plugin.getId(),
                "Event: " + method.getDeclaringClass().getName() + " (" + eventClass.getSimpleName() + ")", null);
    }

    @Override
    public void handle(T event) throws Exception {
        if (!Timings.isTimingsEnabled() || !Sponge.getServer().isMainThread()) {
            this.listener.handle(event);
            return;
        }
        this.timings.startTiming();
        this.listener.handle(event);
        this.timings.stopTiming();
    }
}
