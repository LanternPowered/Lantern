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

import org.spongepowered.api.CatalogType;

import java.util.Locale;

@FunctionalInterface
public interface CatalogTypeConstructor<T extends CatalogType> {

    default T createWithName(String pluginId, String name) {
        return create(pluginId, name.toLowerCase(Locale.ENGLISH), name);
    }

    default T create(String pluginId, String id) {
        return create(pluginId, id, id);
    }

    T create(String pluginId, String id, String name);
}
