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
package org.lanternpowered.server.script.function;

import org.lanternpowered.api.script.function.FunctionType;
import org.lanternpowered.server.script.AbstractObjectType;
import org.spongepowered.api.CatalogKey;

public class AbstractFunctionType<F> extends AbstractObjectType<F> implements FunctionType<F> {

    public AbstractFunctionType(CatalogKey key, Class<? extends F> type) {
        super(key, type);
    }
}
