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
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.type.InventoryColumn;
import org.spongepowered.api.text.translation.Translation;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

public class LanternInventoryColumn extends LanternInventory2D implements InventoryColumn {

    public LanternInventoryColumn(@Nullable Inventory parent) {
        super(parent, null);
    }

    public LanternInventoryColumn(@Nullable Inventory parent, @Nullable Translation name) {
        super(parent, name);
    }

    @Override
    protected <T extends Slot> T registerSlot(T slot) {
        throw new UnsupportedOperationException("Do not use this method directly when using a InventoryColumn, see registerSlotAt(y, slot)");
    }

    /**
     * Registers a regular {@link Slot} for this inventory.
     *
     * @param y The y position of the slot in this column
     * @return The slot for chaining
     * @throws IllegalStateException If there already a slot is registered
     *      at the specified y index
     */
    protected LanternSlot registerSlotAt(int y) {
        return this.registerSlotAt(y, new LanternSlot(this));
    }

    /**
     * Registers the {@link Slot} for this inventory at the
     * specified y index.
     *
     * @param y The y position of the slot in this column
     * @param slot The slot to register
     * @return The slot for chaining
     * @throws IllegalStateException If there already a slot is registered
     *      at the specified y index
     */
    protected <T extends Slot> T registerSlotAt(int y, T slot) {
        this.registerSlot(this.nextFreeSlotIndex(), y, slot);
        return slot;
    }

    /**
     * Registers the {@link Slot} for this inventory.
     *
     * @param index The index of the slot
     * @param y The y position of the slot in this column
     * @param slot The slot to register
     * @return The slot for chaining
     */
    <T extends Slot> T registerSlot(int index, int y, T slot) {
        checkNotNull(slot, "slot");
        checkArgument(y >= 0, "y position may not be negative");
        checkArgument(this.slots.size() <= index || this.slots.get(index) == null, "The slot index %s is already in use", index);
        checkArgument(!this.indexBySlot.containsKey(slot), "The slot is already registered");
        final Vector2i pos = new Vector2i(0, y);
        checkArgument(!this.slotsByPos.containsKey(pos), "The slot position (0;%s) is already in use", y);
        this.registerSlot(index, slot, true);
        this.slotsByPos.put(pos, (LanternSlot) slot);
        return slot;
    }

    @Override
    protected <T extends InventoryProperty<?, ?>> Optional<T> tryGetProperty(Class<T> property, @Nullable Object key) {
        if (property == InventoryDimension.class) {
            //noinspection unchecked
            return Optional.of((T) new InventoryDimension(1, this.size()));
        }
        return super.tryGetProperty(property, key);
    }

    @Override
    protected <T extends InventoryProperty<?, ?>> List<T> tryGetProperties(Class<T> property) {
        final List<T> properties = super.tryGetProperties(property);
        if (property == InventoryDimension.class) {
            //noinspection unchecked
            properties.add((T) new InventoryDimension(1, this.size()));
        }
        return properties;
    }
}
