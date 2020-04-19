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

public class SimpleExpirableValue<V> implements ExpirableValue<V> {

    @Nullable
    private final V value;

    public SimpleExpirableValue(V value) {
        this.value = value;
    }

    @Nullable
    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public boolean isExpired() {
        return false;
    }
}
