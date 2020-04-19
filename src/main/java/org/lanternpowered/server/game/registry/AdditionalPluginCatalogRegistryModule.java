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
package org.lanternpowered.server.game.registry;

import kotlin.reflect.KClass;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;

public class AdditionalPluginCatalogRegistryModule<T extends CatalogType> extends DefaultCatalogRegistryModule<T>
        implements AdditionalCatalogRegistryModule<T> {

    public AdditionalPluginCatalogRegistryModule() {
    }

    public AdditionalPluginCatalogRegistryModule(Class<?>... catalogClasses) {
        super(catalogClasses);
    }

    public AdditionalPluginCatalogRegistryModule(KClass<?>... catalogClasses) {
        super(catalogClasses);
    }

    @Override
    public void registerAdditionalCatalog(T extraCatalog) {
        register(extraCatalog, true);
    }
}
