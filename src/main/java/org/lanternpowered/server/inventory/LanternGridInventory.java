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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector2i;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.item.inventory.type.InventoryColumn;
import org.spongepowered.api.item.inventory.type.InventoryRow;
import org.spongepowered.api.text.translation.Translation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

public class LanternGridInventory extends LanternInventory2D implements GridInventory {

    private final List<LanternInventoryRow> rows = new ArrayList<>();
    private final List<LanternInventoryColumn> columns = new ArrayList<>();

    public LanternGridInventory(@Nullable Inventory parent, @Nullable Translation name) {
        super(parent, name);
    }

    public LanternGridInventory(@Nullable Inventory parent) {
        super(parent, null);
    }

    @Override
    protected void finalizeContent() {
        super.finalizeContent();
        int columns = this.columns.size();
        int rows = this.rows.size();
        // Fill up the missing rows
        for (int i = 0; i < this.rows.size(); i++) {
            final LanternInventoryRow row = this.rows.get(i);
            if (row == null) {
                this.rows.set(i, new LanternInventoryRow(this, null));
            } else if (row.slots.size() > columns) {
                columns = row.slots.size();
            }
        }
        // Fill up the missing columns
        for (int i = 0; i < this.columns.size(); i++) {
            final LanternInventoryColumn column = this.columns.get(i);
            if (column == null) {
                this.columns.set(i, new LanternInventoryColumn(this, null));
            } else if (column.slots.size() > rows) {
                rows = column.slots.size();
            }
        }
        for (int y = 0; y < rows; y++) {
            final LanternInventoryRow row = this.rows.get(y);
            for (int x = 0; x < columns; x++) {
                final LanternInventoryColumn column = this.columns.get(x);

                // Search for the slot in this grid
                LanternSlot slot = this.slotsByPos.get(new Vector2i(x, y));
                // Search in it's row
                LanternSlot slotInRow = row.slotsByPos.get(new Vector2i(x, 0));
                // Search in it's column
                LanternSlot slotInColumn = column.slotsByPos.get(new Vector2i(0, y));
                // Log duplicates or when there none present
                if (slot == null && slotInRow == null && slotInColumn == null) {
                    Lantern.getLogger().warn("There is a slot missing in the inventory {} ({}) at position ({};{})",
                            getClass().getName(), getName().get(), x, y);
                } else if (slot == null && slotInRow != null && slotInColumn != null) {
                    Lantern.getLogger().warn("Duplicate slot found in the inventory {} ({}) at position ({};{}),"
                                    + "one found in the row and one in the column",
                            getClass().getName(), getName().get(), x, y);
                } else if (slot != null && slotInRow != null) {
                    Lantern.getLogger().warn("Duplicate slot found in the inventory {} ({}) at position ({};{}),"
                                    + "one found in the grid and one in the row",
                            getClass().getName(), getName().get(), x, y);
                } else if (slot != null && slotInColumn != null) {
                    Lantern.getLogger().warn("Duplicate slot found in the inventory {} ({}) at position ({};{}),"
                                    + "one found in the row and one in the column",
                            getClass().getName(), getName().get(), x, y);
                }

                if (slot == null) {
                    slot = slotInRow;
                    if (slot == null) {
                        slot = slotInColumn;
                        if (slot != null) {
                            row.registerSlotAt(x, slot);
                        }
                    } else {
                        column.registerSlotAt(y, slot);
                    }
                } else {
                    column.registerSlotAt(y, slot);
                    row.registerSlotAt(x, slot);
                }
            }
        }
    }

    /**
     * Registers a regular {@link Slot} for this inventory.
     *
     * @param x The x position of the slot in the grid
     * @param y The y position of the slot in the grid
     * @return The slot for chaining
     */
    protected LanternSlot registerSlotAt(int x, int y) {
        return this.registerSlotAt(x, y, new LanternSlot(this));
    }

    /**
     * Registers the {@link Slot} for this inventory.
     *
     * @param x The x position of the slot in the grid
     * @param y The y position of the slot in the grid
     * @param slot The slot to register
     * @return The slot for chaining
     */
    protected <T extends Slot> T registerSlotAt(int x, int y, T slot) {
        return this.registerSlotAt(this.nextFreeSlotIndex(), x, y, slot);
    }

    /**
     * Registers the {@link Slot} for this inventory.
     *
     * @param index The index of the slot
     * @param x The x position of the slot in the grid
     * @param y The y position of the slot in the grid
     * @param slot The slot to register
     * @return The slot for chaining
     */
    <T extends Slot> T registerSlotAt(int index, int x, int y, T slot) {
        checkNotNull(slot, "slot");
        checkArgument(x >= 0, "x position may not be negative");
        checkArgument(y >= 0, "y position may not be negative");
        checkArgument(this.slots.size() <= index || this.slots.get(index) == null, "The slot index %s is already in use", index);
        checkArgument(!this.indexBySlot.containsKey(slot), "The slot is already registered");
        Vector2i pos = new Vector2i(x, y);
        checkArgument(!this.slotsByPos.containsKey(pos), "The slot position (%s;%s) is already in use", x, y);
        this.registerSlot(index, slot, true);
        this.slotsByPos.put(pos, (LanternSlot) slot);
        while (this.columns.size() <= x) {
            this.columns.add(null);
        }
        while (this.rows.size() <= y) {
            this.rows.add(null);
        }
        return slot;
    }

