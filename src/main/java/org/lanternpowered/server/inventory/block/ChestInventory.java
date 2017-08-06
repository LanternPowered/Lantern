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

import static org.lanternpowered.server.text.translation.TranslationHelper.tr;

import org.lanternpowered.server.inventory.IInventory;
import org.lanternpowered.server.inventory.LanternContainer;
import org.lanternpowered.server.inventory.LanternGridInventory;
import org.lanternpowered.server.inventory.VanillaOpenableInventory;
import org.lanternpowered.server.inventory.client.ChestClientContainer;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.inventory.client.CraftingTableClientContainer;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.translation.Translation;

import javax.annotation.Nullable;

public class ChestInventory extends LanternGridInventory implements VanillaOpenableInventory {

    public ChestInventory(@Nullable Inventory parent, int rows) {
        this(parent, null, rows);
    }

    public ChestInventory(@Nullable Inventory parent, @Nullable Translation name, int rows) {
        super(parent, name == null ? tr("container.chest") : name);

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < 9; x++) {
                registerSlotAt(x, y);
            }
        }
        finalizeContent();
    }

    @Override
    public IInventory getShiftClickTarget(LanternContainer container, Slot slot) {
        return isChild(slot) ? VanillaOpenableInventory.super.getShiftClickTarget(container, slot) : this;
    }

    @Override
    public boolean disableShiftClickWhenFull() {
        return false;
    }

    @Override
    public ClientContainer constructClientContainer() {
        return new ChestClientContainer(Text.of(getName()), 3);
    }
}
