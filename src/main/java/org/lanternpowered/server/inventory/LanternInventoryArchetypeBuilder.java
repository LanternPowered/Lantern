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

import com.flowpowered.math.vector.Vector2i;
import org.lanternpowered.server.catalog.AbstractCatalogBuilder;
import org.lanternpowered.server.inventory.type.LanternChildrenInventory;
import org.lanternpowered.server.inventory.type.LanternGridInventory;
import org.lanternpowered.server.inventory.type.LanternInventoryColumn;
import org.lanternpowered.server.inventory.type.LanternInventoryRow;
import org.lanternpowered.server.inventory.vanilla.VanillaInventoryArchetypes;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.property.Property;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperties;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.item.inventory.type.InventoryColumn;
import org.spongepowered.api.item.inventory.type.InventoryRow;
import org.spongepowered.api.text.translation.Translation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class LanternInventoryArchetypeBuilder extends AbstractCatalogBuilder<InventoryArchetype, InventoryArchetype.Builder>
        implements InventoryArchetype.Builder {

    private final List<LanternInventoryArchetype<?>> archetypes = new ArrayList<>();
    private final Map<Property<?>, Object> properties = new HashMap<>();

    @Nullable private LanternInventoryArchetype<?> baseArchetype;

    @Override
    public InventoryArchetype.Builder from(InventoryArchetype value) {
        checkNotNull(value, "value");
        this.baseArchetype = (LanternInventoryArchetype<?>) value;
        this.archetypes.clear();
        this.properties.clear();
        return this;
    }

    @Override
    public InventoryArchetype.Builder reset() {
        this.baseArchetype = null;
        this.archetypes.clear();
        this.properties.clear();
        return this;
    }

    @Override
    public <V> InventoryArchetype.Builder property(Property<V> property, V value) {
        checkNotNull(property, "property");
        if (this.baseArchetype != null &&
                this.baseArchetype.getBuilder() instanceof AbstractSlot.Builder) {
            // Disallow modifying the slot capacity
            if (property == InventoryProperties.CAPACITY && (Integer) value != 1) {
                throw new IllegalArgumentException("It's not possible to modify the capacity of"
                        + "a Slot through InventoryCapacity. This is fixed at 1.");
            }
            // Disallow modifying the slot dimensions
            if (property == InventoryProperties.DIMENSION) {
                final Vector2i dim = (Vector2i) value;
                if (dim.getX() != 1 || dim.getY() != 1) {
                    throw new IllegalArgumentException("It's not possible to modify the dimensions of"
                            + "a Slot through InventoryDimension. This is fixed at 1;1.");
                }
            }
        }
        this.properties.put(property, property);
        return this;
    }

    @Override
    public InventoryArchetype.Builder with(InventoryArchetype archetype) {
        checkNotNull(archetype, "archetype");
        if (this.baseArchetype != null && this.baseArchetype.getBuilder() instanceof AbstractSlot.Builder) {
            throw new IllegalArgumentException("Slots cannot contain children InventoryArchetypes.");
        }
        this.archetypes.add((LanternInventoryArchetype<?>) archetype);
        return this;
    }

    @Override
    public InventoryArchetype.Builder with(InventoryArchetype... archetypes) {
        Arrays.stream(archetypes).forEach(this::with);
        return this;
    }

    private LanternInventoryArchetype<?> buildArchetype(CatalogKey key,
            Map<Property<?>, Object> properties, LanternInventoryArchetype<?> archetype) {
        final AbstractArchetypeBuilder archetypeBuilder = archetype.getBuilder().copy();
        properties.forEach(archetypeBuilder::property);
        return archetypeBuilder.buildArchetype(key);
    }

    private void applyProperties(Map<Property<?>, Object> properties, AbstractBuilder builder) {
        properties.forEach(builder::property);
    }

    @Override
    protected InventoryArchetype build(CatalogKey key, Translation name) {
        final Map<Property<?>, Object> properties = new HashMap<>(this.properties);

        final Vector2i inventoryDimension = (Vector2i) properties.remove(InventoryProperties.DIMENSION);
        final Integer inventoryCapacity = (Integer) properties.remove(InventoryProperties.CAPACITY);

        int size = -1;

        if (inventoryDimension != null) {
            final int targetRows = inventoryDimension.getY();
            final int targetColumns = inventoryDimension.getX();
            size = targetRows * targetColumns;

            if (inventoryCapacity != null && size != inventoryCapacity) {
                throw new IllegalStateException("The InventoryCapacity " + inventoryCapacity + " mismatches the InventoryDimension "
                        + "slots quantity: " + targetRows + " * " + targetColumns + " = " + size);
            }
        } else if (inventoryCapacity != null) {
            size = inventoryCapacity;
        }

        // A base archetype is provided to create slots
        if (this.baseArchetype != null) {
            if (this.baseArchetype.getBuilder() instanceof AbstractSlot.Builder) {
                return buildArchetype(key, properties, this.baseArchetype);
            } else if (this.baseArchetype.getBuilder() instanceof AbstractGridInventory.Builder) {
                final AbstractGridInventory.Builder builder = (AbstractGridInventory.Builder) this.baseArchetype.getBuilder().copy();
                if (inventoryDimension != null) {
                    builder.expand(inventoryDimension.getX(), inventoryDimension.getY());
                }
                if (builder instanceof AbstractGridInventory.SlotsBuilder) {
                    final AbstractGridInventory.SlotsBuilder slotsBuilder = (AbstractGridInventory.SlotsBuilder) builder;
                    for (LanternInventoryArchetype<?> archetype : this.archetypes) {
                        if (!(archetype.getBuilder() instanceof AbstractSlot.Builder)) {
                            throw new IllegalStateException("Only slot archetypes can be added to a slot based grid builder.");
                        }
                        slotsBuilder.slot(archetype);
                    }
                    applyProperties(properties, builder);
                    return slotsBuilder.buildArchetype(key);
                } else if (builder instanceof AbstractGridInventory.RowsBuilder) {
                    final AbstractGridInventory.RowsBuilder rowsBuilder = (AbstractGridInventory.RowsBuilder) builder;
                    for (LanternInventoryArchetype<?> archetype : this.archetypes) {
                        final Class<?> inventoryType = builder.constructor.getType();
                        if (InventoryRow.class.isAssignableFrom(inventoryType) ||
                                GridInventory.class.isAssignableFrom(inventoryType)) {
                            rowsBuilder.inventory(archetype);
                        } else {
                            throw new IllegalStateException("Only rows or grids can be added to a rows based grid builder.");
                        }
                    }
                    applyProperties(properties, builder);
                    return rowsBuilder.buildArchetype(key);
                } else if (builder instanceof AbstractGridInventory.ColumnsBuilder) {
                    final AbstractGridInventory.ColumnsBuilder columnsBuilder = (AbstractGridInventory.ColumnsBuilder) builder;
                    for (LanternInventoryArchetype<?> archetype : this.archetypes) {
                        final Class<?> inventoryType = builder.constructor.getType();
                        if (InventoryColumn.class.isAssignableFrom(inventoryType) ||
                                GridInventory.class.isAssignableFrom(inventoryType)) {
                            columnsBuilder.inventory(archetype);
                        } else {
                            throw new IllegalStateException("Only columns or grids can be added to a columns based grid builder.");
                        }
                    }
                    applyProperties(properties, builder);
                    return columnsBuilder.buildArchetype(key);
                } else {
                    throw new IllegalStateException();
                }
            } else if (this.baseArchetype.getBuilder() instanceof AbstractChildrenInventory.Builder) {
                final AbstractChildrenInventory.Builder builder = (AbstractChildrenInventory.Builder) this.baseArchetype.getBuilder().copy();
                if (inventoryDimension != null) {
                    final Class<?> inventoryType = builder.constructor.getType();
                    if (InventoryColumn.class.isAssignableFrom(inventoryType) && inventoryDimension.getX() != 1) {
                        throw new IllegalStateException("A inventory column can only have one column, not " +
                                inventoryDimension.getX() + " specified by the InventoryDimension.");
                    } else if (InventoryRow.class.isAssignableFrom(inventoryType) && inventoryDimension.getY() != 1) {
                        throw new IllegalStateException("A inventory row can only have one row, not " +
                                inventoryDimension.getY() + " specified by the InventoryDimension.");
                    }
                    builder.expand(inventoryDimension.getX() * inventoryDimension.getY());
                } else if (inventoryCapacity != null) {
                    builder.expand(inventoryCapacity);
                }
                for (LanternInventoryArchetype<?> archetype : this.archetypes) {
                    builder.addLast(archetype);
                }
                applyProperties(properties, builder);
                final LanternInventoryArchetype<?> archetype = builder.buildArchetype(key);
                if (inventoryCapacity != null) {
                    final AbstractInventory inventory = archetype.build();
                    if (inventory.capacity() != inventoryCapacity) {
                        throw new IllegalStateException("InventoryCapacity mismatch with the size of the resulting inventory. Got " +
                                inventory.capacity() + ", but expected " + inventoryCapacity);
                    }
                }
                return archetype;
            }
        }

        // A slot
        if (size == 1) {
            final LanternInventoryArchetype<?> archetype;
            if (!this.archetypes.isEmpty()) {
                archetype = this.archetypes.get(0);
                if (this.archetypes.size() != 1 || !(archetype.getBuilder() instanceof AbstractSlot.Builder)) {
                    throw new IllegalStateException("A Inventory with a InventoryCapacity of one can only be one slot.");
                }
            } else {
                archetype = VanillaInventoryArchetypes.SLOT;
            }
            return buildArchetype(key, properties, archetype);
        } else if (size == 0 || this.archetypes.isEmpty()) { // A empty archetype
            return new UnknownInventoryArchetype(key);
        }

        if (inventoryDimension != null) {
            final int targetRows = inventoryDimension.getY();
            final int targetColumns = inventoryDimension.getY();

            // There are two cases to handle a inventory dimension property, if there are only slots,
            // just generate a grid/column/row with those slots. Otherwise try to merge all the columns/rows/grid
            // into the target dimension

            // Try to generate a slots grid
            boolean allSlots = true;
            for (LanternInventoryArchetype<?> archetype : this.archetypes) {
                if (!(archetype.getBuilder() instanceof AbstractSlot.Builder)) {
                    allSlots = false;
                    break;
                }
            }
            if (allSlots) {
                if (this.archetypes.size() != size) {
                    throw new IllegalStateException("Not enough slots are found (" + this.archetypes.size() + ") to reach the capacity of " + size);
                }
                if (targetColumns == 1 || targetRows == 1) {
                    final AbstractChildrenInventory.Builder<LanternChildrenInventory> builder = AbstractChildrenInventory.builder();
                    for (LanternInventoryArchetype<?> archetype : this.archetypes) {
                        builder.addLast((LanternInventoryArchetype<? extends AbstractSlot>) archetype);
                    }
                    applyProperties(properties, builder);
                    if (targetColumns == 1) {
                        return builder.type(LanternInventoryColumn.class).buildArchetype(key);
                    } else {
                        return builder.type(LanternInventoryRow.class).buildArchetype(key);
                    }
                } else {
                    final AbstractGridInventory.SlotsBuilder<LanternGridInventory> builder = AbstractGridInventory.slotsBuilder();
                    for (int y = 0; y < targetRows; y++) {
                        for (int x = 0; x < targetColumns; x++) {
                            builder.slot(x, y, (LanternInventoryArchetype<? extends AbstractSlot>) this.archetypes.get(y * targetColumns + x));
                        }
                    }
                    applyProperties(properties, builder);
                    return builder.buildArchetype(key);
                }
            }

            // Try to copy a row archetype
            if ((targetColumns == 1 || targetRows == 1) && this.archetypes.size() == 1) {
                final LanternInventoryArchetype<?> archetype = this.archetypes.get(0);
                if (archetype.getBuilder() instanceof AbstractChildrenInventory.Builder) {
                    final AbstractChildrenInventory.Builder<AbstractChildrenInventory> builder =
                            (AbstractChildrenInventory.Builder<AbstractChildrenInventory>) archetype.getBuilder().copy();
                    final Class<?> inventoryType = ((AbstractChildrenInventory.Builder) archetype.getBuilder()).constructor.getType();
                    if (targetRows == 1 && !(InventoryRow.class.isAssignableFrom(inventoryType))) {
                        builder.type(LanternInventoryRow.class);
                    } else if (targetColumns == 1 && !(InventoryColumn.class.isAssignableFrom(inventoryType))) {
                        builder.type(LanternInventoryColumn.class);
                    }
                    applyProperties(properties, builder);
                    return builder.buildArchetype(key);
                }
            }

            // Try to construct the grid from rows/columns/grids

            int rows = 0;
            int columns = 0;
            boolean fixedRows = false;
            boolean fixedColumns = false;

            for (LanternInventoryArchetype<?> archetype : this.archetypes) {
                // Retrieve the columns/rows from the archetype, if present
                int rows1 = 0;
                int columns1 = 0;
                if (archetype.getBuilder() instanceof AbstractGridInventory.Builder) {
                    final AbstractGridInventory.Builder builder = (AbstractGridInventory.Builder) archetype.getBuilder();
                    rows1 = builder.rows;
                    columns1 = builder.columns;
                } else if (archetype.getBuilder() instanceof AbstractChildrenInventory.Builder) {
                    final Class<?> inventoryType = ((AbstractChildrenInventory.Builder) archetype.getBuilder()).constructor.getType();
                    if (InventoryRow.class.isAssignableFrom(inventoryType)) {
                        columns1 = ((AbstractChildrenInventory.Builder) archetype.getBuilder()).slots;
                        rows1 = 1;
                    } else if (InventoryColumn.class.isAssignableFrom(inventoryType)) {
                        rows1 = ((AbstractChildrenInventory.Builder) archetype.getBuilder()).slots;
                        columns1 = 1;
                    }
                }
                if (rows1 == 0) {
                    rows = 0;
                    break;
                }
                if (rows1 == targetRows) {
                    if (fixedColumns) {
                        rows = 0;
                        break;
                    }
                    fixedRows = true;
                    rows = rows1;
                    columns += columns1;
                } else if (columns1 == targetColumns) {
                    if (fixedRows) {
                        rows = 0;
                        break;
                    }
                    fixedColumns = true;
                    columns = columns1;
                    rows += rows1;
                } else {
                    rows = 0;
                    break;
                }
            }

            if (rows != targetRows || columns != targetColumns) {
                throw new IllegalArgumentException("Unable to construct a inventory grid from the inventory archetypes.");
            }
            if (fixedColumns) {
                int y = 0;
                final AbstractGridInventory.RowsBuilder<LanternGridInventory> builder = AbstractGridInventory.rowsBuilder();
                for (LanternInventoryArchetype<?> archetype : this.archetypes) {
                    if (archetype.getBuilder() instanceof AbstractGridInventory.Builder) {
                        builder.grid(y, (LanternInventoryArchetype) archetype);
                        y += ((AbstractGridInventory.Builder) archetype.getBuilder()).rows;
                    } else {
                        builder.row(y, (LanternInventoryArchetype) archetype);
                        y++;
                    }
                }
                applyProperties(properties, builder);
                return builder.type(LanternGridInventory.class).buildArchetype(key);
            } else {
                int x = 0;
                final AbstractGridInventory.ColumnsBuilder<LanternGridInventory> builder = AbstractGridInventory.columnsBuilder();
                for (LanternInventoryArchetype<?> archetype : this.archetypes) {
                    if (archetype.getBuilder() instanceof AbstractGridInventory.Builder) {
                        builder.grid(x, (LanternInventoryArchetype) archetype);
                        x += ((AbstractGridInventory.Builder) archetype.getBuilder()).columns;
                    } else {
                        builder.column(x, (LanternInventoryArchetype) archetype);
                        x++;
                    }
                }
                applyProperties(properties, builder);
                return builder.type(LanternGridInventory.class).buildArchetype(key);
            }
        }

        // Just generate a ordered children inventory
        final AbstractChildrenInventory.Builder<LanternChildrenInventory> builder = AbstractChildrenInventory.builder();
        for (LanternInventoryArchetype<?> archetype : this.archetypes) {
            builder.addLast((LanternInventoryArchetype<? extends AbstractMutableInventory>) archetype);
        }
        applyProperties(properties, builder);
        return builder.buildArchetype(key);
    }
}
