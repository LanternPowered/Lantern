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
import org.lanternpowered.server.inventory.type.LanternGridInventory;
import org.lanternpowered.server.inventory.type.LanternInventoryColumn;
import org.lanternpowered.server.inventory.type.LanternInventoryRow;
import org.lanternpowered.server.inventory.type.LanternOrderedInventory;
import org.lanternpowered.server.inventory.vanilla.VanillaInventoryArchetypes;
import org.lanternpowered.server.plugin.InternalPluginsInfo;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.property.InventoryCapacity;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.item.inventory.type.InventoryColumn;
import org.spongepowered.api.item.inventory.type.InventoryRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class LanternInventoryArchetypeBuilder implements InventoryArchetype.Builder {

    private final List<LanternInventoryArchetype<?>> archetypes = new ArrayList<>();
    private final Map<Class<?>, InventoryProperty<?,?>> properties = new HashMap<>();

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
    public InventoryArchetype.Builder property(InventoryProperty<String, ?> property) {
        checkNotNull(property, "property");
        if (this.baseArchetype != null &&
                this.baseArchetype.getBuilder() instanceof AbstractSlot.Builder) {
            // Disallow modifying the slot capacity
            if (property instanceof InventoryCapacity && ((InventoryCapacity) property).getValue() != 1) {
                throw new IllegalArgumentException("It's not possible to modify the capacity of"
                        + "a Slot through InventoryCapacity. This is fixed at 1.");
            }
            // Disallow modifying the slot dimensions
            if (property instanceof InventoryDimension) {
                final Vector2i dim = ((InventoryDimension) property).getValue();
                if (dim.getX() != 1 || dim.getY() != 1) {
                    throw new IllegalArgumentException("It's not possible to modify the dimensions of"
                            + "a Slot through InventoryDimension. This is fixed at 1;1.");
                }
            }
        }
        this.properties.put(property.getClass(), property);
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

    private LanternInventoryArchetype<?> buildArchetype(String pluginId, String id,
            Map<Class<?>, InventoryProperty<?,?>> properties, LanternInventoryArchetype<?> archetype) {
        final AbstractArchetypeBuilder archetypeBuilder = archetype.getBuilder().copy();
        properties.values().forEach(archetypeBuilder::property);
        return archetypeBuilder.buildArchetype(pluginId, id);
    }

    private void applyProperties(Map<Class<?>, InventoryProperty<?,?>> properties, AbstractBuilder builder) {
        properties.values().forEach(builder::property);
    }

    @Override
    public InventoryArchetype build(String id, String name) {
        final int index = id.indexOf(':');
        final String pluginId;
        if (index == -1) {
            pluginId = InternalPluginsInfo.Implementation.IDENTIFIER;
        } else {
            pluginId = id.substring(0, index);
            id = id.substring(index + 1);
        }

        final Map<Class<?>, InventoryProperty<?,?>> properties = new HashMap<>(this.properties);

        final InventoryDimension inventoryDimension = (InventoryDimension) properties.remove(InventoryDimension.class);
        final InventoryCapacity inventoryCapacity = (InventoryCapacity) properties.remove(InventoryCapacity.class);

        int size = -1;

        if (inventoryDimension != null) {
            final int targetRows = inventoryDimension.getRows();
            final int targetColumns = inventoryDimension.getColumns();
            size = targetRows * targetColumns;

            if (inventoryCapacity != null && size != inventoryCapacity.getValue()) {
                throw new IllegalStateException("The InventoryCapacity " + inventoryCapacity.getValue() + " mismatches the InventoryDimension "
                        + "slots quantity: " + targetRows + " * " + targetColumns + " = " + size);
            }
        } else if (inventoryCapacity != null) {
            size = inventoryCapacity.getValue();
        }

        // A base archetype is provided to create slots
        if (this.baseArchetype != null) {
            if (this.baseArchetype.getBuilder() instanceof AbstractSlot.Builder) {
                return buildArchetype(pluginId, id, properties, this.baseArchetype);
            } else if (this.baseArchetype.getBuilder() instanceof AbstractGridInventory.Builder) {
                final AbstractGridInventory.Builder builder = (AbstractGridInventory.Builder) this.baseArchetype.getBuilder().copy();
                if (inventoryDimension != null) {
                    builder.expand(inventoryDimension.getColumns(), inventoryDimension.getRows());
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
                    return slotsBuilder.buildArchetype(pluginId, id);
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
                    return rowsBuilder.buildArchetype(pluginId, id);
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
                    return columnsBuilder.buildArchetype(pluginId, id);
                } else {
                    throw new IllegalStateException();
                }
            } else if (this.baseArchetype.getBuilder() instanceof AbstractOrderedInventory.Builder) {
                final AbstractOrderedInventory.Builder builder = (AbstractOrderedInventory.Builder) this.baseArchetype.getBuilder().copy();
                if (inventoryDimension != null) {
                    final Class<?> inventoryType = builder.constructor.getType();
                    if (InventoryColumn.class.isAssignableFrom(inventoryType) && inventoryDimension.getColumns() != 1) {
                        throw new IllegalStateException("A inventory column can only have one column, not " +
                                inventoryDimension.getColumns() + " specified by the InventoryDimension.");
                    } else if (InventoryRow.class.isAssignableFrom(inventoryType) && inventoryDimension.getRows() != 1) {
                        throw new IllegalStateException("A inventory row can only have one row, not " +
                                inventoryDimension.getRows() + " specified by the InventoryDimension.");
                    }
                    builder.expand(inventoryDimension.getColumns() * inventoryDimension.getRows());
                } else if (inventoryCapacity != null) {
                    builder.expand(inventoryCapacity.getValue());
                }
                for (LanternInventoryArchetype<?> archetype : this.archetypes) {
                    if (!(archetype.getBuilder() instanceof AbstractSlot.Builder)) {
                        throw new IllegalStateException("Only slot archetypes can be added to a slot based builder.");
                    }
                    builder.addLast(archetype);
                }
                applyProperties(properties, builder);
                return builder.buildArchetype(pluginId, id);
            } else if (this.baseArchetype.getBuilder() instanceof AbstractOrderedInventory.Builder) {
                if (inventoryDimension != null) {
                    throw new IllegalStateException("A InventoryDimension cannot be applied to a ordered children inventory.");
                }
                final AbstractOrderedInventory.Builder builder = (AbstractOrderedInventory.Builder) this.baseArchetype.getBuilder().copy();
                for (LanternInventoryArchetype<?> archetype : this.archetypes) {
                    builder.addLast(archetype);
                }
                applyProperties(properties, builder);
                final LanternInventoryArchetype<?> archetype = builder.buildArchetype(pluginId, id);
                if (inventoryCapacity != null) {
                    final AbstractInventory inventory = archetype.build();
                    if (inventory.capacity() != inventoryCapacity.getValue()) {
                        throw new IllegalStateException("InventoryCapacity mismatch with the size of the resulting inventory. Got " +
                                inventory.capacity() + ", but expected " + inventoryCapacity.getValue());
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
            return buildArchetype(pluginId, name, properties, archetype);
        } else if (size == 0 || this.archetypes.isEmpty()) { // A empty archetype
            return new UnknownInventoryArchetype(pluginId, id);
        }

        if (inventoryDimension != null) {
            final int targetRows = inventoryDimension.getRows();
            final int targetColumns = inventoryDimension.getColumns();

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
                    final AbstractOrderedInventory.Builder<LanternOrderedInventory> builder = AbstractOrderedInventory.builder();
                    for (LanternInventoryArchetype<?> archetype : this.archetypes) {
                        builder.addLast((LanternInventoryArchetype<? extends AbstractSlot>) archetype);
                    }
                    applyProperties(properties, builder);
                    if (targetColumns == 1) {
                        return builder.type(LanternInventoryColumn.class).buildArchetype(pluginId, id);
                    } else {
                        return builder.type(LanternInventoryRow.class).buildArchetype(pluginId, id);
                    }
                } else {
                    final AbstractGridInventory.SlotsBuilder<LanternGridInventory> builder = AbstractGridInventory.slotsBuilder();
                    for (int y = 0; y < targetRows; y++) {
                        for (int x = 0; x < targetColumns; x++) {
                            builder.slot(x, y, (LanternInventoryArchetype<? extends AbstractSlot>) this.archetypes.get(y * targetColumns + x));
                        }
                    }
                    applyProperties(properties, builder);
                    return builder.buildArchetype(pluginId, id);
                }
            }

            // Try to copy a row archetype
            if ((targetColumns == 1 || targetRows == 1) && this.archetypes.size() == 1) {
                final LanternInventoryArchetype<?> archetype = this.archetypes.get(0);
                if (archetype.getBuilder() instanceof AbstractOrderedInventory.Builder) {
                    final AbstractOrderedInventory.Builder<AbstractOrderedInventory> builder =
                            (AbstractOrderedInventory.Builder<AbstractOrderedInventory>) archetype.getBuilder().copy();
                    final Class<?> inventoryType = ((AbstractOrderedInventory.Builder) archetype.getBuilder()).constructor.getType();
                    if (targetRows == 1 && !(InventoryRow.class.isAssignableFrom(inventoryType))) {
                        builder.type(LanternInventoryRow.class);
                    } else if (targetColumns == 1 && !(InventoryColumn.class.isAssignableFrom(inventoryType))) {
                        builder.type(LanternInventoryColumn.class);
                    }
                    applyProperties(properties, builder);
                    return builder.buildArchetype(pluginId, id);
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
                } else if (archetype.getBuilder() instanceof AbstractOrderedInventory.Builder) {
                    final Class<?> inventoryType = ((AbstractOrderedInventory.Builder) archetype.getBuilder()).constructor.getType();
                    if (InventoryRow.class.isAssignableFrom(inventoryType)) {
                        columns1 = ((AbstractOrderedInventory.Builder) archetype.getBuilder()).slots;
                        rows1 = 1;
                    } else if (InventoryColumn.class.isAssignableFrom(inventoryType)) {
                        rows1 = ((AbstractOrderedInventory.Builder) archetype.getBuilder()).slots;
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
                return builder.type(LanternGridInventory.class).buildArchetype(pluginId, id);
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
                return builder.type(LanternGridInventory.class).buildArchetype(pluginId, id);
            }
        }

        // Just generate a ordered children inventory
        final AbstractOrderedInventory.Builder<LanternOrderedInventory> builder = AbstractOrderedInventory.builder();
        for (LanternInventoryArchetype<?> archetype : this.archetypes) {
            builder.addLast((LanternInventoryArchetype<? extends AbstractMutableInventory>) archetype);
        }
        applyProperties(properties, builder);
        return builder.buildArchetype(pluginId, id);
    }
}
