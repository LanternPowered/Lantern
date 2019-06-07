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
import static org.lanternpowered.server.util.UncheckedThrowables.doUnchecked;
import static org.lanternpowered.server.util.UncheckedThrowables.throwUnchecked;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.lmbda.LambdaFactory;
import org.lanternpowered.lmbda.MethodHandlesExtensions;
import org.lanternpowered.server.cause.LanternCauseStackManager;
import org.lanternpowered.server.data.key.KeyEventListener;
import org.lanternpowered.server.event.filter.FilterFactory;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.util.DefineableClassLoader;
import org.lanternpowered.server.util.SyncLanternThread;
import org.lanternpowered.server.util.SystemProperties;
import org.lanternpowered.server.util.TypeTokenHelper;
import org.lanternpowered.server.util.function.ThrowableConsumer;
import org.slf4j.Logger;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.GenericEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.event.impl.AbstractValueChangeEvent;
import org.spongepowered.api.plugin.PluginContainer;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings({"unchecked", "ConstantConditions"})
@Singleton
public class LanternEventManager implements EventManager {

    private static final boolean SHOULD_FIRE_ALL_TRUE =
            SystemProperties.get().getBooleanProperty("sponge.shouldFireAll");

    private static final TypeVariable<?> GENERIC_EVENT_TYPE = GenericEvent.class.getTypeParameters()[0];

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
    private final LoadingCache<EventType<?>, List<RegisteredListener<?>>> listenersCache =
            Caffeine.newBuilder().initialCapacity(150).build(this::bakeHandlers);

    private final Map<Class<?>, ShouldFireField> shouldFireFields = new HashMap<>();

    /**
     * A lock to synchronize events called from a {@link SyncLanternThread}.
     */
    private final Object syncPostLock = new Object();

    private static final class ShouldFireField {

        private final static MethodHandles.Lookup lookup = doUnchecked(() ->
                MethodHandlesExtensions.privateLookupIn(ShouldFire.class, MethodHandles.lookup()));

        private final Class<? extends Event> eventClass;

        /**
         * The setter of the field.
         */
        private final Consumer<Boolean> setter;

        /**
         * The getter of the field.
         */
        private final Supplier<Boolean> getter;

        private ShouldFireField(Class<? extends Event> eventClass, Field field) {
            try {
                final MethodHandle setter = lookup.unreflectSetter(field);
                final MethodHandle getter = lookup.unreflectGetter(field);
                this.setter = LambdaFactory.createConsumer(setter);
                this.getter = LambdaFactory.createSupplier(getter);
            } catch (IllegalAccessException e) {
                throw throwUnchecked(e);
            }
            this.eventClass = eventClass;
        }

        public void setState(boolean state) {
            if (this.getter.get() == state) {
                return;
            }
            Lantern.getLogger().debug("Updating ShouldFire field for class {} with value {}",
                    this.eventClass.getName(), state);
            this.setter.accept(state);
        }
    }

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

        // Collect the should fire fields
        for (Field field : ShouldFire.class.getFields()) {
            final ShouldFireTarget target = field.getAnnotation(ShouldFireTarget.class);
            if (target == null) {
                continue;
            }
            this.shouldFireFields.put(target.value(), new ShouldFireField(target.value(), field));
        }

