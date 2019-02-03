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

import org.lanternpowered.api.cause.CauseStack;
import org.spongepowered.api.data.property.Property;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.slot.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.ViewableInventory;
import org.spongepowered.api.text.translation.Translation;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public abstract class AbstractForwardingSlot extends AbstractSlot {

    /**
     * Gets the delegate slot.
     *
     * @return The inventory slot
     */
    protected abstract AbstractSlot getDelegateSlot();

    @Override
    void close(CauseStack causeStack) {
        super.close(causeStack);
        // Handle the listeners of both the slots
        getDelegateSlot().close(causeStack);
    }

    @Override
    protected <V> Optional<V> tryGetProperty(Property<V> property) {
        final Optional<V> optValue = super.tryGetProperty(property);
        if (optValue.isPresent()) {
            return optValue;
        }
        return getDelegateSlot().tryGetProperty(property);
    }

    @Override
    protected <V> Optional<V> tryGetProperty(Inventory child, Property<V> property) {
        final Optional<V> optValue = super.tryGetProperty(child, property);
        if (optValue.isPresent()) {
            return optValue;
        }
        return getDelegateSlot().tryGetProperty(child, property);
    }

    // Slot listeners should only be added directly to the inventory slot,
    // container slots are temporarily and listeners cannot be cleanup up

    @Override
    public void addChangeListener(SlotChangeListener listener) {
        throw new IllegalStateException("Cannot add a SlotChangeListener to a ContainerSlot");
    }

    // Delegate all the other methods

    @Override
    public Translation getName() {
        return getDelegateSlot().getName();
    }

    @Override
    protected void setCarrier(Carrier carrier, boolean override) {
        getDelegateSlot().setCarrier(carrier, override);
    }

    @Override
    protected List<AbstractSlot> getSlots() {
        return getDelegateSlot().getSlots();
    }

    @Override
    protected List<? extends AbstractInventory> getChildren() {
        return getDelegateSlot().getChildren();
    }

    @Override
    protected void peekOffer(ItemStack stack, @Nullable Consumer<SlotTransaction> transactionAdder) {
        getDelegateSlot().peekOffer(stack, transactionAdder);
    }

    @Override
    protected void offer(ItemStack stack, @Nullable Consumer<SlotTransaction> transactionAdder) {
        getDelegateSlot().offer(stack, transactionAdder);
    }

    @Override
    protected void set(ItemStack stack, boolean force, @Nullable Consumer<SlotTransaction> transactionAdder) {
        getDelegateSlot().set(stack, force, transactionAdder);
    }

    @Override
    protected void queryInventories(QueryInventoryAdder adder) {
        getDelegateSlot().queryInventories(adder);
    }

    @Override
    public boolean isValidItem(ItemStackSnapshot stack) {
        return getDelegateSlot().isValidItem(stack);
    }

    @Override
    public boolean isValidItem(ItemType type) {
        return getDelegateSlot().isValidItem(type);
    }

    @Override
    public boolean isValidItem(EquipmentType type) {
        return getDelegateSlot().isValidItem(type);
    }

    @Override
    public LanternItemStack poll(Predicate<ItemStack> matcher) {
        return getDelegateSlot().poll(matcher);
    }

    @Override
    public LanternItemStack poll(int limit, Predicate<ItemStack> matcher) {
        return getDelegateSlot().poll(limit, matcher);
    }

    @Override
    public LanternItemStack peek(Predicate<ItemStack> matcher) {
        return getDelegateSlot().peek(matcher);
    }

    @Override
    public LanternItemStack peek(int limit, Predicate<ItemStack> matcher) {
        return getDelegateSlot().peek(limit, matcher);
    }

    @Override
    public PeekedPollTransactionResult peekPoll(Predicate<ItemStack> matcher) {
        return getDelegateSlot().peekPoll(matcher);
    }

    @Override
    public PeekedPollTransactionResult peekPoll(int limit, Predicate<ItemStack> matcher) {
        return getDelegateSlot().peekPoll(limit, matcher);
    }

    @Override
    public PeekedOfferTransactionResult peekOffer(ItemStack itemStack) {
        return getDelegateSlot().peekOffer(itemStack);
    }

    @Override
    public PeekedSetTransactionResult peekSet(ItemStack itemStack) {
        return getDelegateSlot().peekSet(itemStack);
    }

    @Override
    public boolean isValidItem(ItemStack stack) {
        return getDelegateSlot().isValidItem(stack);
    }

    @Override
    public int getStackSize() {
        return getDelegateSlot().getStackSize();
    }

    @Override
    public boolean canFit(ItemStack stack) {
        return getDelegateSlot().canFit(stack);
    }

    @Override
    public InventoryTransactionResult set(ItemStack stack) {
        return getDelegateSlot().set(stack);
    }

    @Override
    public InventoryTransactionResult set(ItemStack stack, boolean force) {
        return getDelegateSlot().set(stack, force);
    }

    @Override
    public Optional<ItemStack> poll(SlotIndex index) {
        return getDelegateSlot().poll(index);
    }

    @Override
    public Optional<ItemStack> poll(SlotIndex index, int limit) {
        return getDelegateSlot().poll(index, limit);
    }

    @Override
    public Optional<ItemStack> peek(SlotIndex index) {
        return getDelegateSlot().peek(index);
    }

    @Override
    public Optional<ItemStack> peek(SlotIndex index, int limit) {
        return getDelegateSlot().peek(index, limit);
    }

    @Override
    public InventoryTransactionResult set(SlotIndex index, ItemStack stack) {
        return getDelegateSlot().set(index, stack);
    }

    @Override
    public Optional<Slot> getSlot(SlotIndex index) {
        return getDelegateSlot().getSlot(index);
    }

    @Override
    public InventoryTransactionResult setForced(ItemStack stack) {
        return getDelegateSlot().setForced(stack);
    }

    @Override
    public Optional<ISlot> getSlot(int index) {
        return getDelegateSlot().getSlot(index);
    }

    @Override
    public int getSlotIndex(Slot slot) {
        return getDelegateSlot().getSlotIndex(slot);
    }

    @Override
    public void clear() {
        getDelegateSlot().clear();
    }

    @Override
    public int size() {
        return getDelegateSlot().size();
    }

    @Override
    public int totalItems() {
        return getDelegateSlot().totalItems();
    }

    @Override
    public int capacity() {
        return getDelegateSlot().capacity();
    }

    @Override
    public boolean hasChildren() {
        return getDelegateSlot().hasChildren();
    }

    @Override
    public boolean contains(ItemStack stack) {
        return getDelegateSlot().contains(stack);
    }

    @Override
    public boolean contains(ItemType type) {
        return getDelegateSlot().contains(type);
    }

    @Override
    public boolean containsAny(ItemStack stack) {
        return getDelegateSlot().containsAny(stack);
    }

    @Override
    public int getMaxStackSize() {
        return getDelegateSlot().getMaxStackSize();
    }

    @Override
    public void setMaxStackSize(int size) {
        getDelegateSlot().setMaxStackSize(size);
    }

    @Override
    public <T extends Inventory> Optional<T> query(Class<T> inventoryType) {
        if (inventoryType.isInstance(this)) {
            return Optional.of((T) this);
        }
        return getDelegateSlot().query(inventoryType);
    }

    @Override
    public boolean containsInventory(Inventory inventory) {
        return inventory == this || getDelegateSlot().containsInventory(inventory);
    }

    @Nullable
    @Override
    protected ViewableInventory toViewable() {
        return getDelegateSlot().toViewable();
    }

    @Override
    public LanternItemStack getRawItemStack() {
        return getDelegateSlot().getRawItemStack();
    }

    @Override
    public void setRawItemStack(ItemStack itemStack) {
        getDelegateSlot().setRawItemStack(itemStack);
    }

    @Override
    public void addTracker(SlotChangeTracker tracker) {
        getDelegateSlot().addTracker(tracker);
    }

    @Override
    public void removeTracker(SlotChangeTracker tracker) {
        getDelegateSlot().removeTracker(tracker);
    }

    @Override
    public int hashCode() {
        return getDelegateSlot().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractSlot)) {
            return false;
        }
        final AbstractSlot slot = unwrap((AbstractSlot) obj);
        return slot.equals(unwrap(getDelegateSlot()));
    }

    private static AbstractSlot unwrap(AbstractSlot slot) {
        while (slot instanceof AbstractForwardingSlot) {
            slot = ((AbstractForwardingSlot) slot).getDelegateSlot();
        }
        return slot;
    }
}
