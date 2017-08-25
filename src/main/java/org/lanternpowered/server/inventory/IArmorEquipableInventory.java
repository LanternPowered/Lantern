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

import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.equipment.EquipmentInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.property.EquipmentSlotType;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

import java.util.Optional;

import javax.annotation.Nullable;

public interface IArmorEquipableInventory extends IEquipmentInventory<ArmorEquipable>, EquipmentInventory {

    @Override
    default Optional<ItemStack> poll(EquipmentSlotType equipmentType) {
        return IEquipmentInventory.super.poll(equipmentType);
    }

    @Override
    default Optional<ItemStack> poll(EquipmentSlotType equipmentType, int limit) {
        return IEquipmentInventory.super.poll(equipmentType, limit);
    }

    @Override
    default Optional<ItemStack> poll(EquipmentType equipmentType) {
        return IEquipmentInventory.super.poll(equipmentType);
    }

    @Override
    default Optional<ItemStack> poll(EquipmentType equipmentType, int limit) {
        return IEquipmentInventory.super.poll(equipmentType, limit);
    }

    @Override
    default Optional<ItemStack> peek(EquipmentSlotType equipmentType) {
        return IEquipmentInventory.super.peek(equipmentType);
    }

    @Override
    default Optional<ItemStack> peek(EquipmentSlotType equipmentType, int limit) {
        return IEquipmentInventory.super.peek(equipmentType, limit);
    }

    @Override
    default Optional<ItemStack> peek(EquipmentType equipmentType) {
        return IEquipmentInventory.super.peek(equipmentType);
    }

    @Override
    default Optional<ItemStack> peek(EquipmentType equipmentType, int limit) {
        return IEquipmentInventory.super.peek(equipmentType, limit);
    }

    @Override
    default InventoryTransactionResult set(EquipmentSlotType equipmentType, @Nullable ItemStack stack) {
        return IEquipmentInventory.super.set(equipmentType, stack);
    }

    @Override
    default InventoryTransactionResult set(EquipmentType equipmentType, @Nullable ItemStack stack) {
        return IEquipmentInventory.super.set(equipmentType, stack);
    }

    @Override
    default Optional<Slot> getSlot(EquipmentSlotType equipmentType) {
        return IEquipmentInventory.super.getSlot(equipmentType);
    }

    @Override
    default Optional<Slot> getSlot(EquipmentType equipmentType) {
        return IEquipmentInventory.super.getSlot(equipmentType);
    }
}