    /**
     * Registers a {@link InventoryRow} for the specified row index, the
     * {@link Slot}s will be automatically registered in the {@link InventoryRow}
     * after initialization.
     *
     * @param y The row index (y position)
     * @param inventoryRow The inventory row
     * @return The inventory row for chaining
     */
    protected <T extends InventoryRow> T registerRow(int y, T inventoryRow) {
        checkNotNull(inventoryRow, "inventoryRow");
        checkArgument(y >= 0, "y position may not be negative");
        checkArgument(this.rows.size() <= y || this.rows.get(y) == null, "The row index %s is already in use", y);
        while (this.rows.size() <= y) {
            this.rows.add(null);
        }
        this.rows.set(y, (LanternInventoryRow) inventoryRow);
        ((LanternOrderedInventory) inventoryRow).slots.forEach(slot -> this.registerSlot(slot, true));
        return inventoryRow;
    }

    /**
     * Registers a {@link InventoryColumn} for the specified row index, the
     * {@link Slot}s will be automatically registered in the {@link InventoryColumn}
     * after initialization.
     *
     * @param x The column index (x position)
     * @param inventoryColumn The inventory column
     * @return The inventory row for chaining
     */
    protected <T extends InventoryColumn> T registerColumn(int x, T inventoryColumn) {
        checkNotNull(inventoryColumn, "inventoryColumn");
        checkArgument(x >= 0, "x position may not be negative");
        checkArgument(this.columns.size() <= x || this.columns.get(x) == null, "The column index %s is already in use", x);
        while (this.columns.size() <= x) {
            this.columns.add(null);
        }
        this.columns.set(x, (LanternInventoryColumn) inventoryColumn);
        ((LanternOrderedInventory) inventoryColumn).slots.forEach(slot -> this.registerSlot(slot, true));
        return inventoryColumn;
    }

    @Override
    protected <T extends Inventory> T prioritizeChild(T childInventory) {
        checkNotNull(childInventory, "inventory");
        if (this.rows.contains(childInventory) || this.columns.contains(childInventory)) {
            final List<Inventory> children = new ArrayList<>(((AbstractChildrenInventory) childInventory).getChildren());
            // Prioritize in backwards order
            Collections.reverse(children);
            children.forEach(this::prioritizeChild);
            return childInventory;
        }
        return super.prioritizeChild(childInventory);
    }

    @Override
    List<Inventory> queryInventories(Predicate<Inventory> matcher, boolean nested) {
        final List<Inventory> inventories = super.queryInventories(matcher, nested);
        // Ignore if there weren't any matches found or if when everything matched
        if (nested && !inventories.isEmpty() && inventories.get(0) == this) {
            return inventories;
        }
        // TODO: What should happen if there are matching columns and rows?
        //       Lets for now give the rows priority and abort afterwards
        boolean rowFound = false;
        for (LanternInventoryRow row : this.rows) {
            // If all the slots were found, a full row is present
            if (inventories.containsAll(row.slots)) {
                inventories.removeAll(row.slots);
                inventories.add(row);
                rowFound = true;
            } else if (matcher.test(row)) {
                inventories.add(row);
            }
        }
        if (rowFound) {
            return inventories;
        }
        for (LanternInventoryColumn column : this.columns) {
            // If all the slots were found, a full row is present
            if (inventories.containsAll(column.slots)) {
                inventories.removeAll(column.slots);
                inventories.add(column);
            } else if (matcher.test(column)) {
                inventories.add(column);
            }
        }
        return inventories;
    }

    @Override
    public int getColumns() {
        return this.columns.size();
    }

    @Override
    public int getRows() {
        return this.rows.size();
    }

    @Override
    public Vector2i getDimensions() {
        return new Vector2i(getRows(), getColumns());
    }

    @Override
    public Optional<ItemStack> poll(int x, int y) {
        final LanternSlot slot = this.slotsByPos.get(new Vector2i(x, y));
        return slot == null ? Optional.empty() : slot.poll();
    }

    @Override
    public Optional<ItemStack> poll(int x, int y, int limit) {
        final LanternSlot slot = this.slotsByPos.get(new Vector2i(x, y));
        return slot == null ? Optional.empty() : slot.poll(limit);
    }

    @Override
    public Optional<ItemStack> peek(int x, int y) {
        final LanternSlot slot = this.slotsByPos.get(new Vector2i(x, y));
        return slot == null ? Optional.empty() : slot.peek();
    }

    @Override
    public Optional<ItemStack> peek(int x, int y, int limit) {
        final LanternSlot slot = this.slotsByPos.get(new Vector2i(x, y));
        return slot == null ? Optional.empty() : slot.peek(limit);
    }

    @Override
    public InventoryTransactionResult set(int x, int y, ItemStack stack) {
        final LanternSlot slot = this.slotsByPos.get(new Vector2i(x, y));
        return slot == null ? InventoryTransactionResults.FAILURE : slot.set(stack);
    }

    @Override
    public Optional<Slot> getSlot(int x, int y) {
        return Optional.ofNullable(this.slotsByPos.get(new Vector2i(x, y)));
    }

    @Override
    public Optional<InventoryRow> getRow(int y) {
        return Optional.ofNullable(this.rows.get(y));
    }

    @Override
    public Optional<InventoryColumn> getColumn(int x) {
        return Optional.ofNullable(this.columns.get(x));
    }

    @Override
    protected <T extends InventoryProperty<?, ?>> Optional<T> tryGetProperty(Class<T> property, @Nullable Object key) {
        if (property == InventoryDimension.class) {
            //noinspection unchecked
            return Optional.of((T) new InventoryDimension(this.rows.size(), this.columns.size()));
        }
        return super.tryGetProperty(property, key);
    }

    @Override
    protected <T extends InventoryProperty<?, ?>> List<T> tryGetProperties(Class<T> property) {
        final List<T> properties = super.tryGetProperties(property);
        if (property == InventoryDimension.class) {
            //noinspection unchecked
            properties.add((T) new InventoryDimension(this.rows.size(), this.columns.size()));
        }
        return properties;
    }
}
