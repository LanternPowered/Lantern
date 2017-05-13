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
package org.lanternpowered.server.event;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkPlugin;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.lanternpowered.server.event.filter.FilterFactory;
import org.lanternpowered.server.util.DefineableClassLoader;
import org.slf4j.Logger;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.plugin.PluginContainer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import javax.annotation.Nullable;

@Singleton
public class LanternEventManager implements EventManager {

    private final Logger logger;

    private final Object lock = new Object();
    private final DefineableClassLoader classLoader = new DefineableClassLoader();
    private final AnnotatedEventListener.Factory listenerFactory = new ClassEventListenerFactory("org.lanternpowered.server.event.listener",
                    new FilterFactory("org.lanternpowered.server.event.filters", this.classLoader), this.classLoader);
    private final Multimap<Class<?>, RegisteredListener<?>> listenersByEvent = HashMultimap.create();
    private final Set<Object> registeredListeners = new HashSet<>();

    /**
     * A cache of all the handlers for an event type for quick event posting.
     */
    private final LoadingCache<Class<? extends Event>, List<RegisteredListener<?>>> listenersCache =
            Caffeine.newBuilder().initialCapacity(150).build(this::bakeHandlers);

    @Inject
    public LanternEventManager(Logger logger) {
        this.logger = logger;

        // Caffeine offers no control over the concurrency level of the
        // ConcurrentHashMap which backs the cache. By default this concurrency
        // level is 16. We replace the backing map before any use can occur
        // a new ConcurrentHashMap with a concurrency level of 1
        try {
            // Cache impl class is UnboundedLocalLoadingCache which extends
            // UnboundedLocalManualCache

            // UnboundedLocalManualCache has a field 'cache' with an
            // UnboundedLocalCache which contains the actual backing map
            final Field innerCache = this.listenersCache.getClass().getSuperclass().getDeclaredField("cache");
            innerCache.setAccessible(true);
            final Object innerCacheValue = innerCache.get(this.listenersCache);
            final Class<?> innerCacheClass = innerCacheValue.getClass(); // UnboundedLocalCache
            final Field cacheData = innerCacheClass.getDeclaredField("data");
            cacheData.setAccessible(true);
            final ConcurrentHashMap<Class<? extends Event>, List<RegisteredListener<?>>> newBackingData = new ConcurrentHashMap<>(150, 0.75f, 1);
            cacheData.set(innerCacheValue, newBackingData);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            this.logger.warn("Failed to set event cache backing array, type was " + this.listenersCache.getClass().getName());
            this.logger.warn("  Caused by: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<RegisteredListener<?>> bakeHandlers(Class<?> rootEvent) {
        final List<RegisteredListener<?>> handlers = new ArrayList<>();
        final Set<Class<?>> types = (Set) TypeToken.of(rootEvent).getTypes().rawTypes();

        synchronized (this.lock) {
            types.stream().filter(Event.class::isAssignableFrom).forEach(type -> handlers.addAll(this.listenersByEvent.get(type)));
        }

        Collections.sort(handlers);
        return handlers;
    }

    @Nullable
    private static String getHandlerErrorOrNull(Method method) {
        final int modifiers = method.getModifiers();
        final List<String> errors = new ArrayList<>();
        if (Modifier.isStatic(modifiers)) {
            errors.add("method must not be static");
        }
        if (!Modifier.isPublic(modifiers)) {
            errors.add("method must be public");
        }
        if (Modifier.isAbstract(modifiers)) {
            errors.add("method must not be abstract");
        }
        if (method.getDeclaringClass().isInterface()) {
            errors.add("interfaces cannot declare listeners");
        }
        if (method.getReturnType() != void.class) {
            errors.add("method must return void");
        }
        final Class<?>[] parameters = method.getParameterTypes();
        if (parameters.length == 0 || !Event.class.isAssignableFrom(parameters[0])) {
            errors.add("method must have an Event as its first parameter");
        }
        if (errors.isEmpty()) {
            return null;
        }
        return String.join(", ", errors);
    }

    private void register(RegisteredListener<?> listener) {
        register(Collections.singletonList(listener));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void register(List<RegisteredListener<?>> listeners) {
        synchronized (this.lock) {
            final Set<Class<?>> types = new HashSet<>();
            listeners.stream()
                    .filter(listener -> this.listenersByEvent.put(listener.getEventClass(), listener))
                    .forEach(listener -> types.addAll(TypeToken.of(listener.getEventClass()).getTypes().rawTypes()));
            if (!types.isEmpty()) {
                this.listenersCache.invalidateAll(types);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void register(PluginContainer plugin, Object listener) {
        checkNotNull(plugin, "plugin");
        checkNotNull(listener, "listener");

        synchronized (this.registeredListeners) {
            if (this.registeredListeners.contains(listener)) {
                this.logger.warn("Plugin {} attempted to register an already registered listener ({})",
                        plugin.getId(), listener.getClass().getName());
                Thread.dumpStack();
            } else {
                this.registeredListeners.add(listener);
            }
        }

        final List<RegisteredListener<?>> handlers = new ArrayList<>();
        final Map<Method, String> methodErrors = new HashMap<>();

        final Class<?> handle = listener.getClass();
        for (Method method : handle.getMethods()) {
            final Listener subscribe = method.getAnnotation(Listener.class);
            if (subscribe != null) {
                final String error = getHandlerErrorOrNull(method);
                if (error == null) {
                    final Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];
                    final AnnotatedEventListener handler;

                    try {
                        handler = this.listenerFactory.create(listener, method);
                    } catch (Exception e) {
                        this.logger.error("Failed to create listener for {} on {}", method, handle, e);
                        continue;
                    }

                    handlers.add(createRegistration(plugin, eventClass, subscribe, handler));
                } else {
                    methodErrors.put(method, error);
                }
            }
        }

        // getMethods() doesn't return private methods. Do another check to warn
        // about those.
        for (Class<?> handleParent = handle; handleParent != Object.class; handleParent = handleParent.getSuperclass()) {
            for (Method method : handleParent.getDeclaredMethods()) {
                if (method.getAnnotation(Listener.class) != null && !methodErrors.containsKey(method)) {
                    final String error = getHandlerErrorOrNull(method);
                    if (error != null) {
                        methodErrors.put(method, error);
                    }
                }
            }
        }

        for (Map.Entry<Method, String> method : methodErrors.entrySet()) {
            this.logger.warn("Invalid listener method {} in {}: {}", method.getKey(),
                    method.getKey().getDeclaringClass().getName(), method.getValue());
        }

        register(handlers);
    }

    private static <T extends Event> RegisteredListener<T> createRegistration(PluginContainer plugin, Class<T> eventClass,
            Listener subscribe, EventListener<? super T> listener) {
        return createRegistration(plugin, eventClass, subscribe.order(), listener);
    }

    private static <T extends Event> RegisteredListener<T> createRegistration(PluginContainer plugin, Class<T> eventClass,
            Order order, EventListener<? super T> listener) {
        return new RegisteredListener<>(plugin, eventClass, order, listener);
    }

    @Override
    public void registerListeners(Object plugin, Object listener) {
        register(checkPlugin(plugin, "plugin"), checkNotNull(listener, "listener"));
    }

    @Override
    public <T extends Event> void registerListener(Object plugin, Class<T> eventClass, Order order, boolean beforeModifications,
            EventListener<? super T> listener) {
        // Ignore the "beforeModifications" property, this is only used in combination with mods
        registerListener(plugin, eventClass, order, listener);
    }

    @Override
    public <T extends Event> void registerListener(Object plugin, Class<T> eventClass, EventListener<? super T> listener) {
        registerListener(plugin, eventClass, Order.DEFAULT, listener);
    }

    @Override
    public <T extends Event> void registerListener(Object plugin, Class<T> eventClass, Order order, EventListener<? super T> listener) {
        final PluginContainer container = checkPlugin(plugin, "plugin");
        checkNotNull(eventClass, "eventClass");
        checkNotNull(order, "order");
        checkNotNull(listener, "listener");
        register(createRegistration(container, eventClass, order, listener));
    }

    private void unregister(Predicate<RegisteredListener<?>> unregister) {
        synchronized (this.lock) {
            final Set<Class<?>> types = new HashSet<>();
            final Iterator<RegisteredListener<?>> it = this.listenersByEvent.values().iterator();

            while (it.hasNext()) {
                final RegisteredListener<?> listener = it.next();
                if (unregister.test(listener)) {
                    if (listener.getHandle() instanceof AnnotatedEventListener) {
                        synchronized (this.registeredListeners) {
                            this.registeredListeners.remove(((AnnotatedEventListener) listener.getHandle()).getHandle());
                        }
                    }
                    types.addAll(TypeToken.of(listener.getEventClass()).getTypes().rawTypes());
                    it.remove();
                }
            }

            if (!types.isEmpty()) {
                this.listenersCache.invalidateAll(types);
            }
        }
    }

    @Override
    public void unregisterListeners(Object listener) {
        checkNotNull(listener, "listener");
        unregister(handler -> listener.equals(handler.getHandle()));
    }

    @Override
    public void unregisterPluginListeners(Object pluginObj) {
        final PluginContainer plugin = checkPlugin(pluginObj, "plugin");
        unregister(handler -> plugin.equals(handler.getPlugin()));
    }

    @Override
    public boolean post(Event event) {
        checkNotNull(event, "event");
        for (RegisteredListener listener : this.listenersCache.get(event.getClass())) {
            try {
                if (event instanceof AbstractEvent) {
                    ((AbstractEvent) event).currentOrder = listener.getOrder();
                }
                //noinspection unchecked
                listener.handle(event);
            } catch (Throwable e) {
                this.logger.error("Could not pass {} to {}", event.getClass().getSimpleName(),
                        listener.getPlugin(), e);
            }
        }
        if (event instanceof AbstractEvent) {
            //noinspection ConstantConditions
            ((AbstractEvent) event).currentOrder = null;
        }
        return event instanceof Cancellable && ((Cancellable) event).isCancelled();
    }
}
