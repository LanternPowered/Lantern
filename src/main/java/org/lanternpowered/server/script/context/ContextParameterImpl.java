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
package org.lanternpowered.server.script.context;

import org.lanternpowered.api.script.context.Parameter;
import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.spongepowered.api.CatalogKey;

public class ContextParameterImpl<V> extends DefaultCatalogType implements Parameter<V> {

    private final Class<V> valueType;

    public ContextParameterImpl(CatalogKey key, Class<V> valueType) {
        super(key);
        this.valueType = valueType;
    }

    @Override
    public Class<V> getValueType() {
        return this.valueType;
    }
}
