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
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.property.SlotIndex;
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
     * Gets the forwarding slot.
     *
     * @return The inventory slot
     */
    protected abstract AbstractSlot getForwardingSlot();

    @Override
    void close(CauseStack causeStack) {
        super.close(causeStack);
        // Handle the listeners of both the slots
        getForwardingSlot().close(causeStack);
    }

    @Override
    protected <T extends InventoryProperty<?, ?>> Optional<T> tryGetProperty(Class<T> property, @Nullable Object key) {
        return getForwardingSlot().tryGetProperty(property, key);
    }

    @Override
    protected <T extends InventoryProperty<?, ?>> List<T> tryGetProperties(Class<T> property) {
        return getForwardingSlot().tryGetProperties(property);
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
        return getForwardingSlot().getName();
    }

    @Override
    protected void setCarrier(Carrier carrier) {
        getForwardingSlot().setCarrier(carrier);
    }

    @Override
    protected List<AbstractSlot> getSlots() {
        return getForwardingSlot().getSlots();
    }

    @Override
    protected List<? extends AbstractInventory> getChildren() {
        return getForwardingSlot().getChildren();
    }

    @Override
    protected void offer(ItemStack stack, @Nullable Consumer<SlotTransaction> transactionAdder) {
        getForwardingSlot().offer(stack, transactionAdder);
    }

    @Override
    protected void set(ItemStack stack, boolean force, @Nullable Consumer<SlotTransaction> transactionAdder) {
        getForwardingSlot().set(stack, force, transactionAdder);
    }

    @Override
    protected void queryInventories(QueryInventoryAdder adder) {
        getForwardingSlot().queryInventories(adder);
    }

    @Override
    public boolean isValidItem(ItemStackSnapshot stack) {
        return getForwardingSlot().isValidItem(stack);
    }

    @Override
    public boolean isValidItem(ItemType type) {
        return getForwardingSlot().isValidItem(type);
    }

    @Override
    public boolean isValidItem(EquipmentType type) {
        return getForwardingSlot().isValidItem(type);
    }

    @Override
    public LanternItemStack poll(Predicate<ItemStack> matcher) {
        return getForwardingSlot().poll(matcher);
    }

    @Override
    public LanternItemStack poll(int limit, Predicate<ItemStack> matcher) {
        return getForwardingSlot().poll(limit, matcher);
    }

    @Override
    public LanternItemStack peek(Predicate<ItemStack> matcher) {
        return getForwardingSlot().peek(matcher);
    }

    @Override
    public LanternItemStack peek(int limit, Predicate<ItemStack> matcher) {
        return getForwardingSlot().peek(limit, matcher);
    }

    @Override
    public PeekedPollTransactionResult peekPoll(Predicate<ItemStack> matcher) {
        return getForwardingSlot().peekPoll(matcher);
    }

    @Override
    public PeekedPollTransactionResult peekPoll(int limit, Predicate<ItemStack> matcher) {
        return getForwardingSlot().peekPoll(limit, matcher);
    }

    @Override
    public PeekedOfferTransactionResult peekOffer(ItemStack itemStack) {
        return getForwardingSlot().peekOffer(itemStack);
    }

    @Override
    public PeekedSetTransactionResult peekSet(ItemStack itemStack) {
        return getForwardingSlot().peekSet(itemStack);
    }

    @Override
    public boolean isValidItem(ItemStack stack) {
        return getForwardingSlot().isValidItem(stack);
    }

    @Override
    public int getStackSize() {
        return getForwardingSlot().getStackSize();
    }

    @Override
    public InventoryTransactionResult set(ItemStack stack) {
        return getForwardingSlot().set(stack);
    }

    @Override
    public InventoryTransactionResult set(ItemStack stack, boolean force) {
        return getForwardingSlot().set(stack, force);
    }

    @Override
    public Optional<ItemStack> poll(SlotIndex index) {
        return getForwardingSlot().poll(index);
    }

    @Override
    public Optional<ItemStack> poll(SlotIndex index, int limit) {
        return getForwardingSlot().poll(index, limit);
    }

    @Override
    public Optional<ItemStack> peek(SlotIndex index) {
        return getForwardingSlot().peek(index);
    }

    @Override
    public Optional<ItemStack> peek(SlotIndex index, int limit) {
        return getForwardingSlot().peek(index, limit);
    }

    @Override
    public InventoryTransactionResult set(SlotIndex index, ItemStack stack) {
        return getForwardingSlot().set(index, stack);
    }

    @Override
    public Optional<Slot> getSlot(SlotIndex index) {
        return getForwardingSlot().getSlot(index);
    }

    @Override
    public InventoryTransactionResult setForced(ItemStack stack) {
        return getForwardingSlot().setForced(stack);
    }

    @Override
    public Optional<ISlot> getSlot(int index) {
        return getForwardingSlot().getSlot(index);
    }

    @Override
    public int getSlotIndex(Slot slot) {
        return getForwardingSlot().getSlotIndex(slot);
    }

    @Override
    public void clear() {
        getForwardingSlot().clear();
    }

    @Override
    public int size() {
        return getForwardingSlot().size();
    }

    @Override
    public int totalItems() {
        return getForwardingSlot().totalItems();
    }

    @Override
    public int capacity() {
        return getForwardingSlot().capacity();
    }

    @Override
    public boolean hasChildren() {
        return getForwardingSlot().hasChildren();
    }

    @Override
    public boolean contains(ItemStack stack) {
        return getForwardingSlot().contains(stack);
    }

    @Override
    public boolean contains(ItemType type) {
        return getForwardingSlot().contains(type);
    }

    @Override
    public boolean containsAny(ItemStack stack) {
        return getForwardingSlot().containsAny(stack);
    }

    @Override
    public int getMaxStackSize() {
        return getForwardingSlot().getMaxStackSize();
    }

    @Override
    public void setMaxStackSize(int size) {
        getForwardingSlot().setMaxStackSize(size);
    }

    @Override
    public <T extends Inventory> Optional<T> query(Class<T> inventoryType) {
        if (inventoryType.isInstance(this)) {
            return Optional.of((T) this);
        }
        return getForwardingSlot().query(inventoryType);
    }

    @Override
    public boolean containsInventory(Inventory inventory) {
        return inventory == this || getForwardingSlot().containsInventory(inventory);
    }

    @Nullable
    @Override
    protected ViewableInventory toViewable() {
        return getForwardingSlot().toViewable();
    }

    @Override
    public LanternItemStack getRawItemStack() {
        return getForwardingSlot().getRawItemStack();
    }

    @Override
    public void setRawItemStack(ItemStack itemStack) {
        getForwardingSlot().setRawItemStack(itemStack);
    }

    @Override
    public void addTracker(SlotChangeTracker tracker) {
        getForwardingSlot().addTracker(tracker);
    }

    @Override
    public void removeTracker(SlotChangeTracker tracker) {
        getForwardingSlot().removeTracker(tracker);
    }

    @Override
    public int hashCode() {
        return getForwardingSlot().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractSlot)) {
            return false;
        }
        final AbstractSlot slot = unwrap((AbstractSlot) obj);
        return slot.equals(unwrap(getForwardingSlot()));
    }

    private static AbstractSlot unwrap(AbstractSlot slot) {
        while (slot instanceof AbstractForwardingSlot) {
            slot = ((AbstractForwardingSlot) slot).getForwardingSlot();
        }
        return slot;
    }
}
