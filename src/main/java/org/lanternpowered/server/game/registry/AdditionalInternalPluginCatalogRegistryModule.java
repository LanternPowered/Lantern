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

public class AdditionalInternalPluginCatalogRegistryModule<T extends CatalogType> extends InternalPluginCatalogRegistryModule<T>
        implements AdditionalCatalogRegistryModule<T> {

    public AdditionalInternalPluginCatalogRegistryModule() {
        super();
    }

    public AdditionalInternalPluginCatalogRegistryModule(Class<?>... catalogClasses) {
        super(catalogClasses);
    }

    public AdditionalInternalPluginCatalogRegistryModule(KClass<?>... catalogClasses) {
        super(catalogClasses);
    }

    @Override
    public void registerAdditionalCatalog(T extraCatalog) {
        register(extraCatalog, true);
    }
}
