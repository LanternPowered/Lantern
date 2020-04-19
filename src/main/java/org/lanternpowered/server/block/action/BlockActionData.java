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
package org.lanternpowered.server.block.action;

public interface BlockActionData {

    /**
     * Sets the data value that should be used
     * for the specific index.
     *
     * @param index The index
     * @param data The data value
     */
    void set(int index, int data);
}
