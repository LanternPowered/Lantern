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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.inventory.property.LanternAcceptsItems;
import org.lanternpowered.server.inventory.type.slot.LanternSlot;
import org.lanternpowered.server.item.predicate.EquipmentItemPredicate;
import org.lanternpowered.server.item.predicate.ItemPredicate;
import org.lanternpowered.server.item.predicate.PropertyItemPredicates;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.property.EquipmentSlotType;
import org.spongepowered.api.item.inventory.property.InventoryCapacity;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.ViewableInventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nullable;

public abstract class AbstractInventorySlot extends AbstractSlot {

    /**
     * Constructs a new {@link Builder}.
     *
     * @return The builder
     */
    public static Builder<AbstractInventorySlot> builder() {
        return new Builder<>();
    }

    /**
     * The {@link LanternItemStack} that is stored in this slot.
     */
    private LanternItemStack itemStack = LanternItemStack.empty();

    /**
     * The maximum stack size that can fit in this slot.
     */
    private int maxStackSize = DEFAULT_MAX_STACK_SIZE;

    /**
     * All the {@link SlotChangeTracker}s that track this slot.
     */
    private final Set<SlotChangeTracker> trackers = new HashSet<>();

    /**
     * {@link SlotChangeListener}s may track slot changes, these listeners
     * have to be removed manually after they are no longer needed.
     */
    private final List<SlotChangeListener> changeListeners = new ArrayList<>();

    /**
     * The {@link ItemPredicate} that defines which {@link ItemStack}s can be put in this slot.
     */
    @Nullable private ItemPredicate itemFilter;

    @Override
    public void addTracker(SlotChangeTracker tracker) {
        checkNotNull(tracker, "tracker");
        this.trackers.add(tracker);
    }

    @Override
    public void removeTracker(SlotChangeTracker tracker) {
        checkNotNull(tracker, "tracker");
        this.trackers.remove(tracker);
    }

    @Override
    public LanternItemStack getRawItemStack() {
        return this.itemStack;
    }

    @Override
    public void setRawItemStack(ItemStack itemStack) {
        checkNotNull(itemStack, "itemStack");
        if (!this.itemStack.equalTo(itemStack)) {
            queueUpdate();
        }
        this.itemStack = (LanternItemStack) itemStack;
    }

    @Override
    public AbstractInventorySlot viewedSlot() {
        return this;
    }

    protected void setFilter(@Nullable ItemPredicate itemFilter) {
        this.itemFilter = itemFilter;
    }

    @Nullable
    protected ItemPredicate getFilter() {
        return this.itemFilter;
    }

    /**
     * Queues this slot to be updated and trigger the listeners.
     */
    protected void queueUpdate() {
        for (SlotChangeListener listener : this.changeListeners) {
            listener.accept(this);
        }
        for (SlotChangeTracker tracker : this.trackers) {
            tracker.queueSlotChange(this);
        }
    }

    @Override
    public void addChangeListener(SlotChangeListener listener) {
        checkNotNull(listener, "listener");
        this.changeListeners.add(listener);
    }

    @Override
    protected List<AbstractSlot> getSlots() {
        return Collections.emptyList();
    }

    @Override
    protected List<? extends AbstractInventory> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public int getStackSize() {
        return LanternItemStack.isEmpty(this.itemStack) ? 0 : this.itemStack.getQuantity();
    }

    @Override
    public boolean isValidItem(ItemStackSnapshot stack) {
        checkNotNull(stack, "stack");
        return this.itemFilter == null || this.itemFilter.test(stack);
    }

    @Override
    public boolean isValidItem(ItemStack stack) {
        checkNotNull(stack, "stack");
        return this.itemFilter == null || this.itemFilter.test(stack);
    }

    @Override
    public boolean isValidItem(ItemType type) {
        checkNotNull(type, "type");
        return this.itemFilter == null || this.itemFilter.test(type);
    }

    @Override
    public boolean isValidItem(EquipmentType type) {
        checkNotNull(type, "type");
        return !(this.itemFilter instanceof EquipmentItemPredicate) || ((EquipmentItemPredicate) this.itemFilter).test(type);
    }

    @Override
    public LanternItemStack poll(Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        final LanternItemStack itemStack = this.itemStack;
        if (itemStack.isEmpty() || !matcher.test(itemStack)) {
            return LanternItemStack.empty();
        }
        // Just remove the item, the complete stack was being polled
        this.itemStack = LanternItemStack.empty();
        queueUpdate();
        return itemStack;
    }

