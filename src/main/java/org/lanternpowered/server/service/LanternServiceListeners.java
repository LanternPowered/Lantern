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
package org.lanternpowered.server.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class LanternServiceListeners {

    private static final LanternServiceListeners instance = new LanternServiceListeners();

    public static LanternServiceListeners getInstance() {
        return instance;
    }

    private final Multimap<Class<?>, Predicate<Object>> serviceCallbacks = HashMultimap.create();

    @SuppressWarnings("unchecked")
    public <T> void registerExpirableServiceCallback(Class<T> serviceType, Predicate<T> callback) {
        final Optional<T> service = Sponge.getServiceManager().provide(serviceType);
        if (service.isPresent() && !callback.test(service.get())) {
            return;
        }
        synchronized (this.serviceCallbacks) {
            this.serviceCallbacks.put(serviceType, (Predicate<Object>) callback);
        }
    }

    public <T> void registerServiceCallback(Class<T> service, Consumer<T> callback) {
        this.registerExpirableServiceCallback(service, o -> {
            callback.accept(o);
            return true;
        });
    }

    @Listener
    public void onServiceChange(ChangeServiceProviderEvent event) {
        synchronized (this.serviceCallbacks) {
            this.serviceCallbacks.get(event.getService()).removeIf(objectPredicate -> !objectPredicate.test(event.getNewProvider()));
        }
    }

    private LanternServiceListeners() {
    }
}
