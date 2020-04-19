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
