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

import org.lanternpowered.server.inventory.equipment.LanternEquipmentType;
import org.lanternpowered.server.inventory.slot.LanternSlot;
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
     * @return removed ItemStack, per the semantics of {@link Inventory#poll()}
     */
    public Optional<ItemStack> poll(EquipmentSlotType equipmentType) {
        checkNotNull(equipmentType, "equipmentType");
        return this.poll(stack -> {
            final EquipmentType equipmentType1 = equipmentType.getValue();
            if (equipmentType1 == null) {
                return false;
            }
            final Optional<EquipmentSlotType> equipmentSlotType = stack.getProperty(EquipmentSlotType.class);
            if (equipmentSlotType.isPresent()) {
                final EquipmentType equipmentType2 = equipmentSlotType.get().getValue();
                return equipmentType2 != null && ((LanternEquipmentType) equipmentType1).isChild(equipmentType2);
            }
            return false;
        });
    }

    /**
     * Get and remove the items from the stack for the specified equipment type
     * in this Inventory.
     *
     * @see Inventory#poll()
     * @param equipmentType Type of equipment slot to query for
     * @param limit item limit
     * @return removed ItemStack, per the semantics of {@link Inventory#poll()}
     */
    public Optional<ItemStack> poll(EquipmentSlotType equipmentType, int limit) {
        checkNotNull(equipmentType, "equipmentType");
        return this.poll(limit, stack -> {
            final EquipmentType equipmentType1 = equipmentType.getValue();
            if (equipmentType1 == null) {
                return false;
            }
            final Optional<EquipmentSlotType> equipmentSlotType = stack.getProperty(EquipmentSlotType.class);
            if (equipmentSlotType.isPresent()) {
                final EquipmentType equipmentType2 = equipmentSlotType.get().getValue();
                return equipmentType2 != null && ((LanternEquipmentType) equipmentType1).isChild(equipmentType2);
            }
            return false;
        });
    }

    /**
     * Get and remove the stack for the specified equipment type in this
     * Inventory.
     *
     * @see Inventory#poll()
     * @param equipmentType Type of equipment slot to query for
     * @return removed ItemStack, per the semantics of {@link Inventory#poll()}
     */
    public Optional<ItemStack> poll(EquipmentType equipmentType) {
        checkNotNull(equipmentType, "equipmentType");
        return this.poll(stack -> {
            final Optional<EquipmentSlotType> equipmentSlotType = stack.getProperty(EquipmentSlotType.class);
            return equipmentSlotType.isPresent() && ((LanternEquipmentType) equipmentType).isChild(equipmentSlotType.get().getValue());
        });
    }

    /**
     * Get and remove the items from the stack for the specified equipment type
     * in this Inventory.
     *
     * @see Inventory#poll()
     * @param equipmentType Type of equipment slot to query for
     * @param limit item limit
     * @return removed ItemStack, per the semantics of {@link Inventory#poll()}
     */
    public Optional<ItemStack> poll(EquipmentType equipmentType, int limit) {
        checkNotNull(equipmentType, "equipmentType");
        return this.poll(limit, stack -> {
            final Optional<EquipmentSlotType> equipmentSlotType = stack.getProperty(EquipmentSlotType.class);
            return equipmentSlotType.isPresent() && ((LanternEquipmentType) equipmentType).isChild(equipmentSlotType.get().getValue());
        });
    }

    /**
     * Get without removing the stack for the specified equipment type in this
     * Inventory.
     *
     * @see Inventory#peek()
     * @param equipmentType Type of equipment slot to query for
     * @return removed ItemStack, per the semantics of {@link Inventory#peek()}
     */
    public Optional<ItemStack> peek(EquipmentSlotType equipmentType) {
        checkNotNull(equipmentType, "equipmentType");
        return this.peek(stack -> {
            final EquipmentType equipmentType1 = equipmentType.getValue();
            if (equipmentType1 == null) {
                return false;
            }
            final Optional<EquipmentSlotType> equipmentSlotType = stack.getProperty(EquipmentSlotType.class);
            if (equipmentSlotType.isPresent()) {
                final EquipmentType equipmentType2 = equipmentSlotType.get().getValue();
                return equipmentType2 != null && ((LanternEquipmentType) equipmentType1).isChild(equipmentType2);
            }
            return false;
        });
    }

    /**
     * Get without removing the items from the stack for the specified equipment
     * type in this Inventory.
     *
     * @see Inventory#peek()
     * @param equipmentType Type of equipment slot to query for
     * @param limit item limit
     * @return removed ItemStack, per the semantics of {@link Inventory#peek()}
     */
    public Optional<ItemStack> peek(EquipmentSlotType equipmentType, int limit) {
        checkNotNull(equipmentType, "equipmentType");
        return this.peek(limit, stack -> {
            final EquipmentType equipmentType1 = equipmentType.getValue();
            if (equipmentType1 == null) {
                return false;
            }
            final Optional<EquipmentSlotType> equipmentSlotType = stack.getProperty(EquipmentSlotType.class);
            if (equipmentSlotType.isPresent()) {
                final EquipmentType equipmentType2 = equipmentSlotType.get().getValue();
                return equipmentType2 != null && ((LanternEquipmentType) equipmentType1).isChild(equipmentType2);
            }
            return false;
        });
    }

    /**
     * Get without removing the stack for the specified equipment type in this
     * Inventory.
     *
     * @see Inventory#peek()
     * @param equipmentType Type of equipment slot to query for
     * @return removed ItemStack, per the semantics of {@link Inventory#peek()}
     */
    public Optional<ItemStack> peek(EquipmentType equipmentType) {
        checkNotNull(equipmentType, "equipmentType");
        return this.peek(stack -> {
            final Optional<EquipmentSlotType> equipmentSlotType = stack.getProperty(EquipmentSlotType.class);
            return equipmentSlotType.isPresent() && ((LanternEquipmentType) equipmentType).isChild(equipmentSlotType.get().getValue());
        });
    }

    /**
     * Get without removing the items from the stack for the specified equipment
     * type in this Inventory.
     *
     * @see Inventory#peek()
     * @param equipmentType Type of equipment slot to query for
     * @param limit item limit
     * @return removed ItemStack, per the semantics of {@link Inventory#peek()}
     */
    public Optional<ItemStack> peek(EquipmentType equipmentType, int limit) {
        checkNotNull(equipmentType, "equipmentType");
        return this.peek(limit, stack -> {
            final Optional<EquipmentSlotType> equipmentSlotType = stack.getProperty(EquipmentSlotType.class);
            return equipmentSlotType.isPresent() && ((LanternEquipmentType) equipmentType).isChild(equipmentSlotType.get().getValue());
        });
    }

    /**
     * Set the item for the specified equipment type.
     *
     * @see Inventory#set(ItemStack)
     * @param equipmentType Type of equipment slot to set
     * @param stack stack to insert
     * @return operation result, for details see {@link Inventory#set}
     */
    public InventoryTransactionResult set(EquipmentSlotType equipmentType, @Nullable ItemStack stack) {
        checkNotNull(equipmentType, "equipmentType");
        LanternSlot slot = null;
        if (equipmentType.getValue() != null) {
            slot = this.slots.stream().filter(s -> s instanceof EquipmentSlot &&
                    ((EquipmentSlot) s).isValidItem(equipmentType.getValue())).findFirst().orElse(null);
        }
        return slot == null ? InventoryTransactionResult.builder().type(InventoryTransactionResult.Type.FAILURE)
                .reject(stack).build() : slot.set(stack);
    }

    /**
     * Set the item for the specified equipment type.
     *
     * @see Inventory#set(ItemStack)
     * @param equipmentType Type of equipment slot to set
     * @param stack stack to insert
     * @return operation result, for details see {@link Inventory#set}
     */
    public InventoryTransactionResult set(EquipmentType equipmentType, @Nullable ItemStack stack) {
        checkNotNull(equipmentType, "equipmentType");
        LanternSlot slot = this.slots.stream().filter(s -> s instanceof EquipmentSlot &&
                    ((EquipmentSlot) s).isValidItem(equipmentType)).findFirst().orElse(null);
        return slot == null ? InventoryTransactionResult.builder().type(InventoryTransactionResult.Type.FAILURE)
                .reject(stack).build() : slot.set(stack);
    }

    /**
     * Get the {@link Slot} for the specified equipment type.
     *
     * @param equipmentType Type of equipment slot to set
     * @return matching slot or {@link Optional#empty()} if no matching slot
     */
    @SuppressWarnings("unchecked")
    public Optional<Slot> getSlot(EquipmentSlotType equipmentType) {
        checkNotNull(equipmentType, "equipmentType");
        if (equipmentType.getValue() != null) {
            return (Optional) this.slots.stream().filter(s -> s instanceof EquipmentSlot &&
                    ((EquipmentSlot) s).isValidItem(equipmentType.getValue())).findFirst();
        }
        return Optional.empty();
    }

    /**
     * Get the {@link Slot} for the specified equipment type.
     *
     * @param equipmentType Type of equipment slot to set
     * @return matching slot or {@link Optional#empty()} if no matching slot
     */
    @SuppressWarnings("unchecked")
    public Optional<Slot> getSlot(EquipmentType equipmentType) {
        checkNotNull(equipmentType, "equipmentType");
        return (Optional) this.slots.stream().filter(s -> s instanceof EquipmentSlot
                && ((EquipmentSlot) s).isValidItem(equipmentType)).findFirst();
    }
}
