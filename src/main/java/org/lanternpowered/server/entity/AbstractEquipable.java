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
package org.lanternpowered.server.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.inventory.SimpleEquipmentInventory;
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.spongepowered.api.entity.Equipable;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

import java.util.Optional;

import javax.annotation.Nullable;

public interface AbstractEquipable extends Equipable {

    @Override
    default boolean canEquip(EquipmentType type) {
        return canEquip(type, null);
    }

    @Override
    default boolean canEquip(EquipmentType type, @Nullable ItemStack equipment) {
        checkNotNull(type, "type");
        final Inventory inventory = getInventory().query(type);
        if (inventory instanceof EmptyInventory) {
            return false;
        }
        final LanternSlot slot = inventory.first().<LanternSlot>slots().iterator().next();
        return slot != null && (equipment == null || slot.isValidItem(equipment));
    }

    @Override
    default Optional<ItemStack> getEquipped(EquipmentType type) {
        checkNotNull(type, "type");
        final Inventory inventory = getInventory().query(type);
        if (inventory instanceof EmptyInventory) {
            return Optional.empty();
        }
        return inventory.peek();
    }

    @Override
    default boolean equip(EquipmentType type, @Nullable ItemStack equipment) {
        checkNotNull(type, "type");
        final Inventory inventory = getInventory().query(type);
        if (inventory instanceof EmptyInventory) {
            return false;
        }
        final LanternSlot slot = inventory.first().<LanternSlot>slots().iterator().next();
        final InventoryTransactionResult result = slot.set(equipment);
        return result.getType().equals(InventoryTransactionResult.Type.SUCCESS);
    }
}
