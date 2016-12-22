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
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import org.lanternpowered.server.event.filter.FilterFactory;
import org.lanternpowered.server.event.gen.DefineableClassLoader;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.plugin.PluginContainer;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class LanternEventManager implements EventManager {

    private final Object lock = new Object();
    private final DefineableClassLoader classLoader = new DefineableClassLoader(this.getClass().getClassLoader());
    private final AnnotatedEventListener.Factory listenerFactory = new ClassEventListenerFactory("org.lanternpowered.server.event.listener",
                    new FilterFactory("org.lanternpowered.server.event.filters", this.classLoader), this.classLoader);
    private final Multimap<Class<?>, RegisteredListener<?>> listenersByEvent = HashMultimap.create();

    /**
     * A cache of all the handlers for an event type for quick event posting.
     */
    private final LoadingCache<Class<? extends Event>, List<RegisteredListener<?>>> listenersCache =
            Caffeine.newBuilder().build(this::bakeHandlers);

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<RegisteredListener<?>> bakeHandlers(Class<?> rootEvent) {
        final List<RegisteredListener<?>> handlers = Lists.newArrayList();
        final Set<Class<?>> types = (Set) TypeToken.of(rootEvent).getTypes().rawTypes();

        synchronized (this.lock) {
            types.stream().filter(Event.class::isAssignableFrom).forEach(type -> handlers.addAll(this.listenersByEvent.get(type)));
        }

        Collections.sort(handlers);
        return handlers;
    }

    private static boolean isValidHandler(Method method) {
        final int modifiers = method.getModifiers();
        if (Modifier.isStatic(modifiers)
                || !Modifier.isPublic(modifiers)
                || Modifier.isAbstract(modifiers)
                || method.getDeclaringClass().isInterface()
                || method.getReturnType() != void.class) {
            return false;
        }
        final Class<?>[] parameters = method.getParameterTypes();
        return parameters.length >= 1 && Event.class.isAssignableFrom(parameters[0]);
    }

    private void register(RegisteredListener<?> listener) {
        this.register(Collections.singletonList(listener));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void register(List<RegisteredListener<?>> listeners) {
        synchronized (this.lock) {
            final Set<Class<?>> types = Sets.newHashSet();

            for (RegisteredListener listener : listeners) {
                if (this.listenersByEvent.put(listener.getEventClass(), listener)) {
                    types.addAll(TypeToken.of(listener.getEventClass()).getTypes().rawTypes());
                }
            }

            if (!types.isEmpty()) {
                this.listenersCache.invalidateAll(types);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void register(PluginContainer plugin, Object listener) {
        checkNotNull(plugin, "plugin");
        checkNotNull(listener, "listener");

        final List<RegisteredListener<?>> handlers = new ArrayList<>();
        final Class<?> handle = listener.getClass();
        for (Method method : handle.getMethods()) {
            final Listener subscribe = method.getAnnotation(Listener.class);
            if (subscribe != null) {
                if (isValidHandler(method)) {
                    final Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];
                    final AnnotatedEventListener handler;

                    try {
                        handler = this.listenerFactory.create(listener, method);
                    } catch (Exception e) {
                        Lantern.getLogger().error("Failed to create listener for {} on {}", method, handle, e);
                        continue;
                    }

                    handlers.add(createRegistration(plugin, eventClass, subscribe, handler));
                } else {
                    Lantern.getLogger().warn("The method {} on {} has @{} but has the wrong signature", method,
                            handle.getName(), Listener.class.getName());
                }
            }
        }

        this.register(handlers);
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
        this.registerListener(plugin, eventClass, order, listener);
    }

    @Override
    public <T extends Event> void registerListener(Object plugin, Class<T> eventClass, EventListener<? super T> listener) {
        this.registerListener(plugin, eventClass, Order.DEFAULT, listener);
    }

    @Override
    public <T extends Event> void registerListener(Object plugin, Class<T> eventClass, Order order, EventListener<? super T> listener) {
        final PluginContainer container = checkPlugin(plugin, "plugin");
        checkNotNull(eventClass, "eventClass");
        checkNotNull(order, "order");
        checkNotNull(listener, "listener");
        this.register(createRegistration(container, eventClass, order, listener));
    }

    private void unregister(Predicate<RegisteredListener<?>> unregister) {
        synchronized (this.lock) {
            final Set<Class<?>> types = new HashSet<>();
            final Iterator<RegisteredListener<?>> it = this.listenersByEvent.values().iterator();

            while (it.hasNext()) {
                final RegisteredListener<?> listener = it.next();
                if (unregister.test(listener)) {
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
    public void unregisterListeners(final Object listener) {
        checkNotNull(listener, "listener");
        this.unregister(handler -> listener.equals(handler.getHandle()));
    }

    @Override
    public void unregisterPluginListeners(Object pluginObj) {
        final PluginContainer plugin = checkPlugin(pluginObj, "plugin");
        this.unregister(handler -> plugin.equals(handler.getPlugin()));
    }

    @Override
    public boolean post(Event event) {
        checkNotNull(event, "event");
        for (RegisteredListener listener : this.listenersCache.get(event.getClass())) {
            try {
                //noinspection unchecked
                listener.handle(event);
            } catch (Throwable e) {
                Lantern.getLogger().error("Could not pass {} to {}", event.getClass().getSimpleName(),
                        listener.getPlugin(), e);
            }
        }
        return event instanceof Cancellable && ((Cancellable) event).isCancelled();
    }
}
