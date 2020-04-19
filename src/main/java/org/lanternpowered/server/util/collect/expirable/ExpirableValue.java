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

import org.checkerframework.checker.nullness.qual.Nullable;

public interface ExpirableValue<V> {

    /**
     * Gets the value that is attached to the backing value.
     *
     * @return the value
     */
    @Nullable
    V getValue();

    /**
     * Whether this entry is expired, the value will be removed the
     * next time if it's accessed when it returns true.
     *
     * @return is valid
     */
    boolean isExpired();
}
