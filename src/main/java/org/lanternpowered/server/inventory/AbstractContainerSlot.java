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

import static com.google.common.base.Preconditions.checkState;

import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractContainerSlot extends AbstractForwardingSlot {

    @Nullable AbstractInventorySlot slot;

    @Override
    protected AbstractInventorySlot getDelegateSlot() {
        checkState(this.slot != null, "The inventory slot is not initialized yet.");
        return this.slot;
    }

    @Override
    public AbstractInventorySlot viewedSlot() {
        return getDelegateSlot();
    }
}
