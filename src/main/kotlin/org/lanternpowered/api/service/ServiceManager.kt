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