    @Override
    public LanternItemStack poll(int limit, Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        checkArgument(limit >= 0, "Limit may not be negative");
        LanternItemStack itemStack = this.itemStack;
        // There is no item available
        if (itemStack.isEmpty() || !matcher.test(itemStack)) {
            return LanternItemStack.empty();
        }
        // Split the stack if needed
        if (limit < itemStack.getQuantity()) {
            itemStack.setQuantity(itemStack.getQuantity() - limit);
            // Clone the item to be returned
            itemStack = itemStack.copy();
            itemStack.setQuantity(limit);
        } else {
            this.itemStack = LanternItemStack.empty();
        }
        queueUpdate();
        return itemStack;
    }

    @Override
    public PeekedPollTransactionResult peekPoll(Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        if (this.itemStack.isEmpty() || !matcher.test(this.itemStack)) {
            return PeekedPollTransactionResult.empty();
        }
        final List<SlotTransaction> transactions = new ArrayList<>();
        transactions.add(new SlotTransaction(this, this.itemStack.createSnapshot(), ItemStackSnapshot.NONE));
        return new PeekedPollTransactionResult(transactions, this.itemStack.copy());
    }

    @Override
    public PeekedPollTransactionResult peekPoll(int limit, Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        checkArgument(limit >= 0, "Limit may not be negative");
        LanternItemStack itemStack = this.itemStack;
        // There is no item available
        if (limit == 0 || itemStack.isEmpty() || !matcher.test(itemStack)) {
            return PeekedPollTransactionResult.empty();
        }
        final ItemStackSnapshot oldItem = itemStack.createSnapshot();
        itemStack = itemStack.copy();
        final int quantity = itemStack.getQuantity();
        final ItemStackSnapshot newItem;
        if (limit >= quantity) {
            newItem = ItemStackSnapshot.NONE;
        } else {
            itemStack.setQuantity(quantity - limit);
            newItem = itemStack.toSnapshot();
            itemStack.setQuantity(limit);
        }
        final List<SlotTransaction> transactions = new ArrayList<>();
        transactions.add(new SlotTransaction(this, oldItem, newItem));
        return new PeekedPollTransactionResult(transactions, itemStack);
    }

    @Override
    public LanternItemStack peek(Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        return this.itemStack.isEmpty() || !matcher.test(this.itemStack) ? LanternItemStack.empty() : this.itemStack.copy();
    }

    @Override
    public LanternItemStack peek(int limit, Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        checkArgument(limit >= 0, "Limit may not be negative");
        LanternItemStack itemStack = this.itemStack;
        // There is no item available
        if (itemStack.isEmpty() || !matcher.test(itemStack)) {
            return LanternItemStack.empty();
        }
        itemStack = itemStack.copy();
        // Split the stack if needed
        if (limit < itemStack.getQuantity()) {
            itemStack.setQuantity(limit);
        }
        return itemStack;
    }

    @Override
    public PeekedOfferTransactionResult peekOffer(ItemStack stack) {
        checkNotNull(stack, "itemStack");
        if (stack.isEmpty()) {
            return new PeekedOfferTransactionResult(ImmutableList.of(), stack.createSnapshot());
        }
        final int maxStackSize = Math.min(stack.getMaxStackQuantity(), this.maxStackSize);
        if ((this.itemStack.isFilled() && (!this.itemStack.similarTo(stack) || this.itemStack.getQuantity() >= maxStackSize)) ||
                !isValidItem(stack)) {
            return new PeekedOfferTransactionResult(ImmutableList.of(), stack.createSnapshot());
        }
        final List<SlotTransaction> transactions = new ArrayList<>();
        // Get the amount of space we have left
        final int availableSpace = maxStackSize - this.itemStack.getQuantity();
        final int quantity = stack.getQuantity();
        if (quantity > availableSpace) {
            // Create a new item stack which will be the replacement of the current stack
            final LanternItemStack newStack;
            if (this.itemStack.isEmpty()) {
                newStack = (LanternItemStack) stack.copy();
            } else {
                newStack = this.itemStack.copy();
            }
            newStack.setQuantity(maxStackSize);
            // Consume items from the input stack
            stack.setQuantity(quantity - availableSpace);
            // Collect the transaction result
            transactions.add(new SlotTransaction(this, this.itemStack.toSnapshot(), newStack.toWrappedSnapshot()));
            return new PeekedOfferTransactionResult(transactions, stack.createSnapshot());
        } else {
            final LanternItemStack newStack;
            if (this.itemStack.isEmpty()) {
                newStack = (LanternItemStack) stack.copy();
            } else {
                newStack = this.itemStack.copy();
                newStack.setQuantity(newStack.getQuantity() + quantity);
            }
            // Consume the complete input stack
            stack.setQuantity(0);
            // Collect the transaction result
            transactions.add(new SlotTransaction(this, this.itemStack.toSnapshot(), newStack.toWrappedSnapshot()));
            return new PeekedOfferTransactionResult(transactions, ItemStackSnapshot.NONE);
        }
    }

