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
package org.lanternpowered.server.script.function.value;

import org.lanternpowered.api.catalog.CatalogKeys;
import org.lanternpowered.api.script.function.value.DoubleValueProvider;
import org.lanternpowered.api.script.function.value.DoubleValueProviderType;
import org.lanternpowered.api.script.function.value.DoubleValueProviderTypes;
import org.lanternpowered.server.script.AbstractObjectTypeRegistryModule;

public class DoubleValueProviderTypeRegistryModule extends AbstractObjectTypeRegistryModule<DoubleValueProvider, DoubleValueProviderType> {

    private final static DoubleValueProviderTypeRegistryModule INSTANCE = new DoubleValueProviderTypeRegistryModule();

    public static DoubleValueProviderTypeRegistryModule get() {
        return INSTANCE;
    }

    private DoubleValueProviderTypeRegistryModule() {
        super(DoubleValueProviderTypes.class);
    }

    @Override
    public void registerDefaults() {
        this.register(new DoubleValueProviderTypeImpl(CatalogKeys.lantern("constant"), DoubleValueProvider.Constant.class));
        this.register(new DoubleValueProviderTypeImpl(CatalogKeys.lantern("range"), DoubleValueProvider.Range.class));
    }
}
