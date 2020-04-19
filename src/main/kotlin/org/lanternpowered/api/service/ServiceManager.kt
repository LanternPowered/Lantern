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
import org.spongepowered.api.service.ProvisioningException
import kotlin.reflect.KClass

/**
 * Return a provider for the given service, raising an unchecked exception
 * if a provider does not exist.

 * The returned provider may be a proxy to the real underlying proxy,
 * depending on the implementation of the service manager.
 *
 * @param T The type of service
 * @return A provider
 * @throws ProvisioningException Thrown if a provider cannot be provisioned
 */
inline fun <reified T : Any> serviceOf(): T = ServiceManager.provideUnchecked()

/**
 * Return a provider for the given service, if one is available.
 *
 * <p>The returned provider may be a proxy to the real underlying proxy,
 * depending on the implementation of the service manager.</p>
 *
 * @param T The type of service
 * @return A provider, if available
 */
inline fun <reified T : Any> ServiceManager.provide(): T? = provide(T::class)

/**
 * Return a provider for the given service, raising an unchecked exception
 * if a provider does not exist.

 * The returned provider may be a proxy to the real underlying proxy,
 * depending on the implementation of the service manager.
 *
 * @param T The type of service
 * @return A provider
 * @throws ProvisioningException Thrown if a provider cannot be provisioned
 */
inline fun <reified T : Any> ServiceManager.provideUnchecked(): T = provideUnchecked(T::class)

/**
 * The service manager.
 */
interface ServiceManager : org.spongepowered.api.service.ServiceManager {

    /**
     * Return a provider for the given service, if one is available.
     *
     * <p>The returned provider may be a proxy to the real underlying proxy,
     * depending on the implementation of the service manager.</p>
     *
     * @param service The service
     * @param T The type of service
     * @return A provider, if available
     */
    fun <T : Any> provide(service: KClass<T>): T?

    /**
     * Return a provider for the given service, raising an unchecked exception
     * if a provider does not exist.

     * The returned provider may be a proxy to the real underlying proxy,
     * depending on the implementation of the service manager.
     *
     * @param service The service
     * @param T The type of service
     * @return A provider
     * @throws ProvisioningException Thrown if a provider cannot be provisioned
     */
    fun <T : Any> provideUnchecked(service: KClass<T>): T

    /**
     * The singleton instance of the service manager.
     */
    companion object : ServiceManager by Lantern.serviceManager
}
