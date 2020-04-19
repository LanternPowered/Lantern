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

import org.lanternpowered.api.script.function.value.DoubleValueProvider;
import org.lanternpowered.api.script.function.value.DoubleValueProviderType;
import org.lanternpowered.server.script.function.AbstractFunctionType;
import org.spongepowered.api.CatalogKey;

public class DoubleValueProviderTypeImpl extends AbstractFunctionType<DoubleValueProvider> implements DoubleValueProviderType {

    public DoubleValueProviderTypeImpl(CatalogKey key, Class<? extends DoubleValueProvider> type) {
        super(key, type);
    }
}
