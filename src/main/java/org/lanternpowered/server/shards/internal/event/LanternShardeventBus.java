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
package org.lanternpowered.server.shards.internal.event;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.api.shard.event.Shardevent;
import org.lanternpowered.api.shard.event.ShardeventBus;
import org.lanternpowered.api.shard.event.ShardeventListener;
import org.lanternpowered.server.util.LambdaFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class LanternShardeventBus implements ShardeventBus {

    /**
     * A handler that isn't targeting a specific handler object. This means
     * that this handler can be reused for multiple objects. This is only
     * used for method event handlers.
     */
    final static class UntargetedMethodHandler {

        final Class<?> eventClass;
        final BiConsumer<Object, Shardevent> handler;

        UntargetedMethodHandler(Class<?> eventClass,
                BiConsumer<Object, Shardevent> handler) {
            this.eventClass = eventClass;
            this.handler = handler;
        }
    }

    /**
     * Cache all the method event handlers, things will get registered/unregistered
     * quite a lot so avoid regenerating classes/reflection lookups.
     */
    private static final Map<Class<?>, List<UntargetedMethodHandler>> untargetedMethodHandlersByObjectClass = new ConcurrentHashMap<>();

    /**
     * A map with all the generated {@link UntargetedMethodHandler} for a specific method.
     */
    private static final Map<Method, UntargetedMethodHandler> untargetedMethodHandlerByMethod = new ConcurrentHashMap<>();

    private static List<UntargetedMethodHandler> loadUntargetedMethodHandlers(Class<?> objectClass) {
        final List<UntargetedMethodHandler> handlers = new ArrayList<>();
        loadUntargetedMethodHandlers(handlers, objectClass);
        return handlers;
    }

    private static void loadUntargetedMethodHandlers(
            List<UntargetedMethodHandler> handlers, Class<?> objectClass) {
        for (Method method : objectClass.getDeclaredMethods()) {
            // Only add entries for methods that are declared, to avoid
            // duplicate entries when methods are overridden.
            if (method.getDeclaredAnnotation(ShardeventListener.class) == null) {
                continue;
            }
            checkState(!Modifier.isStatic(method.getModifiers()),
                    "ShardeventListener methods cannot be static");
            checkState(method.getReturnType().equals(void.class),
                    "ShardeventListener methods cannot have a return type");
            checkState(method.getParameterCount() == 1 && Shardevent.class.isAssignableFrom(method.getParameterTypes()[0]),
                    "ShardeventListener methods can only have one parameter and must extend Shardevent");
            // Generate a Shardevent handler for the method
            final UntargetedMethodHandler methodHandler = untargetedMethodHandlerByMethod
                    .computeIfAbsent(method, method1 -> new UntargetedMethodHandler(
                            method1.getParameterTypes()[0], LambdaFactory.createBiConsumer(method1)));
            handlers.add(methodHandler);
        }
        for (Class<?> interf : objectClass.getInterfaces()) {
            loadUntargetedMethodHandlers(handlers, interf);
        }
        objectClass = objectClass.getSuperclass();
        if (objectClass != null && objectClass != Object.class) {
            loadUntargetedMethodHandlers(handlers, objectClass);
        }
    }

    /**
     * Gets all the {@link UntargetedMethodHandler}s that are
     * present on the given object class.
     *
     * @param objectClass The object class
     * @return The untargeted method handlers
     */
    private static List<UntargetedMethodHandler> getUntargetedMethodHandlers(Class<?> objectClass) {
        return untargetedMethodHandlersByObjectClass.computeIfAbsent(objectClass, LanternShardeventBus::loadUntargetedMethodHandlers);
    }

    private final Multimap<Class<?>, ShardeventHandler> handlersByClass = HashMultimap.create();
    private final LoadingCache<Class<?>, List<ShardeventHandler>> handlerCache = Caffeine.newBuilder().build(this::loadHandlers);

    private List<ShardeventHandler> loadHandlers(Class<?> eventClass) {
        final List<ShardeventHandler> handlers = new ArrayList<>();
        final Set<Class<?>> types = (Set) TypeToken.of(eventClass).getTypes().rawTypes();
        synchronized (this.handlersByClass) {
            for (Class<?> type : types) {
                if (Shardevent.class.isAssignableFrom(type)) {
                    handlers.addAll(this.handlersByClass.get(type));
                }
            }
        }
        return handlers;
    }

    @Override
    public void post(Shardevent event) {
        post(event, this.handlerCache.get(event.getClass()));
    }

    @Override
    public <T extends Shardevent> void post(Class<T> eventType, Supplier<T> supplier) {
        checkNotNull(eventType, "eventType");
        checkNotNull(supplier, "supplier");
        final List<ShardeventHandler> handlers = this.handlerCache.get(eventType);
        if (!handlers.isEmpty()) {
            post(supplier.get(), handlers);
        }
    }

    private void post(Shardevent event, List<ShardeventHandler> handlers) {
        checkNotNull(event, "event");
        for (ShardeventHandler handler : handlers) {
            try {
                handler.handle(event);
            } catch (Exception e) {
                Lantern.getLogger().error("Failed to handle Shardevent", e);
            }
        }
    }

    @Override
    public void register(Object object) {
        checkNotNull(object, "object");
        final List<UntargetedMethodHandler> untargetedHandlers = getUntargetedMethodHandlers(object.getClass());
        synchronized (this.handlersByClass) {
            for (UntargetedMethodHandler handler : untargetedHandlers) {
                this.handlersByClass.put(handler.eventClass, new ShardeventHandler(object) {
                    @Override
                    public void handle(Shardevent event) {
                        handler.handler.accept(this.handle, event);
                    }
                });
            }
        }
        this.handlerCache.invalidateAll();
    }

    @Override
    public <T extends Shardevent> void register(Class<T> eventType, Consumer<? super T> handler) {
        checkNotNull(eventType, "eventType");
        checkNotNull(handler, "handler");
        synchronized (this.handlersByClass) {
            this.handlersByClass.put(eventType, new ShardeventHandler(handler) {
                @Override
                public void handle(Shardevent event) {
                    ((Consumer) this.handle).accept(event);
                }
            });
        }
        this.handlerCache.invalidateAll();
    }

    @Override
    public void unregister(Object object) {
        checkNotNull(object, "object");
        synchronized (this.handlersByClass) {
            this.handlersByClass.values().removeIf(shardeventHandler -> shardeventHandler.handle == object);
        }
        this.handlerCache.invalidateAll();
    }

    @Override
    public <T extends Shardevent> void unregister(Class<T> eventType, Consumer<? super T> handler) {
        checkNotNull(eventType, "eventType");
        checkNotNull(handler, "handler");
        synchronized (this.handlersByClass) {
            this.handlersByClass.get(eventType).removeIf(shardeventHandler -> shardeventHandler.handle == handler);
        }
        this.handlerCache.invalidateAll();
    }
}
