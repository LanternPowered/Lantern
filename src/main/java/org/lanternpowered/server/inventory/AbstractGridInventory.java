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

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.inventory.constructor.InventoryConstructor;
import org.lanternpowered.server.inventory.constructor.InventoryConstructorFactory;
import org.lanternpowered.server.inventory.type.LanternInventoryColumn;
import org.lanternpowered.server.inventory.type.LanternInventoryRow;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.item.inventory.type.InventoryColumn;
import org.spongepowered.api.item.inventory.type.InventoryRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nullable;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public abstract class AbstractGridInventory extends AbstractInventory2D implements GridInventory {

    /**
     * Constructs a new {@link SlotsBuilder} to create {@link AbstractGridInventory}s. This builder
     * only accepts {@link LanternInventoryArchetype}s that construct {@link AbstractSlot}s.
     *
     * @return The builder
     */
    public static SlotsBuilder<AbstractGridInventory> slotsBuilder() {
        return new SlotsBuilder<>();
    }

    /**
     * Constructs a new {@link RowsBuilder} to create {@link AbstractGridInventory}s. This builder
     * only accepts {@link LanternInventoryArchetype}s that construct {@link AbstractInventoryRow}s
     * or {@link AbstractGridInventory}s.
     * <p>
     * The first specified row/grid will define the width/columns of all the rows, any mismatching
     * value after specifying the first inventory will result in a exception.
     *
     * @return The builder
     */
    public static RowsBuilder<AbstractGridInventory> rowsBuilder() {
        return new RowsBuilder<>();
    }

    /**
     * Constructs a new {@link RowsViewBuilder} to create {@link AbstractGridInventory}s. This builder
     * only accepts {@link LanternInventoryArchetype}s that construct {@link AbstractInventoryRow}s
     * or {@link AbstractGridInventory}s.
     * <p>
     * The first specified row/grid will define the width/columns of all the rows, any mismatching
     * value after specifying the first inventory will result in a exception.
     *
     * @return The builder
     */
    public static RowsViewBuilder<AbstractGridInventory> rowsViewBuilder() {
        return new RowsViewBuilder<>();
    }

    /**
     * Constructs a new {@link ColumnsBuilder} to create {@link AbstractGridInventory}s. This builder
     * only accepts {@link LanternInventoryArchetype}s that construct {@link AbstractInventoryColumn}s
     * or {@link AbstractGridInventory}s.
     * <p>
     * The first specified column/grid will define the height/rows of all the columns, any mismatching
     * value after specifying the first inventory will result in a exception.
     *
     * @return The builder
     */
    public static ColumnsBuilder<AbstractGridInventory> columnsBuilder() {
        return new ColumnsBuilder<>();
    }

    @Nullable private List<AbstractInventoryRow> rows;
    @Nullable private List<AbstractInventoryColumn> columns;

    @Override
    void initWithSlots(List<AbstractMutableInventory> children, List<? extends AbstractSlot> slots, int columns, int rows,
            @Nullable List<? extends AbstractSlot> prioritizedSlots) {
        throw new UnsupportedOperationException();
    }

    void init(List<AbstractMutableInventory> children, List<? extends AbstractSlot> slots, List<AbstractInventoryRow> rows,
            List<AbstractInventoryColumn> columns, @Nullable List<? extends AbstractSlot> prioritizedSlots) {
        this.columns = columns;
        this.rows = rows;

        super.initWithSlots(children, slots, columns.size(), rows.size(), prioritizedSlots);
    }

    @Override
    void queryInventories(Set<AbstractMutableInventory> inventories, Predicate<AbstractMutableInventory> predicate) {
        super.queryInventories(inventories, predicate);
        if (this.rows == null || this.columns == null) {
            return;
        }
        // Match against the rows and columns, no children of these rows or
        // columns since they are already matched
        this.rows.stream().filter(predicate::test).forEach(inventories::add);
        this.columns.stream().filter(predicate::test).forEach(inventories::add);
        // Check the slots their parents, in case they are nested into another grid
        for (AbstractSlot slot : getSlotInventories()) {
            final AbstractInventory parent = slot.parent();
            if ((parent != this || parent != slot) && parent instanceof AbstractMutableInventory &&
                    predicate.test((AbstractMutableInventory) parent)) {
                inventories.add((AbstractMutableInventory) parent);
            }
        }
    }

    @Override
    public Optional<InventoryRow> getRow(int y) {
        return y < 0 || this.rows == null || y >= this.rows.size() ? Optional.empty() : Optional.of(this.rows.get(y));
    }

    @Override
    public Optional<InventoryColumn> getColumn(int x) {
        return x < 0 || this.columns == null || x >= this.columns.size() ? Optional.empty() : Optional.of(this.columns.get(x));
    }

    public static abstract class ViewBuilder<T extends AbstractGridInventory, B extends ViewBuilder<T, B>>
            extends AbstractViewBuilder<T, AbstractGridInventory, B> {

        InventoryConstructor<? extends AbstractInventoryColumn>[] columnTypes = new InventoryConstructor[0];
        InventoryConstructor<? extends AbstractInventoryRow>[] rowTypes = new InventoryConstructor[0];

        int rows = 0;
        int columns = 0;

        ViewBuilder() {
        }

        /**
         * Sets the {@link AbstractInventoryColumn} type for the given
         * column index (x coordinate).
         *
         * @param x The column index (x coordinate)
         * @param columnType The column type
         * @return This builder, for chaining
         */
        public B columnType(int x, Class<? extends AbstractInventoryColumn> columnType) {
            expand(x, this.rows);
            this.columnTypes[x] = InventoryConstructorFactory.get().getConstructor(columnType);
            return (B) this;
        }

        /**
         * Sets the {@link AbstractInventoryRow} type for the given
         * row index (y coordinate).
         *
         * @param y The row index (y coordinate)
         * @param rowType The row type
         * @return This builder, for chaining
         */
        public B rowType(int y, Class<? extends AbstractInventoryRow> rowType) {
            expand(this.columns, y);
            this.rowTypes[y] = InventoryConstructorFactory.get().getConstructor(rowType);
            return (B) this;
        }

        /**
         * Expands the slots matrix to the given maximum.
         *
         * @param columns The columns
         * @param rows The rows
         */
        public B expand(int columns, int rows) {
            // Expand the amount of rows
            if (rows > this.rows) {
                final InventoryConstructor<? extends AbstractInventoryRow>[] rowTypes = new InventoryConstructor[rows];
                System.arraycopy(this.rowTypes, 0, rowTypes, 0, this.rowTypes.length);
                this.rowTypes = rowTypes;
                this.rows = rows;
            }
            // Expand the amount of columns
            if (columns > this.columns) {
                final InventoryConstructor<? extends AbstractInventoryColumn>[] columnTypes = new InventoryConstructor[columns];
                System.arraycopy(this.columnTypes, 0, columnTypes, 0, this.columnTypes.length);
                this.columnTypes = columnTypes;
                this.columns = columns;
            }
            return (B) this;
        }
    }


    public static abstract class Builder<T extends AbstractGridInventory, B extends Builder<T, B>>
            extends AbstractArchetypeBuilder<T, AbstractGridInventory, B> {

        InventoryConstructor<? extends AbstractInventoryColumn>[] columnTypes = new InventoryConstructor[0];
        InventoryConstructor<? extends AbstractInventoryRow>[] rowTypes = new InventoryConstructor[0];

        int rows = 0;
        int columns = 0;

        Builder() {
        }

        /**
         * Sets the {@link AbstractInventoryColumn} type for the given
         * column index (x coordinate).
         *
         * @param x The column index (x coordinate)
         * @param columnType The column type
         * @return This builder, for chaining
         */
        public B columnType(int x, Class<? extends AbstractInventoryColumn> columnType) {
            expand(x + 1, this.rows);
            this.columnTypes[x] = InventoryConstructorFactory.get().getConstructor(columnType);
            return (B) this;
        }

        /**
         * Sets the {@link AbstractInventoryRow} type for the given
         * row index (y coordinate).
         *
         * @param y The row index (y coordinate)
         * @param rowType The row type
         * @return This builder, for chaining
         */
        public B rowType(int y, Class<? extends AbstractInventoryRow> rowType) {
            expand(this.columns, y + 1);
            this.rowTypes[y] = InventoryConstructorFactory.get().getConstructor(rowType);
            return (B) this;
        }

        /**
         * Expands the slots matrix to the given maximum.
         *
         * @param columns The columns
         * @param rows The rows
         */
        public B expand(int columns, int rows) {
            // Expand the amount of rows
            if (rows > this.rows) {
                final InventoryConstructor<? extends AbstractInventoryRow>[] rowTypes = new InventoryConstructor[rows];
                System.arraycopy(this.rowTypes, 0, rowTypes, 0, this.rowTypes.length);
                this.rowTypes = rowTypes;
                this.rows = rows;
            }
            // Expand the amount of columns
            if (columns > this.columns) {
                final InventoryConstructor<? extends AbstractInventoryColumn>[] columnTypes = new InventoryConstructor[columns];
                System.arraycopy(this.columnTypes, 0, columnTypes, 0, this.columnTypes.length);
                this.columnTypes = columnTypes;
                this.columns = columns;
            }
            this.slots = columns * rows;
            return (B) this;
        }

        @Override
        protected void copyTo(B builder) {
            super.copyTo(builder);
            builder.columns = this.columns;
            builder.columnTypes = Arrays.copyOf(this.columnTypes, this.columnTypes.length);
            builder.rows = this.rows;
            builder.rowTypes = Arrays.copyOf(this.rowTypes, this.rowTypes.length);
        }
    }

    public static final class SlotsBuilder<T extends AbstractGridInventory> extends Builder<T, SlotsBuilder<T>> {

        private PrioritizedObject<LanternInventoryArchetype<? extends AbstractSlot>>[][] slots = new PrioritizedObject[0][0];
        @Nullable private List<InventoryArchetype> cachedArchetypesList;

        SlotsBuilder() {
        }

        @Override
        public SlotsBuilder<T> expand(int columns, int rows) {
            // Expand the amount of rows
            if (rows > this.rows) {
                this.slots = Arrays.copyOf(this.slots, rows);
                final int columns1 = Math.max(columns, this.columns);
                for (int i = this.rows; i < rows; i++) {
                    this.slots[i] = new PrioritizedObject[columns1];
                }
            }
            // Expand the amount of columns
            if (columns > this.columns) {
                for (int i = 0; i < this.rows; i++) {
                    this.slots[i] = Arrays.copyOf(this.slots[i], columns);
                }
            }
            return super.expand(columns, rows);
        }

        @Override
        public <N extends AbstractGridInventory> SlotsBuilder<N> type(Class<N> inventoryType) {
            return (SlotsBuilder<N>) super.type(inventoryType);
        }

        SlotsBuilder<T> slot(LanternInventoryArchetype<? extends AbstractSlot> slotArchetype) {
            for (int y = 0; y < this.rows; y++) {
                for (int x = 0; x < this.columns; x++) {
                    if (this.slots[y][x] == null) {
                        return slot(x, y, slotArchetype);
                    }
                }
            }
            throw new IllegalStateException("No free slot index could be found.");
        }

        /**
         * Adds the provided slot {@link LanternInventoryArchetype} to the x and y coordinates.
         *
         * @param x The x coordinate
         * @param y The y coordinate
         * @param slotArchetype The slot archetype
         * @return This builder, for chaining
         */
        public SlotsBuilder<T> slot(int x, int y, LanternInventoryArchetype<? extends AbstractSlot> slotArchetype) {
            return slot(x, y, slotArchetype, DEFAULT_PRIORITY);
        }

        /**
         * Adds the provided slot {@link LanternInventoryArchetype} to the x and y coordinates
         * and the specified priority. All the {@link AbstractSlot} indexes will be generated
         * on the insertion order. The priority will only affect the iteration order, this
         * will affect {@link IInventory#offer(ItemStack)}, ... operations.
         *
         * @param x The x coordinate
         * @param y The y coordinate
         * @param slotArchetype The slot archetype
         * @param priority The priority
         * @return This builder, for chaining
         */
        public SlotsBuilder<T> slot(int x, int y, LanternInventoryArchetype<? extends AbstractSlot> slotArchetype, int priority) {
            checkNotNull(slotArchetype, "slotArchetype");
            expand(x + 1, y + 1);
            checkState(this.slots[y][x] == null, "There is already a slot bound at %s;%s", x, y);
            this.slots[y][x] = new PrioritizedObject<>(slotArchetype, priority);
            this.cachedArchetypesList = null;
            return this;
        }

        @Override
        protected void build(T inventory) {
            // Collect all the slots and validate if there are any missing ones.
            final List<PrioritizedObject<AbstractSlot>> prioritizedSlotObjects = new ArrayList<>();
            final List<PrioritizedObject<AbstractSlot>>[] columnSlots = new List[this.columns];
            final List<AbstractInventoryRow> rows = new ArrayList<>();
            final List<AbstractInventoryColumn> columns = new ArrayList<>();
            for (int x = 0; x < this.columns; x++) {
                columnSlots[x] = new ArrayList<>();
            }
            for (int y = 0; y < this.rows; y++) {
                final List<PrioritizedObject<AbstractSlot>> prioritizedRowSlotObjects = new ArrayList<>();
                for (int x = 0; x < this.columns; x++) {
                    final PrioritizedObject<LanternInventoryArchetype<? extends AbstractSlot>> prioritizedObject = this.slots[y][x];
                    checkState(prioritizedObject != null, "Missing slot at %s;%s within the grid with dimensions %s;%s", x, y, this.columns, this.rows);
                    final AbstractSlot slot = prioritizedObject.object.build();
                    // Set the parent inventory of the slot
                    slot.setParentSafely(inventory);
                    // Add the slot to the list, order matters
                    final PrioritizedObject<AbstractSlot> prioritizedSlotObject = new PrioritizedObject<>(slot, prioritizedObject.priority);
                    prioritizedSlotObjects.add(prioritizedSlotObject);
                    prioritizedRowSlotObjects.add(prioritizedSlotObject);
                    columnSlots[x].add(prioritizedSlotObject);
                }
                final AbstractInventoryRow row = this.rowTypes[y] == null ? new LanternInventoryRow() : this.rowTypes[y].construct();
                final ImmutableList<AbstractSlot> slots = prioritizedRowSlotObjects.stream()
                        .map(e -> e.object).collect(ImmutableList.toImmutableList());
                final ImmutableList<AbstractSlot> prioritizedSlots = prioritizedRowSlotObjects.stream().sorted()
                        .map(e -> e.object).collect(ImmutableList.toImmutableList());
                row.initWithSlots((List) slots, slots, prioritizedSlots);
                row.setParentSafely(inventory); // Only set the parent if not done before
                rows.add(row);
            }
            for (int x = 0; x < this.columns; x++) {
                final AbstractInventoryColumn column = this.columnTypes[x] == null ? new LanternInventoryColumn() : this.columnTypes[x].construct();
                final ImmutableList<AbstractSlot> slots = prioritizedSlotObjects.stream()
                        .map(e -> e.object).collect(ImmutableList.toImmutableList());
                final ImmutableList<AbstractSlot> prioritizedSlots = prioritizedSlotObjects.stream().sorted()
                        .map(e -> e.object).collect(ImmutableList.toImmutableList());
                column.initWithSlots((List) slots, slots, prioritizedSlots);
                column.setParentSafely(inventory); // Only set the parent if not done before
                columns.add(column);
            }
            final ImmutableList<AbstractSlot> slots = prioritizedSlotObjects.stream()
                    .map(e -> e.object).collect(ImmutableList.toImmutableList());
            final ImmutableList<AbstractSlot> prioritizedSlots = prioritizedSlotObjects.stream().sorted()
                    .map(e -> e.object).collect(ImmutableList.toImmutableList());
            inventory.init((List) slots, slots, rows, columns, prioritizedSlots);
        }

        @Override
        protected void copyTo(SlotsBuilder<T> copy) {
            super.copyTo(copy);
            copy.slots = new PrioritizedObject[this.rows][];
            for (int i = 0; i < this.rows; i++) {
                copy.slots[i] = Arrays.copyOf(this.slots[i], this.slots[i].length);
            }
        }

        @Override
        protected SlotsBuilder<T> newBuilder() {
            return new SlotsBuilder<>();
        }

        @Override
        protected List<InventoryArchetype> getArchetypes() {
            if (this.cachedArchetypesList == null) {
                final List<PrioritizedObject<InventoryArchetype>> list = new ArrayList<>();
                for (int y = 0; y < this.slots.length; y++) {
                    for (int x = 0; x < this.slots[y].length; x++) {
                        checkState(this.slots[y][x] != null, "There is no slot bound at %s;%s", x, y);
                        list.add((PrioritizedObject) this.slots[y][x]);
                    }
                }
                this.cachedArchetypesList = list.stream()
                        .sorted()
                        .map(entry -> entry.object)
                        .collect(ImmutableList.toImmutableList());
            }
            return this.cachedArchetypesList;
        }
    }

    public static final class RowsViewBuilder<T extends AbstractGridInventory> extends ViewBuilder<T, RowsViewBuilder<T>> {

        static final class InventoryEntry extends PrioritizedObject<AbstractInventory2D> {

            private final int rows;
            private final int y;

            private InventoryEntry(AbstractInventory2D object, int y, int rows, int priority) {
                super(object, priority);
                this.rows = rows;
                this.y = y;
            }
        }

        private InventoryEntry[] entries = new InventoryEntry[0];

        public RowsViewBuilder<T> grid(int y, AbstractGridInventory gridInventory, int priority) {
            return inventory(y, gridInventory, priority);
        }

        public RowsViewBuilder<T> grid(int y, AbstractGridInventory gridInventory) {
            return grid(y, gridInventory, DEFAULT_PRIORITY);
        }

        public RowsViewBuilder<T> row(int y, AbstractInventoryRow rowInventory, int priority) {
            return inventory(y, rowInventory, priority);
        }

        public RowsViewBuilder<T> row(int y, AbstractInventoryRow rowInventory) {
            return row(y, rowInventory, DEFAULT_PRIORITY);
        }

        private RowsViewBuilder<T> inventory(int y, AbstractInventory2D inventory, int priority) {
            checkNotNull(inventory, "inventory");
            final int columns = inventory.getColumns();
            final int rows = inventory.getRows();
            checkState(this.columns == 0 || this.columns == columns,
                    "Inventory columns mismatch, this must be %s but was %s", this.columns, columns);
            expand(columns, rows + y);
            final InventoryEntry entry = new InventoryEntry(inventory, y, rows, priority);
            for (int i = 0; i < rows; i++) {
                final int index = y + i;
                checkState(this.entries[index] == null, "The row %s is already occupied", index);
                this.entries[index] = entry;
            }
            return this;
        }

        @Override
        public <N extends AbstractGridInventory> RowsViewBuilder<N> type(Class<N> inventoryType) {
            return (RowsViewBuilder<N>) super.type(inventoryType);
        }

        @Override
        public RowsViewBuilder<T> expand(int columns, int rows) {
            checkState(this.columns == 0 || this.columns <= columns,
                    "Cannot expand the amount of columns to %s, it is already fixed at %s", columns, this.columns);
            if (rows > this.rows) {
                this.entries = Arrays.copyOf(this.entries, rows);
            }
            return super.expand(columns, rows);
        }

        @Override
        protected void build(T inventory) {
            final List<PrioritizedObject<AbstractSlot>>[] columnSlots = new List[this.columns];
            for (int x = 0; x < this.columns; x++) {
                columnSlots[x] = new ArrayList<>();
            }
            for (int y = 0; y < this.rows; y++) {
                checkState(this.entries[y] != null, "Missing row at %s within the rows grid with dimensions %s;%s", y, this.columns, this.rows);
            }
            final ImmutableList.Builder<AbstractInventoryRow> rows = ImmutableList.builder();
            final List<PrioritizedObject<AbstractSlot>> prioritizedSlotObjects = new ArrayList<>();
            final List<AbstractMutableInventory> inventories = new ArrayList<>();
            final Set<InventoryEntry> processed = new HashSet<>();
            for (InventoryEntry entry : this.entries) {
                // Process each inventory entry once, even if
                // it's added multiple times
                if (!processed.add(entry)) {
                    continue;
                }
                final AbstractInventory2D inventory2D = entry.object;
                inventories.add(inventory2D);
                // We can use the row as the row instance as well
                if (entry.object instanceof AbstractInventoryRow) {
                    rows.add((AbstractInventoryRow) inventory2D);
                    for (int x = 0; x < this.columns; x++) {
                        final PrioritizedObject<AbstractSlot> prioritizedSlotObject =
                                new PrioritizedObject<>((AbstractSlot) inventory2D.getSlot(x).get(), entry.priority);
                        columnSlots[x].add(prioritizedSlotObject);
                        prioritizedSlotObjects.add(prioritizedSlotObject);
                    }
                } else {
                    for (int i = 0; i < entry.rows; i++) {
                        // Construct the row that will use this grid as parent, also try to generate
                        // a row with the supplier provided in the other grid inventory.
                        final int y = entry.y + i;
                        final AbstractInventoryRow newRow = this.rowTypes[y] != null ? this.rowTypes[y].construct() : new LanternInventoryRow();
                        final List<PrioritizedObject<AbstractSlot>> prioritizedRowSlotObjects = new ArrayList<>();
                        for (int x = 0; x < this.columns; x++) {
                            final PrioritizedObject<AbstractSlot> prioritizedSlotObject =
                                    new PrioritizedObject<>((AbstractSlot) inventory2D.getSlot(x, i).get(), entry.priority);
                            prioritizedRowSlotObjects.add(prioritizedSlotObject);
                            prioritizedSlotObjects.add(prioritizedSlotObject);
                            columnSlots[x].add(prioritizedSlotObject);
                        }
                        final ImmutableList<AbstractSlot> slots = prioritizedRowSlotObjects.stream()
                                .map(e -> e.object).collect(ImmutableList.toImmutableList());
                        final ImmutableList<AbstractSlot> prioritizedSlots = prioritizedRowSlotObjects.stream().sorted()
                                .map(e -> e.object).collect(ImmutableList.toImmutableList());
                        newRow.initWithSlots((List) slots, slots, prioritizedSlots);
                        newRow.setParentSafely(inventory);
                        rows.add(newRow);
                    }
                }
            }
            final ImmutableList.Builder<AbstractInventoryColumn> columns = ImmutableList.builder();
            for (int x = 0; x < this.columns; x++) {
                final AbstractInventoryColumn column = this.columnTypes[x] == null ? new LanternInventoryColumn() : this.columnTypes[x].construct();
                final ImmutableList<AbstractSlot> slots = columnSlots[x].stream()
                        .map(e -> e.object).collect(ImmutableList.toImmutableList());
                final ImmutableList<AbstractSlot> prioritizedSlots = columnSlots[x].stream().sorted()
                        .map(e -> e.object).collect(ImmutableList.toImmutableList());
                column.initWithSlots((List) slots, slots, prioritizedSlots);
                column.setParentSafely(inventory); // Only set the parent if not done before
                columns.add(column);
            }
            final ImmutableList<AbstractSlot> slots = prioritizedSlotObjects.stream()
                    .map(e -> e.object).collect(ImmutableList.toImmutableList());
            final ImmutableList<AbstractSlot> prioritizedSlots = prioritizedSlotObjects.stream().sorted()
                    .map(e -> e.object).collect(ImmutableList.toImmutableList());
            inventory.init(inventories, slots, rows.build(), columns.build(), prioritizedSlots);
        }
    }

    public static final class RowsBuilder<T extends AbstractGridInventory> extends Builder<T, RowsBuilder<T>> {

        static final class ArchetypeEntry extends PrioritizedObject<LanternInventoryArchetype<? extends AbstractInventory2D>> {

            private final int rows;
            private final int y;

            private ArchetypeEntry(LanternInventoryArchetype<? extends AbstractInventory2D> object, int y, int rows, int priority) {
                super(object, priority);
                this.rows = rows;
                this.y = y;
            }
        }

        private ArchetypeEntry[] entries = new ArchetypeEntry[0];
        @Nullable private List<InventoryArchetype> cachedArchetypesList;

        public RowsBuilder<T> grid(int y, LanternInventoryArchetype<? extends AbstractGridInventory> gridArchetype, int priority) {
            return inventory(y, gridArchetype, priority);
        }

        public RowsBuilder<T> grid(int y, LanternInventoryArchetype<? extends AbstractGridInventory> gridArchetype) {
            return grid(y, gridArchetype, DEFAULT_PRIORITY);
        }

        public RowsBuilder<T> row(int y, LanternInventoryArchetype<? extends AbstractInventoryRow> rowArchetype, int priority) {
            return inventory(y, rowArchetype, priority);
        }

        public RowsBuilder<T> row(int y, LanternInventoryArchetype<? extends AbstractInventoryRow> rowArchetype) {
            return row(y, rowArchetype, DEFAULT_PRIORITY);
        }

        RowsBuilder<T> inventory(LanternInventoryArchetype<? extends AbstractInventory2D> archetype) {
            for (int i = 0; i < this.rows; i++) {
                if (this.entries[i] == null) {
                    return inventory(i, archetype, DEFAULT_PRIORITY);
                }
            }
            throw new IllegalStateException("No free row index could be found.");
        }

        private RowsBuilder<T> inventory(int y, LanternInventoryArchetype<? extends AbstractInventory2D> archetype, int priority) {
            checkNotNull(archetype, "archetype");
            final int columns;
            final int rows;
            if (archetype.getBuilder() instanceof SlotsBuilder) {
                columns = ((SlotsBuilder) archetype.getBuilder()).columns;
                rows = ((SlotsBuilder) archetype.getBuilder()).rows;
            } else {
                columns = archetype.getChildArchetypes().size();
                rows = 1;
            }
            checkState(this.columns == 0 || this.columns == columns,
                    "Inventory columns mismatch, this must be %s but was %s", this.columns, columns);
            expand(columns, rows + y);
            final ArchetypeEntry entry = new ArchetypeEntry(archetype, y, rows, priority);
            for (int i = 0; i < rows; i++) {
                final int index = y + i;
                checkState(this.entries[index] == null, "The row %s is already occupied", index);
                this.entries[index] = entry;
            }
            this.cachedArchetypesList = null;
            return this;
        }

        @Override
        public <N extends AbstractGridInventory> RowsBuilder<N> type(Class<N> inventoryType) {
            return (RowsBuilder<N>) super.type(inventoryType);
        }

        @Override
        public RowsBuilder<T> expand(int columns, int rows) {
            checkState(this.columns == 0 || this.columns <= columns,
                    "Cannot expand the amount of columns to %s, it is already fixed at %s", columns, this.columns);
            if (rows > this.rows) {
                this.entries = Arrays.copyOf(this.entries, rows);
            }
            return super.expand(columns, rows);
        }

        @Override
        protected void build(T inventory) {
            final List<PrioritizedObject<AbstractSlot>>[] columnSlots = new List[this.columns];
            for (int x = 0; x < this.columns; x++) {
                columnSlots[x] = new ArrayList<>();
            }
            for (int y = 0; y < this.rows; y++) {
                checkState(this.entries[y] != null, "Missing row at %s within the rows grid with dimensions %s;%s", y, this.columns, this.rows);
            }
            final ImmutableList.Builder<AbstractInventoryRow> rows = ImmutableList.builder();
            final List<PrioritizedObject<AbstractSlot>> prioritizedSlotObjects = new ArrayList<>();
            final List<AbstractMutableInventory> children = new ArrayList<>();
            final Set<ArchetypeEntry> processed = new HashSet<>();
            for (ArchetypeEntry entry : this.entries) {
                // Process each archetype entry once, even if
                // it's added multiple times
                if (!processed.add(entry)) {
                    continue;
                }
                final AbstractInventory2D inventory2D = entry.object.build();
                children.add(inventory2D);
                // We can use the row as the row instance as well
                if (inventory2D instanceof AbstractInventoryRow) {
                    rows.add((AbstractInventoryRow) inventory2D);
                    inventory2D.setParentSafely(inventory);
                    for (int x = 0; x < this.columns; x++) {
                        final PrioritizedObject<AbstractSlot> prioritizedSlotObject =
                                new PrioritizedObject<>(inventory2D.getIndexedSlotInventories().get(x), entry.priority);
                        columnSlots[x].add(prioritizedSlotObject);
                        prioritizedSlotObjects.add(prioritizedSlotObject);
                    }
                } else {
                    for (int i = 0; i < entry.rows; i++) {
                        // Construct the row that will use this grid as parent, also try to generate
                        // a row with the supplier provided in the other grid inventory.
                        final Builder<?,?> builder = (Builder<?, ?>) entry.object.getBuilder();
                        final int y = entry.y + i;
                        final AbstractInventoryRow newRow = this.rowTypes[y] != null ? this.rowTypes[y].construct() :
                                builder.rowTypes[i] != null ? builder.rowTypes[i].construct() : new LanternInventoryRow();
                        final List<PrioritizedObject<AbstractSlot>> prioritizedRowSlotObjects = new ArrayList<>();
                        for (int x = 0; x < this.columns; x++) {
                            final int index = y * this.columns + x;
                            final PrioritizedObject<AbstractSlot> prioritizedSlotObject =
                                    new PrioritizedObject<>(inventory2D.getIndexedSlotInventories().get(index), entry.priority);
                            prioritizedRowSlotObjects.add(prioritizedSlotObject);
                            prioritizedSlotObjects.add(prioritizedSlotObject);
                            columnSlots[x].add(prioritizedSlotObject);
                        }
                        final ImmutableList<AbstractSlot> slots = prioritizedRowSlotObjects.stream()
                                .map(e -> e.object).collect(ImmutableList.toImmutableList());
                        final ImmutableList<AbstractSlot> prioritizedSlots = prioritizedRowSlotObjects.stream().sorted()
                                .map(e -> e.object).collect(ImmutableList.toImmutableList());
                        newRow.initWithSlots((List) slots, slots, prioritizedSlots);
                        newRow.setParentSafely(inventory);
                        rows.add(newRow);
                    }
                }
            }
            final ImmutableList.Builder<AbstractInventoryColumn> columns = ImmutableList.builder();
            for (int x = 0; x < this.columns; x++) {
                final AbstractInventoryColumn column = this.columnTypes[x] == null ? new LanternInventoryColumn() : this.columnTypes[x].construct();
                final ImmutableList<AbstractSlot> slots = columnSlots[x].stream()
                        .map(e -> e.object).collect(ImmutableList.toImmutableList());
                final ImmutableList<AbstractSlot> prioritizedSlots = columnSlots[x].stream().sorted()
                        .map(e -> e.object).collect(ImmutableList.toImmutableList());
                column.initWithSlots((List) slots, slots, prioritizedSlots);
                column.setParentSafely(inventory); // Only set the parent if not done before
                columns.add(column);
            }
            final ImmutableList<AbstractSlot> slots = prioritizedSlotObjects.stream()
                    .map(e -> e.object).collect(ImmutableList.toImmutableList());
            final ImmutableList<AbstractSlot> prioritizedSlots = prioritizedSlotObjects.stream().sorted()
                    .map(e -> e.object).collect(ImmutableList.toImmutableList());
            inventory.init(children, slots, rows.build(), columns.build(), prioritizedSlots);
        }

        @Override
        protected void copyTo(RowsBuilder<T> copy) {
            super.copyTo(copy);
            copy.entries = Arrays.copyOf(this.entries, this.entries.length);
        }

        @Override
        protected RowsBuilder<T> newBuilder() {
            return new RowsBuilder<>();
        }

        @Override
        protected List<InventoryArchetype> getArchetypes() {
            if (this.cachedArchetypesList == null) {
                final ImmutableList.Builder<InventoryArchetype> listBuilder = ImmutableList.builder();
                final Set<ArchetypeEntry> processed = new HashSet<>();
                for (ArchetypeEntry entry : this.entries) {
                    // Process each archetype entry once, even if
                    // it's added multiple times
                    if (processed.add(entry)) {
                        listBuilder.add(entry.object);
                    }
                }
                this.cachedArchetypesList = listBuilder.build();
            }
            return this.cachedArchetypesList;
        }
    }

    public static final class ColumnsBuilder<T extends AbstractGridInventory> extends Builder<T, ColumnsBuilder<T>> {

        static final class ArchetypeEntry extends PrioritizedObject<LanternInventoryArchetype<? extends AbstractInventory2D>> {

            private final int columns;
            private final int x;

            private ArchetypeEntry(LanternInventoryArchetype<? extends AbstractInventory2D> object, int x, int columns, int priority) {
                super(object, priority);
                this.columns = columns;
                this.x = x;
            }
        }

        private ArchetypeEntry[] entries = new ArchetypeEntry[0];
        @Nullable private List<InventoryArchetype> cachedArchetypesList;

        public ColumnsBuilder<T> grid(int x, LanternInventoryArchetype<? extends AbstractGridInventory> gridArchetype, int priority) {
            return inventory(x, gridArchetype, priority);
        }

        public ColumnsBuilder<T> grid(int x, LanternInventoryArchetype<? extends AbstractGridInventory> gridArchetype) {
            return grid(x, gridArchetype, DEFAULT_PRIORITY);
        }

        public ColumnsBuilder<T> column(int x, LanternInventoryArchetype<? extends AbstractInventoryRow> rowArchetype, int priority) {
            return inventory(x, rowArchetype, priority);
        }

        public ColumnsBuilder<T> column(int x, LanternInventoryArchetype<? extends AbstractInventoryRow> rowArchetype) {
            return column(x, rowArchetype, DEFAULT_PRIORITY);
        }

        ColumnsBuilder<T> inventory(LanternInventoryArchetype<? extends AbstractInventory2D> archetype) {
            for (int i = 0; i < this.columns; i++) {
                if (this.entries[i] == null) {
                    return inventory(i, archetype, DEFAULT_PRIORITY);
                }
            }
            throw new IllegalStateException("No free column index could be found.");
        }

        private ColumnsBuilder<T> inventory(int x, LanternInventoryArchetype<? extends AbstractInventory2D> archetype, int priority) {
            checkNotNull(archetype, "archetype");
            final int columns;
            final int rows;
            if (archetype.getBuilder() instanceof SlotsBuilder) {
                columns = ((SlotsBuilder) archetype.getBuilder()).columns;
                rows = ((SlotsBuilder) archetype.getBuilder()).rows;
            } else {
                columns = 1;
                rows = archetype.getChildArchetypes().size();
            }
            checkState(this.rows == 0 || this.rows == columns,
                    "Inventory rows mismatch, this must be %s but was %s", this.rows, rows);
            expand(columns + x, rows);
            final ArchetypeEntry entry = new ArchetypeEntry(archetype, x, columns, priority);
            for (int i = 0; i < rows; i++) {
                final int index = x + i;
                checkState(this.entries[index] == null, "The column %s is already occupied", index);
                this.entries[index] = entry;
            }
            this.cachedArchetypesList = null;
            return this;
        }

        @Override
        public <N extends AbstractGridInventory> ColumnsBuilder<N> type(Class<N> inventoryType) {
            return (ColumnsBuilder<N>) super.type(inventoryType);
        }

        @Override
        public ColumnsBuilder<T> expand(int columns, int rows) {
            checkState(this.rows == 0 || this.rows <= rows,
                    "Cannot expand the amount of rows to %s, it is already fixed at %s", rows, this.rows);
            if (columns > this.columns) {
                this.entries = Arrays.copyOf(this.entries, columns);
            }
            return super.expand(columns, rows);
        }

        @Override
        protected void build(T inventory) {
            final List<PrioritizedObject<AbstractSlot>>[] rowSlots = new List[this.columns];
            for (int y = 0; y < this.rows; y++) {
                rowSlots[y] = new ArrayList<>();
            }
            for (int x = 0; x < this.columns; x++) {
                checkState(this.entries[x] != null, "Missing column at %s within the columns grid with dimensions %s;%s",
                        x, this.columns, this.rows);
            }
            final ImmutableList.Builder<AbstractInventoryColumn> columns = ImmutableList.builder();
            final List<AbstractMutableInventory> children = new ArrayList<>();
            final Set<ArchetypeEntry> processed = new HashSet<>();
            for (ArchetypeEntry entry : this.entries) {
                // Process each archetype entry once, even if
                // it's added multiple times
                if (!processed.add(entry)) {
                    continue;
                }
                final AbstractInventory2D inventory2D = entry.object.build();
                children.add(inventory2D);
                // We can use the row as the row instance as well
                if (inventory2D instanceof AbstractInventoryColumn) {
                    columns.add((AbstractInventoryColumn) inventory2D);
                    inventory2D.setParentSafely(inventory);
                    for (int y = 0; y < this.rows; y++) {
                        rowSlots[y].add(new PrioritizedObject<>(inventory2D.getSlotInventories().get(y), entry.priority));
                    }
                } else {
                    final AbstractGridInventory gridInventory = (AbstractGridInventory) inventory2D;
                    for (int i = 0; i < entry.columns; i++) {
                        final AbstractInventoryColumn column = (AbstractInventoryColumn) gridInventory.getColumn(i).get();
                        // Construct the row that will use this grid as parent, also try to generate
                        // a row with the supplier provided in the other grid inventory.
                        final Builder<?,?> builder = (Builder<?, ?>) entry.object.getBuilder();
                        final int x = entry.x + i;
                        final AbstractInventoryColumn newColumn = this.columnTypes[x] != null ? this.columnTypes[x].construct() :
                                builder.columnTypes[i] != null ? builder.columnTypes[i].construct() : new LanternInventoryColumn();
                        final List<PrioritizedObject<AbstractSlot>> prioritizedSlotObjects = new ArrayList<>();
                        for (int y = 0; y < this.rows; y++) {
                            final PrioritizedObject<AbstractSlot> prioritizedSlotObject =
                                    new PrioritizedObject<>(column.getSlotInventories().get(y), entry.priority);
                            rowSlots[y].add(prioritizedSlotObject);
                            prioritizedSlotObjects.add(prioritizedSlotObject);
                        }
                        final ImmutableList<AbstractSlot> slots = prioritizedSlotObjects.stream()
                                .map(e -> e.object).collect(ImmutableList.toImmutableList());
                        final ImmutableList<AbstractSlot> prioritizedSlots = prioritizedSlotObjects.stream().sorted()
                                .map(e -> e.object).collect(ImmutableList.toImmutableList());
                        newColumn.initWithSlots((List) slots, slots, prioritizedSlots);
                        newColumn.setParentSafely(inventory);
                        columns.add(newColumn);
                    }
                }
            }
            final List<PrioritizedObject<AbstractSlot>> prioritizedSlotObjects = new ArrayList<>();
            final ImmutableList.Builder<AbstractInventoryRow> rows = ImmutableList.builder();
            for (int y = 0; y < this.rows; y++) {
                final AbstractInventoryRow row = this.rowTypes[y] == null ? new LanternInventoryRow() : this.rowTypes[y].construct();
                final ImmutableList<AbstractSlot> rowSlotsList = rowSlots[y].stream()
                        .map(entry -> entry.object).collect(ImmutableList.toImmutableList());
                final ImmutableList<AbstractSlot> prioritizedRowSlotsList = rowSlots[y].stream().sorted()
                        .map(entry -> entry.object).collect(ImmutableList.toImmutableList());
                row.initWithSlots((List) rowSlotsList, rowSlotsList, prioritizedRowSlotsList);
                row.setParentSafely(inventory); // Only set the parent if not done before
                rows.add(row);
                prioritizedSlotObjects.addAll(rowSlots[y]);
            }
            final ImmutableList<AbstractSlot> slots = prioritizedSlotObjects.stream()
                    .map(e -> e.object).collect(ImmutableList.toImmutableList());
            final ImmutableList<AbstractSlot> prioritizedSlots = prioritizedSlotObjects.stream().sorted()
                    .map(e -> e.object).collect(ImmutableList.toImmutableList());
            inventory.init(children, slots, rows.build(), columns.build(), prioritizedSlots);
        }

        @Override
        protected void copyTo(ColumnsBuilder<T> copy) {
            super.copyTo(copy);
            copy.entries = Arrays.copyOf(this.entries, this.entries.length);
        }

        @Override
        protected ColumnsBuilder<T> newBuilder() {
            return new ColumnsBuilder<>();
        }

        @Override
        protected List<InventoryArchetype> getArchetypes() {
            if (this.cachedArchetypesList == null) {
                final ImmutableList.Builder<InventoryArchetype> listBuilder = ImmutableList.builder();
                final Set<ArchetypeEntry> processed = new HashSet<>();
                for (ArchetypeEntry entry : this.entries) {
                    // Process each archetype entry once, even if
                    // it's added multiple times
                    if (processed.add(entry)) {
                        listBuilder.add(entry.object);
                    }
                }
                this.cachedArchetypesList = listBuilder.build();
            }
            return this.cachedArchetypesList;
        }
    }
}
