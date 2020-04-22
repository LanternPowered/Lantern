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
package org.lanternpowered.server.service

import com.google.common.collect.ImmutableList
import com.google.common.collect.MapMaker
import org.lanternpowered.api.cause.CauseStack.Companion.currentOrEmpty
import org.lanternpowered.api.cause.withFrame
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.event.LanternEventFactory
import org.lanternpowered.api.service.ServiceManager
import org.lanternpowered.api.util.optional.optional
import org.spongepowered.api.event.cause.EventContextKeys
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.service.ProviderRegistration
import org.spongepowered.api.service.ProvisioningException
import java.util.Optional
import kotlin.reflect.KClass

object LanternServiceManager : ServiceManager {

    private val providers: MutableMap<Class<*>, ProviderRegistration<*>> = MapMaker()
            .concurrencyLevel(3).makeMap<Class<*>, ProviderRegistration<*>>()

    val providerRegistrations: Collection<ProviderRegistration<*>>
        get() = ImmutableList.copyOf(this.providers.values)

    @Suppress("UNCHECKED_CAST")
    override fun <T> setProvider(plugin: PluginContainer, service: Class<T>, provider: T) {
        val newProvider: ProviderRegistration<T> = Provider(plugin, service, provider)
        val oldProvider: ProviderRegistration<T>? = this.providers.put(service, newProvider) as ProviderRegistration<T>?
        currentOrEmpty().withFrame { frame ->
            frame.pushCause(plugin)
            frame.addContext(EventContextKeys.SERVICE_MANAGER, this)
            EventManager.post(LanternEventFactory.createChangeServiceProviderEvent<T>(
                    frame.currentCause, newProvider, oldProvider))
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getNullableRegistration(service: Class<T>): ProviderRegistration<T>? =
            (this.providers[service] as ProviderRegistration<T>?)

    override fun <T> getRegistration(service: Class<T>): Optional<ProviderRegistration<T>> = getNullableRegistration(service).optional()

    private fun <T> provideNullable(service: Class<T>): T? = getNullableRegistration(service)?.provider

    override fun <T : Any> provide(service: KClass<T>): T? = provideNullable(service.java)
    override fun <T : Any> provide(service: Class<T>): Optional<T> = provideNullable(service).optional()

    override fun <T : Any> provideUnchecked(service: KClass<T>): T = provideUnchecked(service.java)

    override fun <T> provideUnchecked(service: Class<T>): T =
            provideNullable(service) ?: throw ProvisioningException(
                    "No provider is registered for the service '" + service.name + "'", service)

    private class Provider<T> internal constructor(
            private val container: PluginContainer,
            private val service: Class<T>,
            private val provider: T
    ) : ProviderRegistration<T> {

        override fun getService(): Class<T> = this.service
        override fun getProvider(): T = this.provider
        override fun getPlugin(): PluginContainer = this.container
    }
}