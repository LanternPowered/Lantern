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
package org.lanternpowered.server.event.registry;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractGameRegistryRegisterEvent;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.CatalogRegistryModule;

public class LanternGameRegistryRegisterEvent<T extends CatalogType> extends AbstractGameRegistryRegisterEvent<T> {

    private final Cause cause;
    private final Class<T> catalogType;
    private final AdditionalCatalogRegistryModule<T> registryModule;

    public LanternGameRegistryRegisterEvent(Cause cause, Class<T> catalogType,
            AdditionalCatalogRegistryModule<T> registryModule) {
        this.cause = cause;
        this.catalogType = catalogType;
        this.registryModule = registryModule;
    }

    @Override
    public Class<T> getCatalogType() {
        return this.catalogType;
    }

    @Override
    public CatalogRegistryModule<T> getRegistryModule() {
        return this.registryModule;
    }

    @Override
    public void register(T catalogType) {
        this.registryModule.registerAdditionalCatalog(catalogType);
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    @Override
    public TypeToken<T> getGenericType() {
        return TypeToken.of(this.catalogType);
    }
}
