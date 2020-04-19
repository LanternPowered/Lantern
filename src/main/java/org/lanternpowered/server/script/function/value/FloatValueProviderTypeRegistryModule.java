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
import org.lanternpowered.api.script.function.value.FloatValueProvider;
import org.lanternpowered.api.script.function.value.FloatValueProviderType;
import org.lanternpowered.api.script.function.value.FloatValueProviderTypes;
import org.lanternpowered.server.script.AbstractObjectTypeRegistryModule;

public class FloatValueProviderTypeRegistryModule extends AbstractObjectTypeRegistryModule<FloatValueProvider, FloatValueProviderType> {

    private final static FloatValueProviderTypeRegistryModule INSTANCE = new FloatValueProviderTypeRegistryModule();

    public static FloatValueProviderTypeRegistryModule get() {
        return INSTANCE;
    }

    private FloatValueProviderTypeRegistryModule() {
        super(FloatValueProviderTypes.class);
    }

    @Override
    public void registerDefaults() {
        this.register(new FloatValueProviderTypeImpl(CatalogKeys.lantern("constant"), FloatValueProvider.Constant.class));
        this.register(new FloatValueProviderTypeImpl(CatalogKeys.lantern("range"), FloatValueProvider.Range.class));
    }
}
