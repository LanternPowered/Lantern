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

import com.google.common.base.MoreObjects;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("unchecked")
public abstract class AbstractCarrier<T extends CarriedInventory<?>> implements Carrier {

    @Nullable private T inventory;

    void setInventory(T inventory) {
        this.inventory = inventory;
    }

    @Override
    public T getInventory() {
        checkState(this.inventory != null, "The inventory is not initialized yet");
        return this.inventory;
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    protected MoreObjects.ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this);
    }
}
