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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkPlugin;

import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.event.EventManager;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Singleton;

@Singleton
public class LanternEventManager implements EventManager {

    private final Object lock = new Object();
    private final AnnotatedEventHandlerFactory handlerFactory = new ClassEventHandlerFactory("org.lanternpowered.server.event.handler");
    private final Multimap<Class<?>, RegisteredHandler<?>> handlersByEvent = HashMultimap.create();

    /**
     * A cache of all the handlers for an event type for quick event posting.
     */
    private final LoadingCache<Class<? extends Event>, RegisteredHandlerCache> handlersCache =
            CacheBuilder.newBuilder().build(new CacheLoader<Class<? extends Event>, RegisteredHandlerCache>() {
                @Override
                public RegisteredHandlerCache load(Class<? extends Event> eventClass) throws Exception {
                    return bakeHandlers(eventClass);
                }
            });

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private RegisteredHandlerCache bakeHandlers(Class<?> rootEvent) {
        List<RegisteredHandler<?>> handlers = Lists.newArrayList();
        Set<Class<?>> types = (Set) TypeToken.of(rootEvent).getTypes().rawTypes();

        synchronized (this.lock) {
            for (Class<?> type : types) {
                if (Event.class.isAssignableFrom(type)) {
                    handlers.addAll(this.handlersByEvent.get(type));
                }
            }
        }

        Collections.sort(handlers);
        return new RegisteredHandlerCache(handlers);
    }

    private static boolean isValidHandler(Method method) {
        int modifiers = method.getModifiers();
        if (Modifier.isStatic(modifiers)
                || !Modifier.isPublic(modifiers)
                || Modifier.isAbstract(modifiers)
                || method.getDeclaringClass().isInterface()
                || method.getReturnType() != void.class) {
            return false;
        }

        Class<?>[] parameters = method.getParameterTypes();
        return parameters.length == 1 && Event.class.isAssignableFrom(parameters[0]);
    }

    private void register(RegisteredHandler<?> handler) {
        register(Collections.<RegisteredHandler<?>>singletonList(handler));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void register(List<RegisteredHandler<?>> handlers) {
        synchronized (this.lock) {
            Set<Class<?>> types = Sets.newHashSet();

            for (RegisteredHandler handler : handlers) {
                if (this.handlersByEvent.put(handler.getEventClass(), handler)) {
                    types.addAll(TypeToken.of(handler.getEventClass()).getTypes().rawTypes());
                }
            }

            if (!types.isEmpty()) {
                this.handlersCache.invalidateAll(types);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void register(PluginContainer plugin, Object listener) {
        checkNotNull(plugin, "plugin");
        checkNotNull(listener, "listener");

        List<RegisteredHandler<?>> handlers = Lists.newArrayList();

        Class<?> handle = listener.getClass();
        for (Method method : handle.getMethods()) {
            Listener subscribe = method.getAnnotation(Listener.class);
            if (subscribe != null) {
                if (isValidHandler(method)) {
                    Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];
                    AnnotatedEventHandler handler;

                    try {
                        handler = this.handlerFactory.create(listener, method);
                    } catch (Exception e) {
                        LanternGame.log().error("Failed to create handler for {} on {}", method, handle, e);
                        continue;
                    }

                    handlers.add(createRegistration(plugin, eventClass, subscribe, handler));
                } else {
                    LanternGame.log().warn("The method {} on {} has @{} but has the wrong signature", method, handle.getName(),
                            Listener.class.getName());
                }
            }
        }

        this.register(handlers);
    }

    private static <T extends Event> RegisteredHandler<T> createRegistration(PluginContainer plugin, Class<T> eventClass,
            Listener subscribe, EventListener<? super T> handler) {
        return createRegistration(plugin, eventClass, subscribe.order(), subscribe.ignoreCancelled(), handler);
    }

    private static <T extends Event> RegisteredHandler<T> createRegistration(PluginContainer plugin, Class<T> eventClass,
            Order order, boolean ignoreCancelled, EventListener<? super T> handler) {
        return new RegisteredHandler<T>(plugin, eventClass, order, handler, ignoreCancelled);
    }

    @Override
    public void registerListeners(Object plugin, Object listener) {
        this.register(checkPlugin(plugin, "plugin"), checkNotNull(listener, "listener"));
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
        checkPlugin(plugin, "plugin");
        checkNotNull(eventClass, "eventClass");
        checkNotNull(order, "order");
        checkNotNull(listener, "listener");
        this.register(createRegistration(checkPlugin(plugin, "plugin"), eventClass, order, false, listener));
    }

    private void unregister(Predicate<RegisteredHandler<?>> unregister) {
        synchronized (this.lock) {
            Set<Class<?>> types = Sets.newHashSet();
            Iterator<RegisteredHandler<?>> it = this.handlersByEvent.values().iterator();

            while (it.hasNext()) {
                RegisteredHandler<?> handler = it.next();
                if (unregister.apply(handler)) {
                    types.addAll(TypeToken.of(handler.getEventClass()).getTypes().rawTypes());
                    it.remove();
                }
            }

            if (!types.isEmpty()) {
                this.handlersCache.invalidateAll(types);
            }
        }
    }

    @Override
    public void unregisterListeners(final Object listener) {
        checkNotNull(listener, "listener");
        this.unregister(new Predicate<RegisteredHandler<?>>() {

            @Override
            public boolean apply(RegisteredHandler<?> handler) {
                return listener.equals(handler.getHandle());
            }

        });
    }

    @Override
    public void unregisterPluginListeners(Object pluginObj) {
        final PluginContainer plugin = checkPlugin(pluginObj, "plugin");
        this.unregister(new Predicate<RegisteredHandler<?>>() {

            @Override
            public boolean apply(RegisteredHandler<?> handler) {
                return plugin.equals(handler.getPlugin());
            }

        });
    }

    protected RegisteredHandlerCache getHandlerCache(Event event) {
        return this.handlersCache.getUnchecked(checkNotNull(event, "event").getClass());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected boolean post(Event event, List<RegisteredHandler<?>> handlers) {
        for (RegisteredHandler handler : handlers) {
            try {
                handler.handle(event);
            } catch (Throwable e) {
                LanternGame.log().error("Could not pass {} to {}", event.getClass().getSimpleName(), handler.getPlugin(), e);
            }
        }
        return event instanceof Cancellable && ((Cancellable) event).isCancelled();
    }

    @Override
    public boolean post(Event event) {
        return this.post(event, this.getHandlerCache(event).getHandlers());
    }

    /**
     * TODO: Do we need this method?
     */
    public boolean post(Event event, Order order) {
        return this.post(event, this.getHandlerCache(event).getHandlersByOrder(order));
    }

}