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
package org.lanternpowered.server.inject.provider;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import org.lanternpowered.server.inject.InjectionPoint;
import org.lanternpowered.server.inject.Service;
import org.lanternpowered.server.inject.ServiceProvider;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.ServiceManager;

import java.lang.reflect.TypeVariable;
import java.util.Optional;
import java.util.function.Supplier;

public class ServiceObjectProvider implements Provider<Service> {

    private final static TypeVariable<Class<Service>> VALUE_VARIABLE = Service.class.getTypeParameters()[0];

    @Inject private ServiceManager serviceManager;
    @Inject private PluginContainer pluginContainer;
    @Inject private InjectionPoint point;
    @Inject private Injector injector;

    @SuppressWarnings("unchecked")
    @Override
    public Service get() {
        //noinspection unchecked
        final Class serviceType = this.point.getType().resolveType(VALUE_VARIABLE).getRawType();
        if (serviceType == null) {
            throw new IllegalStateException("Missing service type.");
        }
        final ServiceProvider serviceProvider = this.point.getAnnotation(ServiceProvider.class);
        final Service service = () -> {
            final Optional<ProviderRegistration> optRegistration = this.serviceManager.getRegistration(serviceType);
            return optRegistration.orElseThrow(() -> new IllegalStateException("Cannot find the service: " + serviceType.getName()));
        };
        final Optional<ProviderRegistration> optRegistration = this.serviceManager.getRegistration(serviceType);
        if (serviceProvider == null || (!serviceProvider.override() && optRegistration.isPresent())) {
            return service;
        }
        final Class<?> provider = serviceProvider.value();
        final Object serviceInstance;
        if (Provider.class.isAssignableFrom(provider)) {
            serviceInstance = ((Provider) this.injector.getInstance(provider)).get();
        } else if (Supplier.class.isAssignableFrom(provider)) {
            serviceInstance = ((Supplier) this.injector.getInstance(provider)).get();
        } else if (serviceType.isAssignableFrom(provider)) {
            serviceInstance = this.injector.getInstance(provider);
        } else {
            throw new IllegalStateException(String.format("Invalid service instance provider %s for %s",
                    serviceType.getName(), provider.getName()));
        }
        if (!serviceType.isInstance(serviceInstance)) {
            throw new IllegalStateException(String.format("The provided service instance %s doesn't implement the service %s",
                    serviceInstance.getClass().getName(), serviceType.getName()));
        }
        this.serviceManager.setProvider(this.pluginContainer, serviceType, serviceInstance);
        return service;
    }
}
