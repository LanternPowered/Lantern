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
package org.lanternpowered.server.util.collect.expirable;

import java.util.Map;

public interface ExpirableValueMap<K, V, E extends ExpirableValue<V>> extends Map<K, V> {

    /**
     * Gets the map backing this map.
     *
     * @return the backing map
     */
    Map<K, E> getBacking();
}
