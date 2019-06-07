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
package org.lanternpowered.api.inject.service

import com.google.inject.ProvidedBy
import org.lanternpowered.api.Lantern
import org.lanternpowered.api.plugin.PluginContainer
import org.spongepowered.api.service.ProviderRegistration
import java.util.Optional

/**
 * Represents a reference for a service of type [T].
 *
 * @param T The service type
 */
@FunctionalInterface
@ProvidedBy(ServiceRefProvider::class)
interface ServiceRef<T> {

    /**
     * Gets the provider registration.
     */
    val registration: ProviderRegistration<T>

    /**
     * The service type.
     */
    val type: Class<T>
        get() = this.registration.service

    /**
     * Gets the [PluginContainer] that registered the service instance.
     */
    val plugin: PluginContainer
        get() = this.registration.plugin

    /**
     * The service instance.
     */
    val instance: T
        get() = this.registration.provider

    operator fun invoke() = this.instance
    fun get() = this.instance

    fun <E> `as`(type: Class<E>): Optional<E> {
        return if (type.isInstance(instance)) Optional.of(type.cast(instance)) else Optional.empty()
    }

    companion object {

        /**
         * Creates a [ServiceRef] reference for the given service type.
         */
        fun <T> of(type: Class<T>) = object : ServiceRef<T> {
            override val registration: ProviderRegistration<T>
                get() = Lantern.serviceManager.getRegistration(type).orElseThrow { IllegalStateException("Service not found: ${type.name}") }
        }
    }
}
