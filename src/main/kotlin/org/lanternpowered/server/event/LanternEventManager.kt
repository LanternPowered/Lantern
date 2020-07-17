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
package org.lanternpowered.server.event

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.common.collect.HashMultimap
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.withFrame
import org.lanternpowered.api.event.Event
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.event.Order
import org.lanternpowered.api.plugin.id
import org.lanternpowered.api.util.collections.toImmutableSet
import org.lanternpowered.api.util.type.TypeToken
import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.server.cause.LanternCauseStackManager
import org.lanternpowered.server.data.key.ValueKeyEventListener
import org.lanternpowered.server.event.filter.FilterFactory
import org.lanternpowered.server.util.DefineableClassLoader
import org.lanternpowered.server.util.SyncLanternThread
import org.lanternpowered.server.util.SystemProperties
import org.lanternpowered.server.util.annotations.getAnnotation
import org.slf4j.LoggerFactory
import org.spongepowered.api.event.Cancellable
import org.spongepowered.api.event.EventListener
import org.spongepowered.api.event.GenericEvent
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.data.ChangeDataHolderEvent.ValueChange
import org.spongepowered.api.event.filter.type.Exclude
import org.spongepowered.api.event.filter.type.Include
import org.spongepowered.api.event.impl.AbstractEvent
import org.spongepowered.plugin.PluginContainer
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

object LanternEventManager : EventManager {

    private val allShouldFire = SystemProperties.get().getBooleanProperty("sponge.shouldFireAll")

    private val logger = LoggerFactory.getLogger("EventManager")
    private val lock = Any()
    private val registeredListeners = mutableSetOf<Any>()
    private val listenersByEvent = HashMultimap.create<Class<*>, RegisteredListener<*>>()

    private val classLoader = DefineableClassLoader()
    private val listenerFactory = ClassEventListenerFactory("org.lanternpowered.server.event.listener",
            FilterFactory("org.lanternpowered.server.event.filters", this.classLoader), this.classLoader)

    /**
     * A lock to synchronize events called from a [SyncLanternThread].
     */
    private val syncPostLock = Any()

    /**
     * A cache of all the handlers for an event type for quick event posting.
     */
    private val listenersCache = Caffeine.newBuilder()
            .initialCapacity(150).build { eventType: EventType<*> -> bakeHandlers(eventType) }

    private val shouldFireFields = mutableMapOf<Class<*>, ShouldFireField>()

    init {
        for (field in ShouldFire::class.java.fields) {
            val target = field.getAnnotation(ShouldFireTarget::class.java)?.value?.java ?: continue
            this.shouldFireFields[target] = ShouldFireField(target, field)
        }

        updateShouldFireFields()
    }

    /**
     * Updates all the [ShouldFire] fields.
     */
    private fun updateShouldFireFields() {
        val registeredTypes = synchronized(this.lock) { this.listenersByEvent.keySet().toSet() }
        for ((eventClass, value) in this.shouldFireFields) {
            var shouldFire = false
            if (this.allShouldFire) {
                shouldFire = true
            } else {
                for (registeredType in registeredTypes) {
                    if (registeredType.isAssignableFrom(eventClass) ||  // Sub class
                            eventClass.isAssignableFrom(registeredType)) {  // Or super class
                        shouldFire = true // We got a match
                        break
                    }
                }
            }
            value.setState(shouldFire)
        }
    }

    private fun <T : Event> bakeHandlers(eventType: EventType<T>): List<RegisteredListener<T>> {
        val handlers = mutableListOf<RegisteredListener<T>>()
        val types = TypeToken.of(eventType.eventClass).types.rawTypes()

        synchronized(this.lock) {
            for (type in types) {
                if (!Event::class.java.isAssignableFrom(type))
                    continue
                for (listener in this.listenersByEvent[type]) {
                    if (listener.isApplicable(eventType))
                        handlers.add(listener.uncheckedCast())
                }
            }
        }

        handlers.sort()
        return handlers
    }

    override fun <T : Event> registerListener(plugin: PluginContainer, eventClass: KClass<T>, listener: EventListener<in T>) {
        registerListener(plugin, TypeToken.of(eventClass.java), listener)
    }

    override fun <T : Event> registerListener(plugin: PluginContainer, eventClass: Class<T>, listener: EventListener<in T>) {
        registerListener(plugin, TypeToken.of(eventClass), listener)
    }

    override fun <T : Event> registerListener(plugin: PluginContainer, eventType: TypeToken<T>, listener: EventListener<in T>) {
        registerListener(plugin, eventType, Order.DEFAULT, listener)
    }

