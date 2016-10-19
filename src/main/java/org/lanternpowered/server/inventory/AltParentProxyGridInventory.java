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

import com.flowpowered.math.vector.Vector2i;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.item.inventory.type.InventoryColumn;
import org.spongepowered.api.item.inventory.type.InventoryRow;
import org.spongepowered.api.item.inventory.type.OrderedInventory;

import java.util.Optional;

import javax.annotation.Nullable;

public class AltParentProxyGridInventory extends AltParentProxyInventory2D implements GridInventory {

    protected AltParentProxyGridInventory(@Nullable Inventory parent, OrderedInventory delegate) {
        super(parent, delegate);
    }

    @Override
    public int getColumns() {
        return ((GridInventory) this.delegate).getColumns();
    }

    @Override
    public int getRows() {
        return ((GridInventory) this.delegate).getRows();
    }

    @Override
    public Vector2i getDimensions() {
        return ((GridInventory) this.delegate).getDimensions();
    }

    @Override
    public Optional<ItemStack> poll(int x, int y) {
        return ((GridInventory) this.delegate).poll(x, y);
    }

    @Override
    public Optional<ItemStack> poll(int x, int y, int limit) {
        return ((GridInventory) this.delegate).poll(x, y, limit);
    }

    @Override
    public Optional<ItemStack> peek(int x, int y) {
        return ((GridInventory) this.delegate).peek(x, y);
    }

    @Override
    public Optional<ItemStack> peek(int x, int y, int limit) {
        return ((GridInventory) this.delegate).peek(x, y, limit);
    }

    @Override
    public InventoryTransactionResult set(int x, int y, ItemStack stack) {
        return ((GridInventory) this.delegate).set(x, y, stack);
    }

    @Override
    public Optional<Slot> getSlot(int x, int y) {
        return ((GridInventory) this.delegate).getSlot(x, y);
    }

    @Override
    public Optional<InventoryRow> getRow(int y) {
        return ((GridInventory) this.delegate).getRow(y);
    }

    @Override
    public Optional<InventoryColumn> getColumn(int x) {
        return ((GridInventory) this.delegate).getColumn(x);
    }
}