        updateShouldFireFields();
    }

    /**
     * Updates all the {@link ShouldFire} fields.
     */
    private void updateShouldFireFields() {
        final Set<Class<?>> registeredTypes;
        synchronized (this.lock) {
            registeredTypes = new HashSet<>(this.listenersByEvent.keySet());
        }
        for (Map.Entry<Class<?>, ShouldFireField> entry : this.shouldFireFields.entrySet()) {
            final Class<?> eventClass = entry.getKey();
            boolean shouldFire = false;
            if (SHOULD_FIRE_ALL_TRUE) {
                shouldFire = true;
            } else {
                for (Class<?> registeredType : registeredTypes) {
                    if (registeredType.isAssignableFrom(eventClass) || // Sub class
                            eventClass.isAssignableFrom(registeredType)) {  // Or super class
                        shouldFire = true; // We got a match
                        break;
                    }
                }
            }
            entry.getValue().setState(shouldFire);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <T extends Event> List<RegisteredListener<?>> bakeHandlers(EventType<T> eventType) {
        final List<RegisteredListener<?>> handlers = new ArrayList<>();
        final Set<Class<? super T>> types = TypeToken.of(eventType.getType()).getTypes().rawTypes();

        synchronized (this.lock) {
            for (Class<? super T> type : types) {
                if (Event.class.isAssignableFrom(type)) {
                    final Collection<RegisteredListener<?>> listeners = this.listenersByEvent.get(type);
                    if (GenericEvent.class.isAssignableFrom(type)) {
                        final TypeToken<?> genericType = eventType.getGenericType();
                        checkNotNull(genericType);
                        for (RegisteredListener<?> listener : listeners) {
                            final TypeToken<?> genericType1 = listener.getEventType().getGenericType();
                            checkNotNull(genericType1);
                            if (TypeTokenHelper.isAssignable(genericType, genericType1)) {
                                handlers.add(listener);
                            }
                        }
                    } else {
                        handlers.addAll(listeners);
                    }
                }
            }
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

    private final Set<Class<?>> keyEventTypes = TypeToken.of(ChangeDataHolderEvent.ValueChange.class).getTypes().rawTypes().stream()
            .filter(Event.class::isAssignableFrom).collect(Collectors.toSet());

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void register(List<RegisteredListener<?>> listeners) {
        synchronized (this.lock) {
            listeners = listeners.stream()
                    .filter(listener -> {
                        if (!(listener.getHandler() instanceof KeyEventListener) &&
                                this.keyEventTypes.contains(listener.getEventType().getType())) {
                            // Check if somebody has been naughty, this will show a warning
                            // if there is a listener directly listening to a ChangeDataHolderEvent.ValueChange
                            // event, it's a bad idea, the Key#registerEvent should be used instead
                            // This would spam the server, just stop this before damage is done
                            this.logger.warn("Plugin {} attempted to register a listener ({}) that directly listens to"
                                            + "ChangeDataHolderEvent.ValueChange, this is not allowed because it could destroy server performance. "
                                            + "Key#registerEvent is the proper way to handle this event.",
                                    listener.getPlugin().getId(), listener.getHandle().getClass().getName());
                            return false; // Gotcha
                        }
                        return this.listenersByEvent.put(listener.getEventType().getType(), listener);
                    })
                    .collect(Collectors.toList());
        }
        if (!listeners.isEmpty()) {
            this.listenersCache.invalidateAll();

            // Update ShouldFire fields
            updateShouldFireFields();
        }
    }

    private void registerListenerInstance(PluginContainer plugin, Object listener) {
        synchronized (this.registeredListeners) {
            if (this.registeredListeners.contains(listener)) {
                this.logger.warn("Plugin {} attempted to register an already registered listener ({})",
                        plugin.getId(), listener.getClass().getName());
                Thread.dumpStack();
            } else {
                this.registeredListeners.add(listener);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void registerListeners(PluginContainer plugin, Object listener) {
        checkNotNull(plugin, "plugin");
        checkNotNull(listener, "listener");

        registerListenerInstance(plugin, listener);

        final List<RegisteredListener<?>> handlers = new ArrayList<>();
        final Map<Method, String> methodErrors = new HashMap<>();

        final Class<?> handle = listener.getClass();
        for (Method method : handle.getMethods()) {
            final Listener subscribe = method.getAnnotation(Listener.class);
            if (subscribe != null) {
                final String error = getHandlerErrorOrNull(method);
                if (error == null) {
                    final TypeToken eventType = TypeToken.of(method.getGenericParameterTypes()[0]);
                    final AnnotatedEventListener handler;

                    try {
                        handler = this.listenerFactory.create(listener, method);
                    } catch (Exception e) {
                        this.logger.error("Failed to create listener for {} on {}", method, handle, e);
                        continue;
                    }

                    handlers.add(createRegistration(plugin, eventType, subscribe.order(), handler));
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

    @Override
    public <T extends Event> void registerListener(PluginContainer plugin, Class<T> eventClass, Order order, boolean beforeModifications,
            EventListener<? super T> listener) {
        // Ignore the "beforeModifications" property, this is only used in combination with mods
        registerListener(plugin, eventClass, order, listener);
    }

    @Override
    public <T extends Event> void registerListener(PluginContainer plugin, TypeToken<T> eventType, Order order, boolean beforeModifications,
            EventListener<? super T> listener) {
        // Ignore the "beforeModifications" property, this is only used in combination with mods
        registerListener(plugin, eventType, order, listener);
    }

    @Override
    public <T extends Event> void registerListener(PluginContainer plugin, Class<T> eventClass, EventListener<? super T> listener) {
        registerListener(plugin, eventClass, Order.DEFAULT, listener);
    }

    @Override
    public <T extends Event> void registerListener(PluginContainer plugin, TypeToken<T> eventType, EventListener<? super T> listener) {
        registerListener(plugin, eventType, Order.DEFAULT, listener);
    }

    @Override
    public <T extends Event> void registerListener(PluginContainer plugin, Class<T> eventClass, Order order, EventListener<? super T> listener) {
        register(plugin, TypeToken.of(eventClass), order, listener);
    }

    @Override
    public <T extends Event> void registerListener(PluginContainer plugin, TypeToken<T> eventType, Order order, EventListener<? super T> listener) {
        register(plugin, eventType, order, listener);
    }

    public <T extends Event> RegisteredListener<T> register(PluginContainer plugin, TypeToken<T> eventType, Order order, EventListener<? super T> listener) {
        checkNotNull(plugin, "plugin");
        checkNotNull(eventType, "eventType");
        checkNotNull(order, "order");
        checkNotNull(listener, "listener");
        registerListenerInstance(plugin, listener);
        final RegisteredListener<T> registeredListener = createRegistration(plugin, eventType, order, listener);
        register(Collections.singletonList(registeredListener));
        return registeredListener;
    }

    private static <T extends Event> RegisteredListener<T> createRegistration(PluginContainer plugin, TypeToken<T> eventType,
            Order order, EventListener<? super T> handler) {
        TypeToken<?> genericType = null;
        if (GenericEvent.class.isAssignableFrom(eventType.getRawType())) {
            genericType = eventType.resolveType(GENERIC_EVENT_TYPE);
        }
        return new RegisteredListener(plugin, new EventType(eventType.getRawType(), genericType), order, handler);
    }

    private void unregister(Predicate<RegisteredListener<?>> unregister) {
        final Set<Class<?>> changes = new HashSet<>();
        synchronized (this.lock) {
            final Iterator<RegisteredListener<?>> it = this.listenersByEvent.values().iterator();
            while (it.hasNext()) {
                final RegisteredListener<?> listener = it.next();
                if (unregister.test(listener)) {
                    synchronized (this.registeredListeners) {
                        this.registeredListeners.remove(listener.getHandle());
                    }
                    changes.add(listener.getEventType().getType());
                    it.remove();
                }
            }
            changes.removeAll(this.listenersByEvent.keySet());
        }
        if (!changes.isEmpty()) {
            this.listenersCache.invalidateAll();
            // Update ShouldFire fields
            updateShouldFireFields();
        }
    }

    @Override
    public void unregisterListeners(Object listener) {
        checkNotNull(listener, "listener");
        unregister(handler -> listener.equals(handler.getHandle()));
    }

    @Override
    public void unregisterPluginListeners(PluginContainer plugin) {
        checkNotNull(plugin, "plugin");
        unregister(handler -> plugin.equals(handler.getPlugin()));
    }

    private static final class TempDataEventData {

        @Nullable Set<Key<?>> baseKeys;
        @Nullable Set<Key<?>> keys;
        @Nullable DataTransactionResult lastResult;
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Override
    public boolean post(Event event) {
        checkNotNull(event, "event");
        final Class<? extends Event> eventClass = event.getClass();
        final EventType<? extends Event> eventType;
        if (event instanceof GenericEvent) {
            eventType = new EventType(eventClass, checkNotNull(((GenericEvent) event).getGenericType()));
        } else {
            eventType = new EventType(eventClass, null);
        }
        final List<RegisteredListener<?>> listeners = this.listenersCache.get(eventType);
        // Special case
        if (event instanceof AbstractValueChangeEvent) {
            final AbstractValueChangeEvent event1 = (AbstractValueChangeEvent) event;
            final TempDataEventData temp = new TempDataEventData();
            temp.lastResult = event1.getEndResult();
            return post(event, listeners, listener -> {
                if (listener.getHandler() instanceof KeyEventListener) {
                    final KeyEventListener keyEventListener = (KeyEventListener) listener.getHandler();
                    if (keyEventListener.getDataHolderPredicate().test(event1.getTargetHolder())) {
                        final DataTransactionResult newResult = event1.getEndResult();
                        // We need the keys, only regenerate if changed
                        if (temp.keys == null || temp.lastResult != newResult) {
                            // Ignore rejected data, nothing changed for those keys
                            if (temp.baseKeys == null) {
                                temp.baseKeys = new HashSet<>();
                                final DataTransactionResult original = event1.getOriginalChanges();
                                original.getSuccessfulData().forEach(value -> temp.baseKeys.add(value.getKey()));
                                original.getReplacedData().forEach(value -> temp.baseKeys.add(value.getKey()));
                            }
                            if (event1.getOriginalChanges() == newResult) {
                                temp.keys = temp.baseKeys;
                            } else {
                                temp.keys = new HashSet<>();
                                temp.keys.addAll(temp.baseKeys);
                                newResult.getSuccessfulData().forEach(value -> temp.keys.add(value.getKey()));
                                newResult.getReplacedData().forEach(value -> temp.keys.add(value.getKey()));
                            }
                        }
                        if (temp.keys.contains(keyEventListener.getKey())) {
                            listener.handle(event);
                        }
                    }
                }
            });
        }
        return post(event, listeners);
    }

    private boolean post(Event event, Collection<RegisteredListener<?>> listeners) {
        return post(event, listeners, listener -> listener.handle(event));
    }

    private boolean post(Event event, Collection<RegisteredListener<?>> listeners,
            ThrowableConsumer<RegisteredListener, Exception> handler) {
        final Thread thread = Thread.currentThread();
        final CauseStack causeStack = LanternCauseStackManager.INSTANCE.getCauseStackOrEmpty(thread);
        if (thread instanceof SyncLanternThread) {
            synchronized (this.syncPostLock) {
                return post(causeStack, event, listeners, handler);
            }
        }
        return post(causeStack, event, listeners, handler);
    }

    private boolean post(CauseStack causeStack, Event event, Collection<RegisteredListener<?>> listeners,
            ThrowableConsumer<RegisteredListener, Exception> handler) {
        for (RegisteredListener listener : listeners) {
            // Add the calling plugin to the cause stack
            causeStack.pushCause(listener.getPlugin());
            try (CauseStack.Frame ignored = causeStack.pushCauseFrame()) {
                if (event instanceof AbstractEvent) {
                    ((AbstractEvent) event).currentOrder = listener.getOrder();
                }
                handler.accept(listener);
            } catch (Throwable e) {
                this.logger.error("Could not pass {} to {}", event.getClass().getSimpleName(),
                        listener.getPlugin(), e);
            }
            causeStack.popCause();
        }
        if (event instanceof AbstractEvent) {
            ((AbstractEvent) event).currentOrder = null;
        }
        return event instanceof Cancellable && ((Cancellable) event).isCancelled();
    }
}
