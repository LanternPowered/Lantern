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
import org.lanternpowered.server.inventory.filter.EquipmentItemFilter;
import org.lanternpowered.server.inventory.filter.ItemFilter;
import org.lanternpowered.server.inventory.filter.PropertyItemFilters;
import org.lanternpowered.server.inventory.property.LanternAcceptsItems;
import org.lanternpowered.server.inventory.type.slot.LanternSlot;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Carrier;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
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
    @Nullable private LanternItemStack itemStack;

    /**
     * The maximum stack size that can fit in this slot.
     */
    private int maxStackSize = DEFAULT_MAX_STACK_SIZE;

    /**
     * All the {@link LanternContainer}s this slot is attached to, all
     * these containers will be notified if anything changes. A weak
     * set is used to avoid leaks when a container isn't properly cleaned up.
     */
    private final Set<SlotChangeTracker> trackers = Collections.newSetFromMap(new WeakHashMap<>());

    /**
     * {@link SlotChangeListener}s may track slot changes, these listeners
     * have to be removed manually after they are no longer needed.
     */
    private final List<SlotChangeListener> changeListeners = new ArrayList<>();

    /**
     * The {@link ItemFilter} that defines which {@link ItemStack}s can be put in this slot.
     */
    @Nullable private ItemFilter itemFilter;

    /**
     * Adds the {@link SlotChangeTracker}.
     *
     * @param tracker The slot change tracker
     */
    public void addTracker(SlotChangeTracker tracker) {
        this.trackers.add(tracker);
    }

    /**
     * Removes the {@link SlotChangeTracker}.
     *
     * @param tracker The slot change tracker
     */
    public void removeTracker(SlotChangeTracker tracker) {
        this.trackers.remove(tracker);
    }

    @Override
    public LanternItemStack getRawItemStack() {
        return this.itemStack;
    }

    @Override
    public void setRawItemStack(@Nullable ItemStack itemStack) {
        itemStack = itemStack == null || itemStack.isEmpty() ? null : itemStack;
        if (!Objects.equals(this.itemStack, itemStack)) {
            queueUpdate();
        }
        this.itemStack = (LanternItemStack) itemStack;
    }

    protected void setFilter(@Nullable ItemFilter itemFilter) {
        this.itemFilter = itemFilter;
    }

    @Override
    public Slot transform(Type type) {
        checkNotNull(type, "type");
        if (type == Type.INVENTORY) {
            return this;
        }
        throw new IllegalStateException("Unsupported transform type: " + type);
    }

    @Override
    public AbstractInventorySlot transform() {
        return this;
    }

    @Nullable
    protected ItemFilter getFilter() {
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
    protected void setCarrier(Carrier carrier) {
    }

    @Override
    public void addChangeListener(SlotChangeListener listener) {
        checkNotNull(listener, "listener");
        this.changeListeners.add(listener);
    }

    @Override
    protected List<AbstractSlot> getSlotInventories() {
        return Collections.emptyList();
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public int getStackSize() {
        return LanternItemStack.isEmpty(this.itemStack) ? 0 : this.itemStack.getQuantity();
    }

    @Override
    public boolean isValidItem(ItemStack stack) {
        checkNotNull(stack, "stack");
        return this.itemFilter == null || this.itemFilter.isValid(stack);
    }

    @Override
    public boolean isValidItem(ItemType type) {
        checkNotNull(type, "type");
        return this.itemFilter == null || this.itemFilter.isValid(type);
    }

    @Override
    public boolean isValidItem(EquipmentType type) {
        checkNotNull(type, "type");
        return this.itemFilter == null || !(this.itemFilter instanceof EquipmentItemFilter) ||
                ((EquipmentItemFilter) this.itemFilter).isValid(type);
    }

    @Override
    public Optional<ItemStack> poll(Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        if (this.itemStack == null || !matcher.test(this.itemStack)) {
            return Optional.empty();
        }
        final ItemStack itemStack = this.itemStack;
        // Just remove the item, the complete stack was
        // being polled
        this.itemStack = null;
        queueUpdate();
        return Optional.of(itemStack);
    }

    @Override
    public Optional<ItemStack> poll(int limit, Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        checkArgument(limit >= 0, "Limit may not be negative");
        ItemStack itemStack = this.itemStack;
        // There is no item available
        if (itemStack == null || !matcher.test(itemStack)) {
            return Optional.empty();
        }
        // Split the stack if needed
        if (limit < itemStack.getQuantity()) {
            itemStack.setQuantity(itemStack.getQuantity() - limit);
            // Clone the item to be returned
            itemStack = itemStack.copy();
            itemStack.setQuantity(limit);
        } else {
            this.itemStack = null;
        }
        queueUpdate();
        return Optional.of(itemStack);
    }

    @Override
    public Optional<PeekedPollTransactionResult> peekPoll(Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        if (this.itemStack == null || !matcher.test(this.itemStack)) {
            return Optional.empty();
        }
        final List<SlotTransaction> transactions = new ArrayList<>();
        transactions.add(new SlotTransaction(this, this.itemStack.createSnapshot(), ItemStackSnapshot.NONE));
        return Optional.of(new PeekedPollTransactionResult(transactions, this.itemStack.copy()));
    }

    @Override
    public Optional<PeekedPollTransactionResult> peekPoll(int limit, Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        checkArgument(limit >= 0, "Limit may not be negative");
        ItemStack itemStack = this.itemStack;
        // There is no item available
        if (limit == 0 || itemStack == null || !matcher.test(itemStack)) {
            return Optional.empty();
        }
        final ItemStackSnapshot oldItem = itemStack.createSnapshot();
        itemStack = itemStack.copy();
        final int quantity = itemStack.getQuantity();
        final ItemStackSnapshot newItem;
        if (limit >= quantity) {
            newItem = ItemStackSnapshot.NONE;
        } else {
            itemStack.setQuantity(quantity - limit);
            newItem = LanternItemStack.toSnapshot(itemStack);
            itemStack.setQuantity(limit);
        }
        final List<SlotTransaction> transactions = new ArrayList<>();
        transactions.add(new SlotTransaction(this, oldItem, newItem));
        return Optional.of(new PeekedPollTransactionResult(transactions, itemStack));
    }

    @Override
    public Optional<ItemStack> peek(Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        return Optional.ofNullable(this.itemStack == null || !matcher.test(this.itemStack) ? null : this.itemStack.copy());
    }

    @Override
    public Optional<ItemStack> peek(int limit, Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        checkArgument(limit >= 0, "Limit may not be negative");
        ItemStack itemStack = this.itemStack;
        // There is no item available
        if (itemStack == null || !matcher.test(itemStack)) {
            return Optional.empty();
        }
        itemStack = itemStack.copy();
        // Split the stack if needed
        if (limit < itemStack.getQuantity()) {
            itemStack.setQuantity(limit);
        }
        return Optional.of(itemStack);
    }

    @Override
    public PeekedOfferTransactionResult peekOffer(ItemStack itemStack) {
        checkNotNull(itemStack, "itemStack");
        if (itemStack.isEmpty()) {
            return new PeekedOfferTransactionResult(InventoryTransactionResult.Type.FAILURE, ImmutableList.of(), itemStack);
        }
        final int maxStackSize = Math.min(itemStack.getMaxStackQuantity(), this.maxStackSize);
        if ((this.itemStack != null && (!this.itemStack.similarTo(itemStack)
                || this.itemStack.getQuantity() >= maxStackSize)) || !isValidItem(itemStack)) {
            return new PeekedOfferTransactionResult(InventoryTransactionResult.Type.FAILURE, ImmutableList.of(), itemStack);
        }
        final List<SlotTransaction> transactions = new ArrayList<>();
        // Get the amount of space we have left
        final int availableSpace = this.itemStack == null ? maxStackSize :
                maxStackSize - this.itemStack.getQuantity();
        final int quantity = itemStack.getQuantity();
        if (quantity > availableSpace) {
            ItemStack newStack;
            if (this.itemStack == null) {
                newStack = itemStack.copy();
            } else {
                newStack = this.itemStack.copy();
            }
            newStack.setQuantity(maxStackSize);
            itemStack = itemStack.copy();
            itemStack.setQuantity(quantity - availableSpace);
            transactions.add(new SlotTransaction(this, LanternItemStack.toSnapshot(this.itemStack), LanternItemStackSnapshot.wrap(newStack)));
            return new PeekedOfferTransactionResult(InventoryTransactionResult.Type.SUCCESS, transactions, itemStack);
        } else {
            final ItemStack newStack;
            if (this.itemStack == null) {
                newStack = itemStack.copy();
            } else {
                newStack = this.itemStack.copy();
                newStack.setQuantity(newStack.getQuantity() + quantity);
            }
            transactions.add(new SlotTransaction(this, LanternItemStack.toSnapshot(this.itemStack),
                    LanternItemStackSnapshot.wrap(newStack)));
            return new PeekedOfferTransactionResult(InventoryTransactionResult.Type.SUCCESS, transactions, null);
        }
    }

    @Override
    public FastOfferResult offerFast(ItemStack stack) {
        checkNotNull(stack, "stack");
        if (LanternItemStack.toNullable(stack) == null) {
            return new FastOfferResult(stack, false);
        }
        final int maxStackSize = Math.min(stack.getMaxStackQuantity(), this.maxStackSize);
        if (this.itemStack != null && (!this.itemStack.similarTo(stack) ||
                this.itemStack.getQuantity() >= maxStackSize) || !isValidItem(stack)) {
            return new FastOfferResult(stack, false);
        }
        // Get the amount of space we have left
        final int availableSpace = this.itemStack == null ? maxStackSize :
                maxStackSize - this.itemStack.getQuantity();
        final int quantity = stack.getQuantity();
        if (quantity > availableSpace) {
            if (this.itemStack == null) {
                this.itemStack = (LanternItemStack) stack.copy();
            }
            this.itemStack.setQuantity(maxStackSize);
            stack = stack.copy();
            stack.setQuantity(quantity - availableSpace);
            queueUpdate();
            return new FastOfferResult(stack, true);
        } else {
            if (this.itemStack == null) {
                this.itemStack = (LanternItemStack) stack.copy();
            } else {
                this.itemStack.setQuantity(this.itemStack.getQuantity() + quantity);
            }
            queueUpdate();
            return FastOfferResult.SUCCESS_NO_REJECTED_ITEM;
        }
    }

    @Override
    public PeekedSetTransactionResult peekSet(@Nullable ItemStack stack) {
        if (!LanternItemStack.isEmpty(stack) && !isValidItem(stack)) {
            return new PeekedSetTransactionResult(InventoryTransactionResult.Type.FAILURE, ImmutableList.of(), stack, null);
        }
        stack = LanternItemStack.toNullable(stack);
        final List<SlotTransaction> transactions = new ArrayList<>();
        final ItemStackSnapshot oldItem = LanternItemStack.toSnapshot(this.itemStack);
        ItemStack rejectedItem = null;
        ItemStack replacedItem = oldItem.isEmpty() ? null : this.itemStack.copy();
        ItemStackSnapshot newItem = ItemStackSnapshot.NONE;
        if (stack != null) {
            final int maxStackSize = Math.min(stack.getMaxStackQuantity(), this.maxStackSize);
            final int quantity = stack.getQuantity();
            if (quantity > maxStackSize) {
                stack = stack.copy();
                stack.setQuantity(maxStackSize);
                newItem = LanternItemStack.toSnapshot(stack);
                // Create the rest stack that was rejected,
                // because the inventory doesn't allow so many items
                rejectedItem = stack.copy();
                rejectedItem.setQuantity(quantity - maxStackSize);
            } else {
                newItem = LanternItemStack.toSnapshot(stack);
            }
        }
        transactions.add(new SlotTransaction(this, oldItem, newItem));
        return new PeekedSetTransactionResult(InventoryTransactionResult.Type.SUCCESS, transactions, rejectedItem, replacedItem);
    }

    @Override
    public InventoryTransactionResult set(@Nullable ItemStack stack) {
        return set(stack, false);
    }

    @Override
    public InventoryTransactionResult setForced(@Nullable ItemStack stack) {
        return set(stack, true);
    }

    private InventoryTransactionResult set(@Nullable ItemStack stack, boolean forced) {
        stack = LanternItemStack.toNullable(stack);
        boolean fail = false;
        if (stack != null) {
            if (stack.getQuantity() <= 0) {
                stack = null;
            } else if (!forced) {
                fail = !isValidItem(stack);
            }
        }
        if (fail) {
            return InventoryTransactionResult.builder()
                    .type(InventoryTransactionResult.Type.FAILURE)
                    .reject(stack)
                    .build();
        }
        InventoryTransactionResult.Builder resultBuilder = InventoryTransactionResult.builder()
                .type(InventoryTransactionResult.Type.SUCCESS);
        if (this.itemStack != null) {
            resultBuilder.replace(this.itemStack);
        }
        if (stack != null) {
            stack = stack.copy();
            final int maxStackSize = Math.min(stack.getMaxStackQuantity(), this.maxStackSize);
            final int quantity = stack.getQuantity();
            if (quantity > maxStackSize) {
                stack.setQuantity(maxStackSize);
                // Create the rest stack that was rejected,
                // because the inventory doesn't allow so many items
                stack = stack.copy();
                stack.setQuantity(quantity - maxStackSize);
                resultBuilder.reject(stack);
            }
        }
        this.itemStack = (LanternItemStack) stack;
        queueUpdate();
        return resultBuilder.build();
    }

    @Override
    public <T extends Inventory> Iterable<T> slots() {
        return Collections.emptyList();
    }

    @Override
    public void clear() {
        if (this.itemStack != null) {
            this.itemStack = null;
            queueUpdate();
        }
    }

    @Override
    public int size() {
        return this.itemStack == null || this.itemStack.isEmpty() ? 0 : 1;
    }

    @Override
    public int totalItems() {
        return this.itemStack == null || this.itemStack.isEmpty() ? 0 : this.itemStack.getQuantity();
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
        checkNotNull(stack, "stack");
        return !LanternItemStack.isEmpty(this.itemStack) && LanternItemStack.areSimilar(this.itemStack, stack);
    }

    @Override
    public boolean contains(ItemType type) {
        checkNotNull(type, "type");
        return !LanternItemStack.isEmpty(this.itemStack) && this.itemStack.getType().equals(type);
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
    public Iterator<Inventory> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    protected <T extends Inventory> T queryInventories(Predicate<AbstractMutableInventory> predicate) {
        return genericEmpty();
    }

    /**
     * Constructs a {@link AbstractContainerSlot} for this inventory slot.
     *
     * @return The container slot
     */
    protected abstract AbstractContainerSlot constructContainerSlot();

    @SuppressWarnings("unchecked")
    public static final class Builder<T extends AbstractInventorySlot> extends AbstractArchetypeBuilder<T, AbstractInventorySlot, Builder<T>> {

        @Nullable private ItemFilter itemFilter;

        @Nullable private ItemFilter cachedResultItemFilter;
        private boolean hasItemFilter;

        private Builder() {
            type(LanternSlot.class);
        }

        /**
         * Sets the {@link ItemFilter}.
         *
         * @param itemFilter The item filter
         * @return This builder, for chaining
         */
        public Builder<T> filter(ItemFilter itemFilter) {
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
                ItemFilter itemFilter = this.itemFilter;
                // Attempt to generate the ItemFilter
                final LanternAcceptsItems acceptsItems = findProperty(LanternAcceptsItems.class);
                if (acceptsItems != null) {
                    itemFilter = PropertyItemFilters.of(acceptsItems);
                }
                final EquipmentSlotType equipmentSlotType = findProperty(EquipmentSlotType.class);
                if (equipmentSlotType != null) {
                    EquipmentItemFilter equipmentItemFilter = EquipmentItemFilter.of(equipmentSlotType);
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
