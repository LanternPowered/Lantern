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
package org.lanternpowered.api.service

import org.lanternpowered.api.Lantern
import org.lanternpowered.api.util.optional.asOptional
import org.spongepowered.api.service.ban.BanService
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.service.pagination.PaginationService
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.service.whitelist.WhitelistService
import java.util.Optional
import kotlin.reflect.KClass

/**
 * Provides the service represented by the given type [T].
 *
 * @param T The type of service
 * @return The service, if one exists
 */
inline fun <reified T : Any> serviceOf(): T? = ServiceProvider.provide()

/**
 * Return a provider for the given service, if one is available.
 *
 * <p>The returned provider may be a proxy to the real underlying proxy,
 * depending on the implementation of the service manager.</p>
 *
 * @param T The type of service
 * @return A provider, if available
 */
inline fun <reified T : Any> ServiceProvider.provide(): T? = provide(T::class)

/**
 * The service provider.
 */
interface ServiceProvider : org.spongepowered.api.service.ServiceProvider {

    /**
     * Provides the service represented by the given [KClass].
     *
     * @param serviceClass The service class
     * @param <T> The type of service
     * @return The service, if one exists
     */
    fun <T : Any> provide(serviceClass: KClass<T>): T?

    override fun paginationService(): PaginationService = provide() ?: error("Pagination service is unavailable.")
    override fun banService(): BanService = provide() ?: error("Ban service is unavailable.")
    override fun economyService(): Optional<EconomyService> = provide<EconomyService>().asOptional()
    override fun permissionService(): PermissionService = provide() ?: error("Permission service is unavailable.")
    override fun whitelistService(): WhitelistService = provide() ?: error("Whitelist service is unavailable.")

    /**
     * The singleton instance of the service manager.
     */
    companion object : ServiceProvider by Lantern.serviceProvider
}
