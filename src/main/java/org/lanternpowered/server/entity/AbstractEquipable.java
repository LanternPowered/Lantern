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
package org.lanternpowered.server.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.IEquipmentInventory;
import org.lanternpowered.server.inventory.IQueryInventory;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Equipable;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.query.QueryOperation;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

import java.util.Optional;

@SuppressWarnings("unchecked")
public interface AbstractEquipable extends Equipable {

    class Holder {

        private static final QueryOperation<?> EQUIPMENT_INVENTORY_OPERATION =
                QueryOperationTypes.INVENTORY_TYPE.of(IEquipmentInventory.class);
    }

    @Override
    default boolean canEquip(EquipmentType type) {
        return canEquip(type, LanternItemStack.empty());
    }

    @Override
    default boolean canEquip(EquipmentType type, ItemStack equipment) {
        checkNotNull(type, "type");
        final IQueryInventory inventory = (IQueryInventory) getInventory().query(Holder.EQUIPMENT_INVENTORY_OPERATION);
        if (inventory instanceof EmptyInventory) {
            return false;
        }
        final AbstractSlot slot = (AbstractSlot) ((IEquipmentInventory) inventory.first()).getSlot(type).orElse(null);
        return slot != null && slot.isValidItem(equipment);
    }

    @Override
    default Optional<ItemStack> getEquipped(EquipmentType type) {
        checkNotNull(type, "type");
        final Inventory inventory = getInventory().query(Holder.EQUIPMENT_INVENTORY_OPERATION);
        if (inventory instanceof EmptyInventory) {
            return Optional.empty();
        }
        return Optional.of(inventory.peek());
    }

    @Override
    default boolean equip(EquipmentType type, ItemStack equipment) {
        checkNotNull(type, "type");
        final IQueryInventory inventory = (IQueryInventory) getInventory().query(Holder.EQUIPMENT_INVENTORY_OPERATION);
        if (inventory instanceof EmptyInventory) {
            return false;
        }
        final AbstractSlot slot = (AbstractSlot) ((IEquipmentInventory) inventory.first()).getSlot(type).orElse(null);
        if (slot == null) {
            return false;
        }
        final InventoryTransactionResult result = slot.set(equipment);
        return result.getType().equals(InventoryTransactionResult.Type.SUCCESS);
    }
}
