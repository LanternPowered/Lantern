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
package org.lanternpowered.server.inventory;

import java.util.function.Consumer;

@FunctionalInterface
public interface SlotChangeListener extends Consumer<ISlot> {

    /**
     * Is called when the content of a {@link ISlot} changed.
     *
     * @param slot The slot
     */
    @Override
    void accept(ISlot slot);
}
