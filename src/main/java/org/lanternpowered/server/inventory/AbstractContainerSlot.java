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
import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.event.CauseStack;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.text.translation.Translation;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

public abstract class AbstractContainerSlot extends AbstractSlot {

    @Nullable AbstractInventorySlot slot;

    /**
     * Gets the {@link AbstractInventorySlot}.
     *
     * @return The inventory slot
     */
    protected AbstractInventorySlot getInventorySlot() {
        checkState(this.slot != null, "The inventory slot is not initialized yet.");
        return this.slot;
    }

    @Override
    public Slot transform(Type type) {
        checkNotNull(type, "type");
        if (type == Type.INVENTORY) {
            return getInventorySlot();
        }
        throw new IllegalStateException("Unsupported transform type: " + type);
    }

    @Override
    void close(CauseStack causeStack) {
        super.close(causeStack);
        // Handle the listeners of both the slots
        getInventorySlot().close(causeStack);
    }

    @Override
    protected <T extends InventoryProperty<?, ?>> Optional<T> tryGetProperty(Class<T> property, @Nullable Object key) {
        return getInventorySlot().tryGetProperty(property, key);
    }

    @Override
    protected <T extends InventoryProperty<?, ?>> List<T> tryGetProperties(Class<T> property) {
        return getInventorySlot().tryGetProperties(property);
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
        return getInventorySlot().getName();
    }

    @Override
    protected void setCarrier(Carrier carrier) {
        getInventorySlot().setCarrier(carrier);
    }

    @Override
    protected List<AbstractSlot> getSlotInventories() {
        return getInventorySlot().getSlotInventories();
    }

    @Override
    protected FastOfferResult offerFast(ItemStack stack) {
        return getInventorySlot().offerFast(stack);
    }

    @Override
    protected <T extends Inventory> T queryInventories(Predicate<AbstractMutableInventory> predicate) {
        return getInventorySlot().queryInventories(predicate);
    }

    @Override
    protected List<AbstractSlot> getIndexedSlotInventories() {
        return getInventorySlot().getIndexedSlotInventories();
    }

    @Override
    public boolean isValidItem(ItemType type) {
        return getInventorySlot().isValidItem(type);
    }

    @Override
    public boolean isValidItem(EquipmentType type) {
        return getInventorySlot().isValidItem(type);
    }

    @Override
    public Optional<ItemStack> poll(Predicate<ItemStack> matcher) {
        return getInventorySlot().poll(matcher);
    }

    @Override
    public Optional<ItemStack> poll(int limit, Predicate<ItemStack> matcher) {
        return getInventorySlot().poll(limit, matcher);
    }

    @Override
    public Optional<ItemStack> peek(Predicate<ItemStack> matcher) {
        return getInventorySlot().peek(matcher);
    }

    @Override
    public Optional<ItemStack> peek(int limit, Predicate<ItemStack> matcher) {
        return getInventorySlot().peek(limit, matcher);
    }

    @Override
    public Optional<PeekedPollTransactionResult> peekPoll(Predicate<ItemStack> matcher) {
        return getInventorySlot().peekPoll(matcher);
    }

    @Override
    public Optional<PeekedPollTransactionResult> peekPoll(int limit, Predicate<ItemStack> matcher) {
        return getInventorySlot().peekPoll(limit, matcher);
    }

    @Override
    public PeekedOfferTransactionResult peekOffer(ItemStack itemStack) {
        return getInventorySlot().peekOffer(itemStack);
    }

    @Override
    public PeekedSetTransactionResult peekSet(@Nullable ItemStack itemStack) {
        return getInventorySlot().peekSet(itemStack);
    }

    @Override
    public boolean isValidItem(ItemStack stack) {
        return getInventorySlot().isValidItem(stack);
    }

    @Override
    public int getStackSize() {
        return getInventorySlot().getStackSize();
    }

    @Override
    public InventoryTransactionResult set(@Nullable ItemStack stack) {
        return getInventorySlot().set(stack);
    }

    @Override
    public InventoryTransactionResult setForced(@Nullable ItemStack stack) {
        return getInventorySlot().setForced(stack);
    }

    @Override
    public void clear() {
        getInventorySlot().clear();
    }

    @Override
    public int size() {
        return getInventorySlot().size();
    }

    @Override
    public int totalItems() {
        return getInventorySlot().totalItems();
    }

    @Override
    public int capacity() {
        return getInventorySlot().capacity();
    }

    @Override
    public boolean hasChildren() {
        return getInventorySlot().hasChildren();
    }

    @Override
    public boolean contains(ItemStack stack) {
        return getInventorySlot().contains(stack);
    }

    @Override
    public boolean contains(ItemType type) {
        return getInventorySlot().contains(type);
    }

    @Override
    public boolean containsAny(ItemStack stack) {
        return getInventorySlot().containsAny(stack);
    }

    @Override
    public int getMaxStackSize() {
        return getInventorySlot().getMaxStackSize();
    }

    @Override
    public void setMaxStackSize(int size) {
        getInventorySlot().setMaxStackSize(size);
    }

    @Override
    public boolean containsInventory(Inventory inventory) {
        return inventory == this || getInventorySlot().containsInventory(inventory);
    }

    @Override
    public Iterator<Inventory> iterator() {
        return getInventorySlot().iterator();
    }

    @Nullable
    @Override
    public LanternItemStack getRawItemStack() {
        return getInventorySlot().getRawItemStack();
    }

    @Override
    public void setRawItemStack(@Nullable ItemStack itemStack) {
        getInventorySlot().setRawItemStack(itemStack);
    }
}
