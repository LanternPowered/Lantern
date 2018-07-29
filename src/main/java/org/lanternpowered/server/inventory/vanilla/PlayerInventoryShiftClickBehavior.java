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
package org.lanternpowered.server.inventory.vanilla;

import org.lanternpowered.server.inventory.AbstractInventorySlot;
import org.lanternpowered.server.inventory.IInventory;
import org.lanternpowered.server.inventory.LanternContainer;
import org.lanternpowered.server.inventory.behavior.AbstractShiftClickBehavior;
import org.lanternpowered.server.inventory.transformation.InventoryTransforms;
import org.spongepowered.api.item.inventory.slot.OutputSlot;

public class PlayerInventoryShiftClickBehavior extends AbstractShiftClickBehavior {

    public static final PlayerInventoryShiftClickBehavior INSTANCE = new PlayerInventoryShiftClickBehavior();

    @Override
    public IInventory getTarget(LanternContainer container, AbstractInventorySlot slot) {
        final LanternMainPlayerInventory main = container.getPlayerInventory().getMain();
        // Check if the slot isn't in the main inventory
        if (!main.containsInventory(slot)) {
            if (slot instanceof OutputSlot) {
                return main.transform(InventoryTransforms.REVERSE);
            }
            return main;
        // Shift click from the hotbar to the main inventory
        } else {
            IInventory target;
            if (main.getHotbar().containsInventory(slot)) {
                target = main.getGrid();
            } else {
                target = main.getHotbar();
            }
            return container.getPlayerInventory().getArmor().union(target);
        }
    }
}
