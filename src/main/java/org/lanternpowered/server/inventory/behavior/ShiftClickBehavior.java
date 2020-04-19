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
package org.lanternpowered.server.inventory.behavior;

import org.lanternpowered.server.inventory.AbstractContainer;
import org.lanternpowered.server.inventory.AbstractInventorySlot;
import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.IInventory;

import org.checkerframework.checker.nullness.qual.Nullable;

@FunctionalInterface
public interface ShiftClickBehavior {

    /**
     * Gets the {@link IInventory}s that should be used when shift
     * clicking from the specified {@link AbstractInventorySlot}.
     * {@code null} can be returned to disable shift clicking.
     *
     * @param container The container
     * @param slot The slot
     * @return The target inventory, or null if none
     */
    @Nullable
    IInventory getTarget(AbstractContainer container, AbstractSlot slot);
}
