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

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.Game
import org.lanternpowered.api.cause.Cause
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.emptyCause
import org.lanternpowered.api.cause.first
import org.lanternpowered.api.event.lifecycle.ProvideServiceEvent
import org.lanternpowered.api.plugin.PluginContainer
import org.lanternpowered.api.service.ServiceProvider
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.api.util.optional.asOptional
import org.spongepowered.api.service.ServiceRegistration
import java.util.Optional
import java.util.function.Supplier
import kotlin.reflect.KClass

class LanternServiceProvider(private val game: Game) : ServiceProvider {

    private val registrationMap = mutableMapOf<Class<*>, ServiceRegistration<*>>()
    private val onRegister = mutableListOf<(ServiceRegistration<*>) -> Unit>()

    fun onRegister(fn: (ServiceRegistration<*>) -> Unit) {
        this.onRegister += fn
    }

    inline fun <reified T : Any> register(): T? =
            this.register(T::class)

    inline fun <reified T : Any> register(noinline default: () -> Pair<PluginContainer, T>): T =
            this.register(T::class, default)

    fun <T : Any> register(serviceClass: KClass<T>): T? =
            this.register(serviceClass.java, null)

    fun <T : Any> register(serviceClass: KClass<T>, default: () -> Pair<PluginContainer, T>): T =
            this.register(serviceClass.java, default)!!

    private fun <T : Any> register(serviceClass: Class<T>, default: (() -> Pair<PluginContainer, T>)? = null): T? {
        val cause = emptyCause()
        var supplier: (() -> Pair<PluginContainer, T>)? = null
        val event = object : ProvideServiceEvent<T> {
            override fun getCause(): Cause = cause
            override fun getGame(): Game = this@LanternServiceProvider.game
            override fun getGenericType(): TypeToken<T> = TypeToken.of(serviceClass)
            override fun suggest(serviceFactory: Supplier<T>) {
                val plugin: PluginContainer = CauseStack.current().first()
                        ?: error("No plugin found in the cause stack.")
                // The first one wins, I guess
                if (supplier == null)
                    supplier = { plugin to serviceFactory.get() }
            }
        }
        this.game.eventManager.post(event)
        if (supplier == null)
            supplier = default
        if (supplier == null)
            return null
        val (plugin, service) = supplier!!()
        val registration = LanternServiceRegistration(serviceClass, service, plugin)
        this.registrationMap[serviceClass] = registration
        for (onRegister in this.onRegister)
            onRegister(registration)
        return service
    }

    val registrations: Collection<ServiceRegistration<*>>
        get() = this.registrationMap.values.toImmutableList()

    override fun <T : Any> provide(serviceClass: KClass<T>): T? = this.provideNullable(serviceClass.java)
    override fun <T : Any> provide(serviceClass: Class<T>): Optional<T> = this.provideNullable(serviceClass).asOptional()

    override fun <T : Any> getRegistration(serviceClass: Class<T>): Optional<ServiceRegistration<T>> =
            this.getNullableRegistration(serviceClass).asOptional()

    private fun <T : Any> provideNullable(serviceClass: Class<T>): T? =
            this.getNullableRegistration(serviceClass)?.service()

    @Suppress("UNCHECKED_CAST")
    private fun <T> getNullableRegistration(serviceClass: Class<T>): ServiceRegistration<T>? =
            this.registrationMap[serviceClass] as? ServiceRegistration<T>
}

private data class LanternServiceRegistration<T>(
        private val serviceClass: Class<T>,
        private val service: T,
        private val plugin: PluginContainer
) : ServiceRegistration<T> {
    override fun serviceClass(): Class<T> = this.serviceClass
    override fun service(): T = this.service
    override fun pluginContainer(): PluginContainer = this.plugin
}
