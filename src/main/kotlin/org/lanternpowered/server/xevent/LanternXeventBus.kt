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
package org.lanternpowered.server.xevent

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.common.collect.HashMultimap
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.util.type.typeToken
import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.api.xevent.Xevent
import org.lanternpowered.api.xevent.XeventBus
import org.lanternpowered.api.xevent.XeventHandler
import org.lanternpowered.api.xevent.XeventListener
import org.lanternpowered.lmbda.LambdaFactory
import org.lanternpowered.lmbda.LambdaType
import org.lanternpowered.lmbda.kt.privateLookupIn
import org.lanternpowered.server.game.Lantern
import java.lang.invoke.MethodHandles
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.ArrayList
import java.util.Optional
import java.util.concurrent.ConcurrentHashMap

class LanternXeventBus : XeventBus {

    companion object {

        interface UntargetedHandler {

            fun handle(target: Any, xevent: Xevent)
        }

        private val lookup = MethodHandles.lookup()
        private val untargetedHandlerType = object : LambdaType<UntargetedHandler>() {}

        /**
         * A handler that isn't targeting a specific handler object. This means
         * that this handler can be reused for multiple objects. This is only
         * used for method event handlers.
         */
        internal class UntargetedMethodHandler(val eventClass: Class<*>,
                                               val handler: UntargetedHandler)

        /**
         * Cache all the method event handlers, things will get registered/unregistered
         * quite a lot so avoid regenerating classes/reflection lookups.
         */
        private val untargetedMethodHandlersByObjectClass = ConcurrentHashMap<Class<*>, List<UntargetedMethodHandler>>()

        /**
         * A map with all the generated [UntargetedMethodHandler] for a specific method.
         */
        private val untargetedMethodHandlerByMethod = ConcurrentHashMap<Method, UntargetedMethodHandler>()

        private fun loadUntargetedMethodHandlers(objectClass: Class<*>): List<UntargetedMethodHandler> {
            val handlers = ArrayList<UntargetedMethodHandler>()
            loadUntargetedMethodHandlers(handlers, objectClass)
            return handlers
        }

        private fun loadUntargetedMethodHandlers(handlers: MutableList<UntargetedMethodHandler>, objectClass: Class<*>) {
            for (method in objectClass.declaredMethods) {
                // Only add entries for methods that are declared, to avoid
                // duplicate entries when methods are overridden.
                if (method.getDeclaredAnnotation(XeventListener::class.java) == null) {
                    continue
                }
                check(!Modifier.isStatic(method.modifiers)) { "ShardeventListener methods cannot be static" }
                check(method.returnType == Void.TYPE) { "ShardeventListener methods cannot have a return type" }
                check(method.parameterCount == 1 && Xevent::class.java.isAssignableFrom(method.parameterTypes[0])) {
                        "ShardeventListener methods can only have one parameter and must extend Shardevent"}
                // Generate a Shardevent handler for the method
                val methodHandler = this.untargetedMethodHandlerByMethod.computeIfAbsent(method) {
                    // Convert the method to a method handle
                    val methodHandle = this.lookup.privateLookupIn(method.declaringClass).unreflect(method)
                    UntargetedMethodHandler(method.parameterTypes[0], LambdaFactory.create(this.untargetedHandlerType, methodHandle))
                }
                handlers.add(methodHandler)
            }
            for (interf in objectClass.interfaces) {
                loadUntargetedMethodHandlers(handlers, interf)
            }
            val superclass = objectClass.superclass
            if (superclass != null && superclass != Any::class.java) {
                loadUntargetedMethodHandlers(handlers, superclass)
            }
        }

        /**
         * Gets all the [UntargetedMethodHandler]s that are
         * present on the given object class.
         *
         * @param objectClass The object class
         * @return The untargeted method handlers
         */
        private fun getUntargetedMethodHandlers(objectClass: Class<*>): List<UntargetedMethodHandler> {
            return this.untargetedMethodHandlersByObjectClass.computeIfAbsent(objectClass, ::loadUntargetedMethodHandlers)
        }
    }

    private abstract class InternalHandler(val handle: Any) {

        abstract fun handle(event: Xevent)
    }

    private val handlersByClass = HashMultimap.create<Class<*>, InternalHandler>()
    private val handlerCache = Caffeine.newBuilder().build(::loadHandlers)

    private fun loadHandlers(eventClass: Class<*>): List<InternalHandler> {
        val handlers = ArrayList<InternalHandler>()
        val types = eventClass.typeToken.types.rawTypes()
        synchronized(this.handlersByClass) {
            for (type in types) {
                if (Xevent::class.java.isAssignableFrom(type)) {
                    handlers.addAll(this.handlersByClass.get(type))
                }
            }
        }
        return handlers
    }

    override fun post(event: Xevent) {
        post(event, this.handlerCache.get(event.javaClass)!!)
    }

    override fun <T : Xevent> post(eventType: Class<T>, supplier: () -> T): Optional<T> {
        val handlers = this.handlerCache.get(eventType)!!
        var event: T? = null
        if (!handlers.isEmpty()) {
            event = supplier()
            post(event, handlers)
        }
        return event.optional()
    }

    private fun post(event: Xevent, handlers: List<InternalHandler>) {
        for (handler in handlers) {
            try {
                handler.handle(event)
            } catch (e: Exception) {
                Lantern.getLogger().error("Failed to handle Shardevent", e)
            }
        }
    }

    override fun register(any: Any) {
        val untargetedHandlers = getUntargetedMethodHandlers(any.javaClass)
        synchronized(this.handlersByClass) {
            for (handler in untargetedHandlers) {
                this.handlersByClass.put(handler.eventClass, object : InternalHandler(any) {
                    override fun handle(event: Xevent) {
                        handler.handler.handle(this.handle, event)
                    }
                })
            }
        }
        this.handlerCache.invalidateAll()
    }

    override fun <T : Xevent> register(eventType: Class<T>, handler: XeventHandler<T>) {
        synchronized(this.handlersByClass) {
            this.handlersByClass.put(eventType, object : InternalHandler(handler) {
                override fun handle(event: Xevent) {
                    handler.handle(event.uncheckedCast())
                }
            })
        }
        this.handlerCache.invalidateAll()
    }

    override fun unregister(any: Any) {
        synchronized(this.handlersByClass) {
            this.handlersByClass.values().removeIf { it.handle === any }
        }
        this.handlerCache.invalidateAll()
    }

    override fun <T : Xevent> unregister(eventType: Class<T>, handler: XeventHandler<T>) {
        synchronized(this.handlersByClass) {
            this.handlersByClass.get(eventType).removeIf { it.handle === handler }
        }
        this.handlerCache.invalidateAll()
    }
}