    override fun <T : Event> registerListener(plugin: PluginContainer, eventClass: Class<T>, order: Order, listener: EventListener<in T>) {
        registerListener(plugin, eventClass, Order.DEFAULT, listener)
    }

    override fun <T : Event> registerListener(plugin: PluginContainer, eventType: TypeToken<T>, order: Order, listener: EventListener<in T>) {
        register(plugin, eventType, order, listener)
    }

    fun <T : Event> register(plugin: PluginContainer, eventType: TypeToken<T>, order: Order, listener: EventListener<in T>): RegisteredListener<T> {
        val registered = RegisteredListener(plugin, listener, EventType.of(eventType), order)
        register(plugin, listener, listOf(registered))
        return registered
    }

    override fun registerListeners(plugin: PluginContainer, instance: Any) {
        val handlers = mutableListOf<RegisteredListener<*>>()
        val methodErrors = mutableMapOf<Method, String>()

        val handle: Class<*> = instance.javaClass
        for (method in handle.methods) {
            val subscribe = method.getAnnotation(Listener::class.java)
            if (subscribe != null) {
                val error = getHandlerErrorOrNull(method)
                if (error == null) {
                    val eventToken = TypeToken.of(method.genericParameterTypes[0])
                    val handler = try {
                        this.listenerFactory.create(instance, method)
                    } catch (e: Exception) {
                        this.logger.error("Failed to create listener for {} on {}", method, handle, e)
                        continue
                    }

                    val excludedTypes = method.getAnnotation<Exclude>()?.value?.asList()?.map { it.java }
                    val includedTypes = method.getAnnotation<Include>()?.value?.asList()?.map { it.java }

                    val eventType = EventType.of(eventToken.uncheckedCast<TypeToken<Event>>())
                    val registeredListener = RegisteredListener(
                            plugin, handler, eventType, subscribe.order, includedTypes, excludedTypes)
                    handlers.add(registeredListener)
                } else {
                    methodErrors[method] = error
                }
            }
        }

        // getMethods() doesn't return private methods. Do another check to warn about those.
        var handleParent = handle
        while (handleParent != Any::class.java) {
            for (method in handleParent.declaredMethods) {
                if (method.getAnnotation(Listener::class.java) != null && !methodErrors.containsKey(method)) {
                    val error = getHandlerErrorOrNull(method)
                    if (error != null) {
                        methodErrors[method] = error
                    }
                }
            }
            handleParent = handleParent.superclass
        }


        for ((key, value) in methodErrors) {
            this.logger.warn("Invalid listener method $key in ${key.declaringClass.name}: $value")
        }

        register(plugin, instance, handlers)
    }

    private fun getHandlerErrorOrNull(method: Method): String? {
        val modifiers = method.modifiers
        val errors = mutableListOf<String>()
        if (Modifier.isStatic(modifiers))
            errors.add("method must not be static")
        if (!Modifier.isPublic(modifiers))
            errors.add("method must be public")
        if (Modifier.isAbstract(modifiers))
            errors.add("method must not be abstract")
        if (method.declaringClass.isInterface)
            errors.add("interfaces cannot declare listeners")
        if (method.returnType != Void.TYPE)
            errors.add("method must return void")
        val parameters = method.parameterTypes
        if (parameters.isEmpty() || !Event::class.java.isAssignableFrom(parameters[0]))
            errors.add("method must have an Event as its first parameter")
        val exclude = method.getAnnotation<Exclude>()
        val include = method.getAnnotation<Include>()
        if (exclude != null && include != null)
            errors.add("method cannot have both @Exclude and @Include")
        return if (errors.isEmpty()) null else errors.joinToString(", ")
    }

    private val keyEventTypes: Set<Class<*>> = TypeToken.of(ValueChange::class.java).types.rawTypes().stream()
            .filter { cls -> Event::class.java.isAssignableFrom(cls) }.toImmutableSet()

