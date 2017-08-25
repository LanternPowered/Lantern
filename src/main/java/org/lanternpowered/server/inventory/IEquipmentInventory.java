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

import com.google.common.collect.Streams;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.property.EquipmentSlotType;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import java.util.Optional;

import javax.annotation.Nullable;

public interface IEquipmentInventory<C extends Carrier> extends IInventory, CarriedInventory<C> {

    /**
     * Get and remove the stack for the specified equipment type in this
     * Inventory.
     *
     * @see Inventory#poll()
     * @param equipmentType Type of equipment slot to query for
     * @return The removed ItemStack, per the semantics of {@link Inventory#poll()}
     */
    default Optional<ItemStack> poll(EquipmentSlotType equipmentType) {
        return getSlot(equipmentType).flatMap(Inventory::poll);
    }

    /**
     * Get and remove the items from the stack for the specified equipment type
     * in this Inventory.
     *
     * @see Inventory#poll()
     * @param equipmentType Type of equipment slot to query for
     * @param limit The item limit
     * @return The removed ItemStack, per the semantics of {@link Inventory#poll()}
     */
    default Optional<ItemStack> poll(EquipmentSlotType equipmentType, int limit) {
        return getSlot(equipmentType).flatMap(slot -> slot.poll(limit));
    }

    /**
     * Get and remove the stack for the specified equipment type in this
     * Inventory.
     *
     * @see Inventory#poll()
     * @param equipmentType Type of equipment slot to query for
     * @return The removed ItemStack, per the semantics of {@link Inventory#poll()}
     */
    default Optional<ItemStack> poll(EquipmentType equipmentType) {
        return getSlot(equipmentType).flatMap(Inventory::poll);
    }

    /**
     * Get and remove the items from the stack for the specified equipment type
     * in this Inventory.
     *
     * @see Inventory#poll()
     * @param equipmentType Type of equipment slot to query for
     * @param limit The item limit
     * @return The removed ItemStack, per the semantics of {@link Inventory#poll()}
     */
    default Optional<ItemStack> poll(EquipmentType equipmentType, int limit) {
        return getSlot(equipmentType).flatMap(slot -> slot.poll(limit));
    }

    /**
     * Get without removing the stack for the specified equipment type in this
     * Inventory.
     *
     * @see Inventory#peek()
     * @param equipmentType Type of equipment slot to query for
     * @return The removed ItemStack, per the semantics of {@link Inventory#peek()}
     */
    default Optional<ItemStack> peek(EquipmentSlotType equipmentType) {
        return getSlot(equipmentType).flatMap(Inventory::peek);
    }

    /**
     * Get without removing the items from the stack for the specified equipment
     * type in this Inventory.
     *
     * @see Inventory#peek()
     * @param equipmentType Type of equipment slot to query for
     * @param limit The item limit
     * @return The removed ItemStack, per the semantics of {@link Inventory#peek()}
     */
    default Optional<ItemStack> peek(EquipmentSlotType equipmentType, int limit) {
        return getSlot(equipmentType).flatMap(slot -> slot.peek(limit));
    }

    /**
     * Get without removing the stack for the specified equipment type in this
     * Inventory.
     *
     * @see Inventory#peek()
     * @param equipmentType Type of equipment slot to query for
     * @return The removed ItemStack, per the semantics of {@link Inventory#peek()}
     */
    default Optional<ItemStack> peek(EquipmentType equipmentType) {
        return getSlot(equipmentType).flatMap(Inventory::peek);
    }

    /**
     * Get without removing the items from the stack for the specified equipment
     * type in this Inventory.
     *
     * @see Inventory#peek()
     * @param equipmentType Type of equipment slot to query for
     * @param limit The item limit
     * @return The removed ItemStack, per the semantics of {@link Inventory#peek()}
     */
    default Optional<ItemStack> peek(EquipmentType equipmentType, int limit) {
        return getSlot(equipmentType).flatMap(slot -> slot.peek(limit));
    }

    /**
     * Set the item for the specified equipment type.
     *
     * @see Inventory#set(ItemStack)
     * @param equipmentType Type of equipment slot to set
     * @param stack The stack to insert
     * @return The operation result, for details see {@link Inventory#set}
     */
    default InventoryTransactionResult set(EquipmentSlotType equipmentType, @Nullable ItemStack stack) {
        checkNotNull(equipmentType, "equipmentType");
        //noinspection ConstantConditions
        return getSlot(equipmentType).map(slot -> slot.set(stack))
                .orElseGet(() -> InventoryTransactionResult.builder()
                        .type(InventoryTransactionResult.Type.FAILURE)
                        .reject(LanternItemStack.toSnapshot(stack).createStack())
                        .build());
    }

    /**
     * Set the item for the specified equipment type.
     *
     * @see Inventory#set(ItemStack)
     * @param equipmentType Type of equipment slot to set
     * @param stack The stack to insert
     * @return The operation result, for details see {@link Inventory#set}
     */
    default InventoryTransactionResult set(EquipmentType equipmentType, @Nullable ItemStack stack) {
        checkNotNull(equipmentType, "equipmentType");
        //noinspection ConstantConditions
        return getSlot(equipmentType).map(slot -> slot.set(stack))
                .orElseGet(() -> InventoryTransactionResult.builder()
                        .type(InventoryTransactionResult.Type.FAILURE)
                        .reject(LanternItemStack.toSnapshot(stack).createStack())
                        .build());
    }

    /**
     * Get the {@link Slot} for the specified equipment type.
     *
     * @param equipmentType Type of equipment slot to set
     * @return The matching slot or {@link Optional#empty()} if no matching slot
     */
    @SuppressWarnings("unchecked")
    default Optional<Slot> getSlot(EquipmentSlotType equipmentType) {
        checkNotNull(equipmentType, "equipmentType");
        if (equipmentType.getValue() == null || equipmentType.getOperator() != Property.Operator.EQUAL) {
            return Optional.empty();
        }
        return getSlot(equipmentType.getValue());
    }

    /**
     * Get the {@link Slot} for the specified equipment type.
     *
     * @param equipmentType Type of equipment slot to set
     * @return The matching slot or {@link Optional#empty()} if no matching slot
     */
    @SuppressWarnings("unchecked")
    default Optional<Slot> getSlot(EquipmentType equipmentType) {
        checkNotNull(equipmentType, "equipmentType");
        return (Optional) Streams.stream(this.<AbstractSlot>slots()).filter(s -> s.isValidItem(equipmentType)).findFirst();
    }
}
