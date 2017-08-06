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
package org.lanternpowered.server.inventory;

import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.inventory.client.ClientSlot;
import org.lanternpowered.server.inventory.entity.LanternPlayerInventory;
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.slot.OutputSlot;

import javax.annotation.Nullable;

/**
 * Represents a {@link IInventory} that can be opened by
 * a {@link Player}.
 */
public interface OpenableInventory extends IInventory {

    /**
     * Gets the target {@link Inventory} where the contents of the
     * {@link Slot} should be moved to. If {@code null} is returned
     * will the behavior default to moving items between the hotbar
     * and main inventory.
     * <p>
     * All {@link OutputSlot}s in the {@link IInventory} will also
     * be ignored, if no valid slot can be found in the
     * {@link IInventory} will the behavior also use the default.
     *
     * @param container The container in which the shift click occurred
     * @param slot The slot
     * @return The target inventory
     */
    @Nullable
    default IInventory getShiftClickTarget(LanternContainer container, Slot slot) {
        return null;
    }

    /**
     * Gets whether shift clicking from the player inventory
     * should be disabled when the top inventory is full.
     *
     * @return Disable shift click
     */
    default boolean disableShiftClickWhenFull() {
        return true;
    }

    /**
     * Constructs a {@link ClientContainer} for this {@link OpenableInventory}
     * and the {@link LanternContainer}. This is the place to assign
     * {@link LanternSlot}s to {@link ClientSlot}s.
     *
     * @return The constructed client container
     */
    ClientContainer constructClientContainer(LanternContainer container);
}
