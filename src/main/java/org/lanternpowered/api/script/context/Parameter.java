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
package org.lanternpowered.api.script.context;

import org.spongepowered.api.CatalogType;

public interface Parameter<V> extends CatalogType {

    /**
     * Gets the type of the value that will be stored
     * by this {@link Parameter}.
     *
     * @return The value type
     */
    Class<V> getValueType();
}
