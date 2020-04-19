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
package org.lanternpowered.server.inventory.client;

import org.lanternpowered.server.inventory.AbstractSlot;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Represents a bound slot on a {@link ClientContainer}.
 */
public interface ClientSlot {

    /**
     * Gets the {@link ItemStack} that is visible in
     * this {@link ClientSlot}.
     *
     * @return The item stack
     */
    ItemStack getItem();

    /**
     * Represents a {@link ClientSlot} that just represents
     * an icon. The slot cannot be modified through inventory
     * operations.
     */
    interface Button extends ClientSlot {

        /**
         * Sets the icon {@link ItemStack}.
         *
         * @param itemStack The item stack
         */
        void setItem(ItemStack itemStack);
    }

    /**
     * Represents a {@link ClientSlot} that is bound to
     * a {@link AbstractSlot}.
     */
    interface Slot extends ClientSlot {

        /**
         * Gets the {@link AbstractSlot}.
         *
         * @return The slot
         */
        AbstractSlot getSlot();
    }

    /**
     * Nothing is bound, {@link #getItem()} will always return
     * a empty {@link ItemStack}.
     */
    interface Empty extends ClientSlot {

    }
}
