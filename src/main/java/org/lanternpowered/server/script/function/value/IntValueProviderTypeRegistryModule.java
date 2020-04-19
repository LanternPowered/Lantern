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
import org.lanternpowered.api.script.function.value.IntValueProvider;
import org.lanternpowered.api.script.function.value.IntValueProviderType;
import org.lanternpowered.api.script.function.value.IntValueProviderTypes;
import org.lanternpowered.server.script.AbstractObjectTypeRegistryModule;

public class IntValueProviderTypeRegistryModule extends AbstractObjectTypeRegistryModule<IntValueProvider, IntValueProviderType> {

    private final static IntValueProviderTypeRegistryModule INSTANCE = new IntValueProviderTypeRegistryModule();

    public static IntValueProviderTypeRegistryModule get() {
        return INSTANCE;
    }

    private IntValueProviderTypeRegistryModule() {
        super(IntValueProviderTypes.class);
    }

    @Override
    public void registerDefaults() {
        this.register(new IntValueProviderTypeImpl(CatalogKeys.lantern("constant"), IntValueProvider.Constant.class));
        this.register(new IntValueProviderTypeImpl(CatalogKeys.lantern("range"), IntValueProvider.Range.class));
    }
}
