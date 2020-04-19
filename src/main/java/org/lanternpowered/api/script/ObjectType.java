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
package org.lanternpowered.api.script;

import org.spongepowered.api.CatalogType;

public interface ObjectType<O> extends CatalogType {

    /**
     * Gets the object class.
     *
     * @return The object class
     */
    Class<? extends O> getType();
}
