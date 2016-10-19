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

import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.type.Inventory2D;
import org.spongepowered.api.item.inventory.type.OrderedInventory;

import java.util.Optional;

import javax.annotation.Nullable;

public class AltParentProxyInventory2D extends AltParentProxyOrderedInventory implements Inventory2D {

    protected AltParentProxyInventory2D(@Nullable Inventory parent, OrderedInventory delegate) {
        super(parent, delegate);
    }

    @Override
    public Optional<ItemStack> poll(SlotPos pos) {
        return ((Inventory2D) this.delegate).poll(pos);
    }

    @Override
    public Optional<ItemStack> poll(SlotPos pos, int limit) {
        return ((Inventory2D) this.delegate).poll(pos, limit);
    }

    @Override
    public Optional<ItemStack> peek(SlotPos pos) {
        return ((Inventory2D) this.delegate).peek(pos);
    }

    @Override
    public Optional<ItemStack> peek(SlotPos pos, int limit) {
        return ((Inventory2D) this.delegate).peek(pos, limit);
    }

    @Override
    public InventoryTransactionResult set(SlotPos pos, ItemStack stack) {
        return ((Inventory2D) this.delegate).set(pos, stack);
    }

    @Override
    public Optional<Slot> getSlot(SlotPos pos) {
        return ((Inventory2D) this.delegate).getSlot(pos);
    }
}
