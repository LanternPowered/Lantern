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

import org.lanternpowered.server.inventory.behavior.AbstractShiftClickBehavior;
import org.lanternpowered.server.inventory.behavior.ShiftClickBehavior;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A {@link ShiftClickBehavior} that is only applicable to a {@link PlayerTopBottomContainer}.
 */
public abstract class AbstractTopBottomShiftClickBehavior extends AbstractShiftClickBehavior {

    @Override
    public IInventory getTarget(AbstractContainer container, AbstractSlot slot) {
        if (container instanceof PlayerTopBottomContainer) {
            return getTarget((PlayerTopBottomContainer) container, slot);
        }
        // Use default
        return getDefaultTarget(container, slot);
    }

    @Nullable
    public abstract IInventory getTarget(PlayerTopBottomContainer container, AbstractSlot slot);
}
