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

import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.property.EquipmentSlotType;
import org.spongepowered.api.item.inventory.slot.EquipmentSlot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.text.translation.Translation;

import java.util.Optional;

import javax.annotation.Nullable;

public class SimpleEquipmentInventory extends LanternOrderedInventory {

    public SimpleEquipmentInventory(@Nullable Inventory parent) {
        this(parent, null);
    }

    public SimpleEquipmentInventory(@Nullable Inventory parent, @Nullable Translation name) {
        super(parent, name);
    }

    /**
     * Get and remove the stack for the specified equipment type in this
     * Inventory.
     *
     * @see Inventory#poll()
     * @param equipmentType Type of equipment slot to query for
     * @return The removed ItemStack, per the semantics of {@link Inventory#poll()}
     */
    public Optional<ItemStack> poll(EquipmentSlotType equipmentType) {
        checkNotNull(equipmentType, "equipmentType");
        return getSlot(equipmentType).map(Inventory::poll).orElseGet(Optional::empty);
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
    public Optional<ItemStack> poll(EquipmentSlotType equipmentType, int limit) {
        checkNotNull(equipmentType, "equipmentType");
        return getSlot(equipmentType).map(slot -> slot.poll(limit)).orElseGet(Optional::empty);
    }

    /**
     * Get and remove the stack for the specified equipment type in this
     * Inventory.
     *
     * @see Inventory#poll()
     * @param equipmentType Type of equipment slot to query for
     * @return The removed ItemStack, per the semantics of {@link Inventory#poll()}
     */
    public Optional<ItemStack> poll(EquipmentType equipmentType) {
        checkNotNull(equipmentType, "equipmentType");
        return getSlot(equipmentType).map(Inventory::poll).orElseGet(Optional::empty);
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
    public Optional<ItemStack> poll(EquipmentType equipmentType, int limit) {
        checkNotNull(equipmentType, "equipmentType");
        return getSlot(equipmentType).map(slot -> slot.poll(limit)).orElseGet(Optional::empty);
    }

    /**
     * Get without removing the stack for the specified equipment type in this
     * Inventory.
     *
     * @see Inventory#peek()
     * @param equipmentType Type of equipment slot to query for
     * @return The removed ItemStack, per the semantics of {@link Inventory#peek()}
     */
    public Optional<ItemStack> peek(EquipmentSlotType equipmentType) {
        checkNotNull(equipmentType, "equipmentType");
        return getSlot(equipmentType).map(Inventory::peek).orElseGet(Optional::empty);
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
    public Optional<ItemStack> peek(EquipmentSlotType equipmentType, int limit) {
        checkNotNull(equipmentType, "equipmentType");
        return getSlot(equipmentType).map(slot -> slot.peek(limit)).orElseGet(Optional::empty);
    }

    /**
     * Get without removing the stack for the specified equipment type in this
     * Inventory.
     *
     * @see Inventory#peek()
     * @param equipmentType Type of equipment slot to query for
     * @return The removed ItemStack, per the semantics of {@link Inventory#peek()}
     */
    public Optional<ItemStack> peek(EquipmentType equipmentType) {
        checkNotNull(equipmentType, "equipmentType");
        return getSlot(equipmentType).map(Inventory::peek).orElseGet(Optional::empty);
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
    public Optional<ItemStack> peek(EquipmentType equipmentType, int limit) {
        checkNotNull(equipmentType, "equipmentType");
        //noinspection ConstantConditions
        return getSlot(equipmentType).map(slot -> slot.peek(limit)).orElseGet(Optional::empty);
    }

    /**
     * Set the item for the specified equipment type.
     *
     * @see Inventory#set(ItemStack)
     * @param equipmentType Type of equipment slot to set
     * @param stack The stack to insert
     * @return The operation result, for details see {@link Inventory#set}
     */
    public InventoryTransactionResult set(EquipmentSlotType equipmentType, @Nullable ItemStack stack) {
        checkNotNull(equipmentType, "equipmentType");
        //noinspection ConstantConditions
        return getSlot(equipmentType).map(slot -> slot.set(stack)).orElseGet(
                () -> InventoryTransactionResult.builder()
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
    public InventoryTransactionResult set(EquipmentType equipmentType, @Nullable ItemStack stack) {
        checkNotNull(equipmentType, "equipmentType");
        //noinspection ConstantConditions
        return getSlot(equipmentType).map(slot -> slot.set(stack)).orElseGet(
                () -> InventoryTransactionResult.builder()
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
    public Optional<Slot> getSlot(EquipmentSlotType equipmentType) {
        checkNotNull(equipmentType, "equipmentType");
        //noinspection ConstantConditions
        return equipmentType.getValue() == null ? Optional.empty() :
                (Optional) (this.slots.stream().filter(s -> s instanceof EquipmentSlot &&
                        ((EquipmentSlot) s).isValidItem(equipmentType.getValue())).findFirst());
    }

    /**
     * Get the {@link Slot} for the specified equipment type.
     *
     * @param equipmentType Type of equipment slot to set
     * @return The matching slot or {@link Optional#empty()} if no matching slot
     */
    @SuppressWarnings("unchecked")
    public Optional<Slot> getSlot(EquipmentType equipmentType) {
        checkNotNull(equipmentType, "equipmentType");
        //noinspection ConstantConditions
        return (Optional) (this.slots.stream().filter(s -> s instanceof EquipmentSlot &&
                        ((EquipmentSlot) s).isValidItem(equipmentType)).findFirst());
    }
}