    @Override
    protected void offer(ItemStack stack, @Nullable Consumer<SlotTransaction> transactionAdder) {
        checkNotNull(stack, "stack");
        if (stack.isEmpty()) {
            return;
        }
        final int maxStackSize = Math.min(stack.getMaxStackQuantity(), this.maxStackSize);
        if (this.itemStack.isFilled() && (!this.itemStack.similarTo(stack) ||
                this.itemStack.getQuantity() >= maxStackSize) || !isValidItem(stack)) {
            return;
        }
        ItemStackSnapshot oldSnapshot = null;
        if (transactionAdder != null) {
            oldSnapshot = this.itemStack.toSnapshot();
        }
        // Get the amount of space we have left
        final int availableSpace = maxStackSize - this.itemStack.getQuantity();
        final int quantity = stack.getQuantity();
        if (quantity > availableSpace) {
            if (this.itemStack.isEmpty()) {
                this.itemStack = (LanternItemStack) stack.copy();
            }
            this.itemStack.setQuantity(maxStackSize);
            // Consume items from the input stack
            stack.setQuantity(quantity - availableSpace);
        } else {
            if (this.itemStack.isEmpty()) {
                this.itemStack = (LanternItemStack) stack.copy();
            } else {
                this.itemStack.setQuantity(this.itemStack.getQuantity() + quantity);
            }
            // Consume the complete input stack
            stack.setQuantity(0);
        }
        if (transactionAdder != null) {
            transactionAdder.accept(new SlotTransaction(this, oldSnapshot, this.itemStack.toSnapshot()));
        }
        queueUpdate();
    }

    @Override
    public PeekedSetTransactionResult peekSet(ItemStack stack) {
        checkNotNull(stack, "stack");
        if (!stack.isEmpty() && !isValidItem(stack)) {
            return new PeekedSetTransactionResult(ImmutableList.of(), stack.createSnapshot());
        }
        final ItemStackSnapshot oldSnapshot = this.itemStack.toSnapshot();
        ItemStackSnapshot rejectedSnapshot = ItemStackSnapshot.NONE;
        ItemStackSnapshot newSnapshot = ItemStackSnapshot.NONE;
        if (!stack.isEmpty()) {
            final int maxStackSize = Math.min(stack.getMaxStackQuantity(), this.maxStackSize);
            final int quantity = stack.getQuantity();
            if (quantity > maxStackSize) {
                stack = stack.copy();
                stack.setQuantity(maxStackSize);

                newSnapshot = stack.createSnapshot();
                // Consume items from the input stack
                stack.setQuantity(quantity - maxStackSize);
                // Create rest snapshot
                rejectedSnapshot = stack.createSnapshot();
            } else {
                newSnapshot = stack.createSnapshot();
            }
        }
        return new PeekedSetTransactionResult(
                Collections.singletonList(new SlotTransaction(this, oldSnapshot, newSnapshot)), rejectedSnapshot);
    }

    @Override
    public Optional<ISlot> getSlot(int index) {
        return Optional.empty();
    }

    @Override
    public int getSlotIndex(Slot slot) {
        return INVALID_SLOT_INDEX;
    }

    @Override
    protected void set(ItemStack stack, boolean force, @Nullable Consumer<SlotTransaction> transactionAdder) {
        checkNotNull(stack, "stack");
        if (!stack.isEmpty() && !force && !isValidItem(stack)) {
            // Invalid items must be forced
            return;
        }

        final LanternItemStack newStack = (LanternItemStack) stack.copy();
        final InventoryTransactionResult.Builder resultBuilder = InventoryTransactionResult.builder();

        final int maxStackSize = Math.min(stack.getMaxStackQuantity(), this.maxStackSize);
        final int quantity = stack.getQuantity();
        if (quantity > maxStackSize) {
            newStack.setQuantity(maxStackSize);

            // Consume the items that fit in the slot
            stack.setQuantity(quantity - maxStackSize);

            // Reject the rest
            resultBuilder
                    .type(InventoryTransactionResult.Type.FAILURE) // Fail if not everything fits the slot
                    .reject(Collections.singleton(stack.createSnapshot()));
        } else {
            // Consume the complete stack
            stack.setQuantity(0);

            // Complete stack set, success
            resultBuilder.type(InventoryTransactionResult.Type.SUCCESS);
        }
        if (transactionAdder != null) {
            transactionAdder.accept(new SlotTransaction(this, this.itemStack.toSnapshot(), newStack.toSnapshot()));
        }
        this.itemStack = newStack;
        queueUpdate();
    }

