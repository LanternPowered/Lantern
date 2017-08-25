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
package org.lanternpowered.server.inventory.behavior;

import org.lanternpowered.server.inventory.AbstractInventorySlot;
import org.lanternpowered.server.inventory.IInventory;
import org.lanternpowered.server.inventory.LanternContainer;
import org.lanternpowered.server.inventory.vanilla.LanternMainPlayerInventory;
import org.lanternpowered.server.inventory.vanilla.LanternPlayerInventory;
import org.spongepowered.api.item.inventory.slot.InputSlot;

public abstract class AbstractShiftClickBehavior implements ShiftClickBehavior {

    /**
     * Gets the default target {@link IInventory}, this target will never
     * be to the top inventory, only from the top to the bottom one or shifting
     * between the hotbar and the grid.
     *
     * @param container The container
     * @param slot The slot
     * @return The default target inventory
     */
    protected IInventory getDefaultTarget(LanternContainer container, AbstractInventorySlot slot) {
        final LanternMainPlayerInventory main = container.getPlayerInventory().getMain();
        // Check if the slot isn't in the main inventory
        if (!main.containsInventory(slot)) {
            if (slot instanceof InputSlot) {
                // The input slots use a different insertion order to the default
                return container.getPlayerInventory().getView(LanternPlayerInventory.View.PRIORITY_MAIN_AND_HOTBAR);
            }
            // Check click to the main inventory
            return container.getPlayerInventory().getView(LanternPlayerInventory.View.REVERSE_MAIN_AND_HOTBAR);
        // Shift click from the hotbar to the main inventory
        } else if (main.getHotbar().containsInventory(slot)) {
            return main.getGrid();
        } else {
            return main.getHotbar();
        }
    }
}
