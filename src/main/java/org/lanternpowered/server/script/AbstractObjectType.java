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
package org.lanternpowered.server.script;

import org.lanternpowered.api.script.ObjectType;
import org.lanternpowered.api.util.ToStringHelper;
import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.spongepowered.api.CatalogKey;

public abstract class AbstractObjectType<O> extends DefaultCatalogType implements ObjectType<O> {

    private final Class<? extends O> type;

    public AbstractObjectType(CatalogKey key, Class<? extends O> type) {
        super(key);
        this.type = type;
    }

    @Override
    public Class<? extends O> getType() {
        return this.type;
    }

    @Override
    public ToStringHelper toStringHelper() {
        return super.toStringHelper().add("type", this.type);
    }
}