    @Override
    public void clear() {
        if (this.itemStack.isFilled()) {
            this.itemStack = LanternItemStack.empty();
            queueUpdate();
        }
    }

    @Override
    public int size() {
        return this.itemStack.isEmpty() ? 0 : 1;
    }

    @Override
    public int totalItems() {
        return this.itemStack.getQuantity();
    }

    @Override
    public int capacity() {
        return 1;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean contains(ItemStack stack) {
        checkNotNull(stack, "stack");
        return containsAny(stack) && this.itemStack.getQuantity() >= stack.getQuantity();
    }

    @Override
    public boolean containsAny(ItemStack stack) {
        return this.itemStack.similarTo(stack);
    }

    @Override
    public boolean contains(ItemType type) {
        checkNotNull(type, "type");
        return this.itemStack.getType().equals(type);
    }

    @Override
    public int getMaxStackSize() {
        return this.maxStackSize;
    }

    @Override
    public void setMaxStackSize(int size) {
        checkArgument(size > 0, "Size must be greater then 0");
        this.maxStackSize = size;
    }

    @Override
    public boolean containsInventory(Inventory inventory) {
        return inventory == this;
    }

    @Override
    protected void queryInventories(QueryInventoryAdder adder) {
    }

    @Override
    protected ViewableInventory toViewable() {
        return null;
    }

    /**
     * Constructs a {@link AbstractContainerSlot} for this inventory slot.
     *
     * @return The container slot
     */
    protected abstract AbstractContainerSlot constructContainerSlot();

    @SuppressWarnings("unchecked")
    public static final class Builder<T extends AbstractInventorySlot> extends AbstractArchetypeBuilder<T, AbstractInventorySlot, Builder<T>> {

        @Nullable private ItemPredicate itemFilter;

        @Nullable private ItemPredicate cachedResultItemFilter;
        private boolean hasItemFilter;

        private Builder() {
            type(LanternSlot.class);
        }

        /**
         * Sets the {@link ItemPredicate}.
         *
         * @param itemFilter The item filter
         * @return This builder, for chaining
         */
        public Builder<T> filter(ItemPredicate itemFilter) {
            checkNotNull(itemFilter, "itemFilter");
            this.itemFilter = itemFilter;
            // Regenerate the result item filter
            this.hasItemFilter = true;
            this.cachedResultItemFilter = null;
            invalidateCachedArchetype();
            return this;
        }

        @Override
        public Builder<T> property(InventoryProperty<String, ?> property) {
            checkArgument(!(property instanceof SlotIndex), "The slot index may not be set through a property.");
            checkArgument(!(property instanceof InventoryCapacity), "The slot capacity cannot be set.");
            super.property(property);
            // Regenerate the result item filter
            if (property instanceof EquipmentSlotType || property instanceof LanternAcceptsItems) {
                this.hasItemFilter = true;
                this.cachedResultItemFilter = null;
                invalidateCachedArchetype();
            }
            return this;
        }

        @Nullable
        private <R extends InventoryProperty<String, ?>> R findProperty(Class<R> type) {
            final Map<String, InventoryProperty<String, ?>> properties = this.properties.get(type);
            if (properties == null) {
                return null;
            }
            return (R) properties.values().stream().findFirst().orElse(null);
        }

        @Override
        protected void build(AbstractInventorySlot inventory) {
            if (this.cachedResultItemFilter == null && this.hasItemFilter) {
                ItemPredicate itemFilter = this.itemFilter;
                // Attempt to generate the ItemFilter
                final LanternAcceptsItems acceptsItems = findProperty(LanternAcceptsItems.class);
                if (acceptsItems != null) {
                    itemFilter = PropertyItemPredicates.of(acceptsItems);
                }
                final EquipmentSlotType equipmentSlotType = findProperty(EquipmentSlotType.class);
                if (equipmentSlotType != null) {
                    EquipmentItemPredicate equipmentItemFilter = EquipmentItemPredicate.of(equipmentSlotType);
                    if (itemFilter != null) {
                        equipmentItemFilter = equipmentItemFilter.andThen(itemFilter);
                    }
                    itemFilter = equipmentItemFilter;
                }
                this.cachedResultItemFilter = itemFilter;
            }
            inventory.setFilter(this.cachedResultItemFilter);
            inventory.init();
        }

        @Override
        protected void copyTo(Builder<T> copy) {
            super.copyTo(copy);
            copy.itemFilter = this.itemFilter;
            copy.hasItemFilter = this.hasItemFilter;
            copy.cachedResultItemFilter = this.cachedResultItemFilter;
        }

        @Override
        protected Builder<T> newBuilder() {
            return new Builder<>();
        }

        @Override
        protected List<InventoryArchetype> getArchetypes() {
            return Collections.emptyList();
        }
    }
}
