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
package org.lanternpowered.api.xevent

import org.lanternpowered.api.util.optional.orNull
import java.util.Optional
import kotlin.reflect.KClass

/**
 * Represents the bus to handle [Xevent]s.
 */
interface XeventBus {

    /**
     * Posts the [Xevent].
     *
     * @param event The event
     */
    fun post(event: Xevent)

    /**
     * Posts the [Xevent] provided by the given supplier if
     * there are handlers registered for the given [Xevent] type.
     *
     * @param eventType The event type
     * @param supplier The event supplier
     * @param T The type of the event
     */
    fun <T : Xevent> post(eventType: Class<T>, supplier: () -> T): Optional<T>

    /**
     * Posts the [Xevent] provided by the given supplier if
     * there are handlers registered for the given [Xevent] type.
     *
     * @param eventType The event type
     * @param supplier The event supplier
     * @param T The type of the event
     */
    fun <T : Xevent> post(eventType: KClass<T>, supplier: () -> T): T? {
        return post(eventType.java, supplier).orNull()
    }

    /**
     * Registers all the methods of the object annotated with
     * [XeventListener] as event handlers.
     *
     * @param any The object
     */
    fun register(any: Any)

    /**
     * Registers the handler for the given [Xevent] type.
     *
     * @param eventType The event type
     * @param handler The handler
     * @param T The type of the event
     */
    fun <T : Xevent> register(eventType: KClass<T>, handler: XeventHandler<T>) {
        register(eventType.java, handler)
    }

    /**
     * Registers the handler for the given [Xevent] type.
     *
     * @param eventType The event type
     * @param handler The handler
     * @param T The type of the event
     */
    fun <T : Xevent> register(eventType: Class<T>, handler: XeventHandler<T>)

    /**
     * Unregisters all the handlers methods that
     * got registered by using the object.
     *
     * @param any The object
     */
    fun unregister(any: Any)

    /**
     * Unregisters the given handler from the
     * given [Xevent] type.
     *
     * @param eventType The event type
     * @param handler The handler
     * @param T The type of the event
     */
    fun <T : Xevent> unregister(eventType: KClass<T>, handler: XeventHandler<T>) {
        unregister(eventType.java, handler)
    }

    /**
     * Unregisters the given handler from the
     * given [Xevent] type.
     *
     * @param eventType The event type
     * @param handler The handler
     * @param T The type of the event
     */
    fun <T : Xevent> unregister(eventType: Class<T>, handler: XeventHandler<T>)
}
