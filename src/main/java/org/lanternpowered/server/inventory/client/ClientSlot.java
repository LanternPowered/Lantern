/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.inventory.client;

import org.lanternpowered.server.inventory.slot.LanternSlot;
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
     * a {@link LanternSlot}.
     */
    interface Slot extends ClientSlot {

        /**
         * Gets the {@link LanternSlot}.
         *
         * @return The slot
         */
        LanternSlot getSlot();
    }

    /**
     * Nothing is bound, {@link #getItem()} will always return
     * a empty {@link ItemStack}.
     */
    interface Empty extends ClientSlot {

    }
}
