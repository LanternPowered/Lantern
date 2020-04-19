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

import org.lanternpowered.api.script.function.value.IntValueProvider;
import org.lanternpowered.api.script.function.value.IntValueProviderType;
import org.lanternpowered.server.script.function.AbstractFunctionType;
import org.spongepowered.api.CatalogKey;

public class IntValueProviderTypeImpl extends AbstractFunctionType<IntValueProvider> implements IntValueProviderType {

    public IntValueProviderTypeImpl(CatalogKey key, Class<? extends IntValueProvider> type) {
        super(key, type);
    }
}
