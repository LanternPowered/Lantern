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
package org.lanternpowered.server.inventory.entity;

import org.lanternpowered.server.inventory.LanternGridInventory;
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.text.translation.Translation;

import javax.annotation.Nullable;

public class LanternHumanMainInventory extends LanternGridInventory implements MainPlayerInventory {

    protected LanternHotbar hotbar;
    protected LanternGridInventory grid;

    LanternHumanMainInventory(@Nullable Inventory parent) {
        super(parent);
    }

    @Override
    public LanternHotbar getHotbar() {
        return this.hotbar;
    }

    @Override
    public LanternGridInventory getGrid() {
        return this.grid;
    }

    @Override
    protected <T extends Slot> T registerSlotAt(int x, int y, T slot) {
        return super.registerSlotAt(x, y, slot);
    }
}
