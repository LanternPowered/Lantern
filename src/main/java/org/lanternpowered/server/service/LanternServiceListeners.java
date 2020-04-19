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
package org.lanternpowered.server.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;

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
