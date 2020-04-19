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
package org.lanternpowered.server.util.palette;

public interface ImmutablePalette<T> extends Palette<T> {

    @Override
    default int getOrAssign(T object) {
        final int id = getId(object);
        if (id != INVALID_ID) {
            return id;
        }
        throw new IllegalStateException("Cannot assign object ids in a immutable palette.");
    }
}
