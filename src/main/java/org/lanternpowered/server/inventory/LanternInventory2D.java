/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.type.Inventory2D;
import org.spongepowered.api.text.translation.Translation;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

public class LanternInventory2D extends LanternOrderedInventory implements Inventory2D {

    /**
     * All the {@link LanternSlot}s mapped by their position.
     */
    final BiMap<Vector2i, LanternSlot> slotsByPos = HashBiMap.create();

    public LanternInventory2D(@Nullable Inventory parent, @Nullable Translation name) {
        super(parent, name);
    }

    @Override
    public Optional<ItemStack> poll(SlotPos pos) {
        final LanternSlot slot = this.slotsByPos.get(checkNotNull(checkNotNull(pos, "pos").getValue(), "value"));
        return slot == null ? Optional.empty() : slot.poll();
    }

    @Override
    public Optional<ItemStack> poll(SlotPos pos, int limit) {
        final LanternSlot slot = this.slotsByPos.get(checkNotNull(checkNotNull(pos, "pos").getValue(), "value"));
        return slot == null ? Optional.empty() : slot.poll(limit);
    }

    @Override
    public Optional<ItemStack> peek(SlotPos pos) {
        final LanternSlot slot = this.slotsByPos.get(checkNotNull(checkNotNull(pos, "pos").getValue(), "value"));
        return slot == null ? Optional.empty() : slot.peek();
    }

    @Override
    public Optional<ItemStack> peek(SlotPos pos, int limit) {
        final LanternSlot slot = this.slotsByPos.get(checkNotNull(checkNotNull(pos, "pos").getValue(), "value"));
        return slot == null ? Optional.empty() : slot.peek(limit);
    }

    @Override
    public InventoryTransactionResult set(SlotPos pos, ItemStack stack) {
        final LanternSlot slot = this.slotsByPos.get(checkNotNull(checkNotNull(pos, "pos").getValue(), "value"));
        return slot == null ? InventoryTransactionResults.FAILURE : slot.set(stack);
    }

    @Override
    public Optional<Slot> getSlot(SlotPos pos) {
        return Optional.ofNullable(this.slotsByPos.get(checkNotNull(checkNotNull(pos, "pos").getValue(), "value")));
    }

    @Override
    protected <T extends InventoryProperty<?, ?>> Optional<T> tryGetProperty(Inventory child, Class<T> property, Object key) {
        if (property == SlotPos.class && child instanceof Slot) {
            final Vector2i pos = this.slotsByPos.inverse().get(child);
            return pos == null ? Optional.empty() : Optional.of(property.cast(SlotPos.of(pos)));
        }
        return super.tryGetProperty(child, property, key);
    }

    @Override
    protected <T extends InventoryProperty<?, ?>> List<T> tryGetProperties(Inventory child, Class<T> property) {
        final List<T> properties = super.tryGetProperties(child, property);
        if (property == SlotPos.class && child instanceof Slot) {
            final Vector2i pos = this.slotsByPos.inverse().get(child);
            if (pos != null) {
                properties.add(property.cast(SlotPos.of(pos)));
            }
        }
        return properties;
    }
}
