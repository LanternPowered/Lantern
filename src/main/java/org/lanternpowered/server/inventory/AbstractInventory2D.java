/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.inventory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.data.property.Property;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperties;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.math.vector.Vector2i;

import java.util.List;
import java.util.Optional;

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
    public Optional<ItemStack> poll(Vector2i pos) {
        return getSlot(pos).map(Inventory::poll);
    }

    @Override
    public Optional<ItemStack> poll(Vector2i pos, int limit) {
        return getSlot(pos).map(slot -> slot.poll(limit));
    }

    @Override
    public Optional<ItemStack> peek(Vector2i pos) {
        return getSlot(pos).map(Inventory::peek);
    }

    @Override
    public Optional<ItemStack> peek(Vector2i pos, int limit) {
        return getSlot(pos).map(slot -> slot.peek(limit));
    }

    @Override
    public InventoryTransactionResult set(Vector2i index, ItemStack stack) {
        return getSlot(index).map(slot -> slot.set(stack)).orElse(CachedInventoryTransactionResults.FAIL_NO_TRANSACTIONS);
    }

    @Override
    public Optional<Slot> getSlot(Vector2i pos) {
        checkNotNull(pos, "slotPos");
        return getSlot(pos.getX(), pos.getY());
    }

    @Override
    protected <V> Optional<V> tryGetProperty(Inventory child, Property<V> property) {
        if (property == InventoryProperties.SLOT_POSITION && child instanceof Slot) {
            final int index = getSlotIndex((Slot) child);
            if (index == -1) {
                return Optional.empty();
            }
            final int x = index % this.columns;
            final int y = index / this.columns;
            return Optional.of((V) new Vector2i(x, y));
        }
        return super.tryGetProperty(child, property);
    }

    @Override
    protected <V> Optional<V> tryGetProperty(Property<V> property) {
        if (property == InventoryProperties.DIMENSION) {
            return Optional.of((V) getDimensions());
        }
        return super.tryGetProperty(property);
    }
}
