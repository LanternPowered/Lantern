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
package org.lanternpowered.server.inventory.block;

import org.lanternpowered.server.inventory.LanternCraftingInventory;
import org.lanternpowered.server.inventory.LanternGridInventory;
import org.lanternpowered.server.inventory.slot.LanternCraftingInput;
import org.lanternpowered.server.inventory.slot.LanternCraftingOutput;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.text.translation.Translation;

import javax.annotation.Nullable;

public class WorkbenchInventory extends LanternCraftingInventory {

    public WorkbenchInventory(@Nullable Inventory parent, @Nullable Translation name) {
        super(parent, name);

        this.registerSlot(new LanternCraftingOutput(this));
        this.registerChild(new LanternGridInventory(this) {
            {
                for (int y = 0; y < 3; y++) {
                    for (int x = 0; x < 3; x++) {
                        this.registerSlotAt(x, y, new LanternCraftingInput(this));
                    }
                }
                this.finalizeContent();
            }
        });
    }
}
