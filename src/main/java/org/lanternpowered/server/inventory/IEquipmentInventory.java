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

import org.spongepowered.api.entity.Equipable;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.equipment.EquipmentInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

import java.util.Optional;

public interface IEquipmentInventory extends ICarriedInventory<Equipable>, EquipmentInventory {

    @Override
    default Optional<ItemStack> poll(EquipmentType equipmentType) {
        return getSlot(equipmentType).map(Inventory::poll);
    }

    @Override
    default Optional<ItemStack> poll(EquipmentType equipmentType, int limit) {
        return getSlot(equipmentType).map(slot -> slot.poll(limit));
    }

    @Override
    default Optional<ItemStack> peek(EquipmentType equipmentType) {
        return getSlot(equipmentType).map(Inventory::peek);
    }

    @Override
    default Optional<ItemStack> peek(EquipmentType equipmentType, int limit) {
        return getSlot(equipmentType).map(slot -> slot.peek(limit));
    }

    @Override
    default InventoryTransactionResult set(EquipmentType equipmentType, ItemStack stack) {
        checkNotNull(equipmentType, "equipmentType");
        return getSlot(equipmentType).map(slot -> slot.set(stack))
                .orElseGet(() -> InventoryTransactionResult.builder()
                        .type(InventoryTransactionResult.Type.FAILURE)
                        .reject(LanternItemStack.toSnapshot(stack).createStack())
                        .build());
    }

    @Override
    default Optional<Slot> getSlot(EquipmentType equipmentType) {
        checkNotNull(equipmentType, "equipmentType");
        return this.slots().stream().filter(s -> ((AbstractSlot) s).isValidItem(equipmentType)).findFirst();
    }
}
