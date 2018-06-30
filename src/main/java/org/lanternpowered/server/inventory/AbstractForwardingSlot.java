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

import org.lanternpowered.server.event.CauseStack;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.text.translation.Translation;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

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
    protected List<AbstractSlot> getSlotInventories() {
        return getForwardingSlot().getSlotInventories();
    }

    @Override
    protected FastOfferResult offerFast(ItemStack stack) {
        return getForwardingSlot().offerFast(stack);
    }

    @Override
    protected <T extends Inventory> T queryInventories(Predicate<AbstractMutableInventory> predicate) {
        return getForwardingSlot().queryInventories(predicate);
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
    public Optional<ItemStack> poll(Predicate<ItemStack> matcher) {
        return getForwardingSlot().poll(matcher);
    }

    @Override
    public Optional<ItemStack> poll(int limit, Predicate<ItemStack> matcher) {
        return getForwardingSlot().poll(limit, matcher);
    }

    @Override
    public Optional<ItemStack> peek(Predicate<ItemStack> matcher) {
        return getForwardingSlot().peek(matcher);
    }

    @Override
    public Optional<ItemStack> peek(int limit, Predicate<ItemStack> matcher) {
        return getForwardingSlot().peek(limit, matcher);
    }

    @Override
    public Optional<PeekedPollTransactionResult> peekPoll(Predicate<ItemStack> matcher) {
        return getForwardingSlot().peekPoll(matcher);
    }

    @Override
    public Optional<PeekedPollTransactionResult> peekPoll(int limit, Predicate<ItemStack> matcher) {
        return getForwardingSlot().peekPoll(limit, matcher);
    }

    @Override
    public PeekedOfferTransactionResult peekOffer(ItemStack itemStack) {
        return getForwardingSlot().peekOffer(itemStack);
    }

    @Override
    public PeekedSetTransactionResult peekSet(@Nullable ItemStack itemStack) {
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
    public InventoryTransactionResult set(@Nullable ItemStack stack) {
        return getForwardingSlot().set(stack);
    }

    @Override
    public InventoryTransactionResult setForced(@Nullable ItemStack stack) {
        return getForwardingSlot().setForced(stack);
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
    public boolean containsInventory(Inventory inventory) {
        return inventory == this || getForwardingSlot().containsInventory(inventory);
    }

    @Override
    public Iterator<Inventory> iterator() {
        return getForwardingSlot().iterator();
    }

    @Nullable
    @Override
    public LanternItemStack getRawItemStack() {
        return getForwardingSlot().getRawItemStack();
    }

    @Override
    public void setRawItemStack(@Nullable ItemStack itemStack) {
        getForwardingSlot().setRawItemStack(itemStack);
    }
}
