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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.flowpowered.math.vector.Vector2i;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

/**
 * A base inventory for inventories that have slots in the x-y plane
 * that are combined into a grid/row/column.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractInventory2D extends AbstractChildrenInventory implements IInventory2D {

    private int columns;
    private int rows;

    AbstractInventory2D() {
    }

    void initWithSlots(List<AbstractMutableInventory> children, List<? extends AbstractSlot> slots, int columns, int rows) {
        checkState(columns * rows == slots.size(), "Slots mismatch, %s (columns) * %s (rows) = %s (slots) and not %s (slots)",
                columns, rows, columns * rows, slots.size());

        this.columns = columns;
        this.rows = rows;

        super.initWithSlots(children, slots);
    }

    void initWithChildren(List<AbstractMutableInventory> children, int columns, int rows) {
        super.initWithChildren(children, false);
        if (columns == -1 && rows == -1) {
            rows = 1;
        }
        final int capacity = capacity();
        if (columns == -1) {
            checkState(capacity % rows == 0);
            columns = capacity / rows;
        } else if (rows == -1) {
            checkState(capacity % columns == 0);
            rows = capacity / columns;
        }
        checkState(capacity == columns * rows);
        this.columns = columns;
        this.rows = rows;
    }

    @Override
    void initWithSlots(List<AbstractMutableInventory> children, List<? extends AbstractSlot> slots) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    void initWithChildren(List<AbstractMutableInventory> children, boolean lazy) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public int getRows() {
        return this.rows;
    }

    @Override
    public int getColumns() {
        return this.columns;
    }

    @Override
    public Vector2i getDimensions() {
        return new Vector2i(getColumns(), getRows());
    }

    @Override
    public InventoryTransactionResult set(int x, int y, ItemStack stack) {
        return getSlot(x, y).map(slot -> slot.set(stack)).orElse(CachedInventoryTransactionResults.FAIL_NO_TRANSACTIONS);
    }

    @Override
    public Optional<ItemStack> peek(int x, int y, int limit) {
        return getSlot(x, y).map(slot -> slot.peek(limit));
    }

    @Override
    public Optional<ItemStack> peek(int x, int y) {
        return getSlot(x, y).map(Slot::peek);
    }

    @Override
    public Optional<ItemStack> poll(int x, int y, int limit) {
        return getSlot(x, y).map(slot -> slot.poll(limit));
    }

    @Override
    public Optional<ItemStack> poll(int x, int y) {
        return getSlot(x, y).map(Slot::poll);
    }

    @Override
    public Optional<Slot> getSlot(int x, int y) {
        if (x < 0 || y < 0) {
            return Optional.empty();
        }
        final int index = y * this.columns + x;
        return (Optional) getSlot(index);
    }

    @Override
    public Optional<ItemStack> poll(SlotPos pos) {
        return getSlot(pos).map(Inventory::poll);
    }

    @Override
    public Optional<ItemStack> poll(SlotPos pos, int limit) {
        return getSlot(pos).map(slot -> slot.poll(limit));
    }

    @Override
    public Optional<ItemStack> peek(SlotPos pos) {
        return getSlot(pos).map(Inventory::peek);
    }

    @Override
    public Optional<ItemStack> peek(SlotPos pos, int limit) {
        return getSlot(pos).map(slot -> slot.peek(limit));
    }

    @Override
    public InventoryTransactionResult set(SlotPos index, ItemStack stack) {
        return getSlot(index).map(slot -> slot.set(stack)).orElse(CachedInventoryTransactionResults.FAIL_NO_TRANSACTIONS);
    }

    @Override
    public Optional<Slot> getSlot(SlotPos pos) {
        checkNotNull(pos, "slotPos");
        if (!(pos.getOperator() == Property.Operator.EQUAL ||
                pos.getOperator() == Property.Operator.DELEGATE) || pos.getValue() == null) {
            return Optional.empty();
        }
        return getSlot(pos.getX(), pos.getY());
    }

    @Override
    protected <T extends InventoryProperty<?, ?>> Optional<T> tryGetProperty(Inventory child, Class<T> property, @Nullable Object key) {
        if (property == SlotPos.class && child instanceof Slot) {
            final int index = getSlotIndex((Slot) child);
            if (index == -1) {
                return Optional.empty();
            }
            final int x = index % this.columns;
            final int y = index / this.columns;
            return Optional.of((T) SlotPos.of(x, y));
        }
        return super.tryGetProperty(child, property, key);
    }

    @Override
    protected <T extends InventoryProperty<?, ?>> List<T> tryGetProperties(Inventory child, Class<T> property) {
        final List<T> properties = super.tryGetProperties(child, property);
        if (property == SlotPos.class && child instanceof Slot) {
            final int index = getSlotIndex((Slot) child);
            if (index != -1) {
                final int x = index % this.columns;
                final int y = index / this.columns;
                properties.add((T) SlotPos.of(x, y));
            }
        }
        return properties;
    }

    @Override
    protected <T extends InventoryProperty<?, ?>> Optional<T> tryGetProperty(Class<T> property, @Nullable Object key) {
        if (property == InventoryDimension.class) {
            return Optional.of((T) InventoryDimension.builder().value(getDimensions()).build());
        }
        return super.tryGetProperty(property, key);
    }

    @Override
    protected <T extends InventoryProperty<?, ?>> List<T> tryGetProperties(Class<T> property) {
        final List<T> list = super.tryGetProperties(property);
        if (property == InventoryDimension.class) {
            list.add((T) InventoryDimension.builder().value(getDimensions()));
        }
        return list;
    }
}
