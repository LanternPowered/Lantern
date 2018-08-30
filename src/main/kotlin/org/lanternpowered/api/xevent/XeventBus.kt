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
package org.lanternpowered.api.xevent

import org.lanternpowered.api.ext.*
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
        return !post(eventType.java, supplier)
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