    private fun register(plugin: PluginContainer, instance: Any, listeners: List<RegisteredListener<*>>) {
        val listenersToInvalidate = synchronized(this.lock) {
            if (!this.registeredListeners.add(instance)) {
                this.logger.warn("Plugin {} attempted to register an already registered listener ({})",
                        plugin.id, instance.javaClass.name, IllegalStateException("Duplicate listener instance: $instance"))
            }
            listeners.filter { listener ->
                if (listener.listener !is ValueKeyEventListener && this.keyEventTypes.contains(listener.eventType.eventClass)) {
                    // Check if somebody has been naughty, this will show a warning
                    // if there is a listener directly listening to a ChangeDataHolderEvent.ValueChange
                    // event, it's a bad idea, the Key#registerEvent should be used instead
                    // This would spam the server, just stop this before damage is done
                    this.logger.warn("Plugin {} attempted to register a listener ({}) that directly listens to"
                            + "ChangeDataHolderEvent.ValueChange, this is not allowed because it could destroy server performance. "
                            + "Key#registerEvent is the proper way to handle this event.",
                            listener.plugin.id, listener.handle.javaClass.name)
                    return@filter false // Gotcha
                }
                this.listenersByEvent.put(listener.eventType.eventClass, listener)
            }
        }
        if (listenersToInvalidate.isNotEmpty()) {
            this.listenersCache.invalidateAll()
            updateShouldFireFields()
        }
    }

    private fun unregister(unregister: (RegisteredListener<*>) -> Boolean) {
        val changes = mutableSetOf<Class<*>>()
        synchronized(this.lock) {
            val it = this.listenersByEvent.values().iterator()
            while (it.hasNext()) {
                val listener = it.next()
                if (unregister(listener)) {
                    this.registeredListeners.remove(listener.handle)
                    changes.add(listener.eventType.eventClass)
                    it.remove()
                }
            }
            changes.removeAll(this.listenersByEvent.keySet())
        }
        if (changes.isNotEmpty()) {
            this.listenersCache.invalidateAll()
            updateShouldFireFields()
        }
    }

    override fun unregisterListeners(instance: Any) {
        unregister { handler -> instance == handler.handle }
    }

    override fun unregisterPluginListeners(plugin: PluginContainer) {
        unregister { handler -> plugin == handler.plugin }
    }

    private fun <E : Event> E.eventType(): EventType<E> =
            EventType(this.javaClass, (this as? GenericEvent<*>)?.genericType)

    /**
     * Posts the [Event], but only handlers from the given
     * [PluginContainer] will be called.
     */
    fun postFor(event: Event, plugin: PluginContainer): Boolean {
        val listeners = this.listenersCache.get(event.eventType())!!
                .filter { it.plugin == plugin }
        return post(event, listeners)
    }

    override fun post(event: Event): Boolean {
        val eventType = event.eventType()
        val listeners = this.listenersCache.get(eventType)!!
        /* TODO
        // Special case
        if (event instanceof AbstractValueChangeEvent) {
            final AbstractValueChangeEvent event1 = (AbstractValueChangeEvent) event;
            final TempDataEventData temp = new TempDataEventData();
            temp.lastResult = event1.getEndResult();
            return post(event, listeners, listener -> {
                if (listener.getHandler() instanceof ValueKeyEventListener) {
                    final ValueKeyEventListener keyEventListener = (ValueKeyEventListener) listener.getHandler();
                    if (keyEventListener.getDataHolderFilter().invoke(event1.getTargetHolder())) {
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
        */
        return post(event, listeners)
    }

    private fun post(event: Event, listeners: Collection<RegisteredListener<*>>): Boolean =
            post(event, listeners) { listener -> listener.uncheckedCast<RegisteredListener<Event>>().handle(event) }

    private fun post(event: Event, listeners: Collection<RegisteredListener<*>>, handler: (RegisteredListener<*>) -> Unit): Boolean {
        val thread = Thread.currentThread()
        val causeStack = LanternCauseStackManager.getCauseStackOrEmpty(thread)
        if (thread is SyncLanternThread)
            return synchronized(this.syncPostLock) { post(causeStack, event, listeners, handler) }
        return post(causeStack, event, listeners, handler)
    }

    private fun post(
            causeStack: CauseStack, event: Event, listeners: Collection<RegisteredListener<*>>, handler: (RegisteredListener<*>) -> Unit
    ): Boolean {
        for (listener in listeners) {
            // Add the calling plugin to the cause stack
            causeStack.pushCause(listener.plugin)
            try {
                causeStack.withFrame {
                    if (event is AbstractEvent)
                        event.currentOrder = listener.order
                    handler(listener)
                }
            } catch (e: Throwable) {
                this.logger.error("Could not pass ${event.javaClass.simpleName} to ${listener.plugin}", e)
            }
            causeStack.popCause()
        }
        if (event is AbstractEvent)
            event.currentOrder = null
        return event is Cancellable && (event as Cancellable).isCancelled
    }
}
