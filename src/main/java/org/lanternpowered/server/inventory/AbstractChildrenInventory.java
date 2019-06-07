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
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.inventory.vanilla.VanillaInventoryConstants.HOPPER_SIZE;
import static org.lanternpowered.server.inventory.vanilla.VanillaInventoryConstants.MAX_CHEST_SIZE;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.inventory.client.TopContainerPart;
import org.lanternpowered.server.inventory.type.LanternChildrenInventory;
import org.spongepowered.api.data.property.Property;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperties;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.gui.GuiIds;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.slot.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.ViewableInventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A base class for all the {@link Inventory}s that have multiple children,
 * this should be every {@link Inventory} except {@link EmptyInventory}
 * or {@link Slot}.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractChildrenInventory extends AbstractMutableInventory {

    public static Builder<LanternChildrenInventory> builder() {
        return new Builder<>().type(LanternChildrenInventory.class);
    }

    public static ViewBuilder<LanternChildrenInventory> viewBuilder() {
        return new ViewBuilder<>().type(LanternChildrenInventory.class);
    }

    @Nullable private List<AbstractMutableInventory> children;

    @Nullable private List<AbstractSlot> slots;
    @Nullable private Object2IntMap<AbstractSlot> slotsToIndex;

    void initWithSlots(List<AbstractMutableInventory> children, List<? extends AbstractSlot> slots) {
        this.children = children;
        final Object2IntMap<AbstractSlot> slotsToIndex = new Object2IntOpenHashMap<>();
        slotsToIndex.defaultReturnValue(INVALID_SLOT_INDEX);
        int index = 0;
        for (AbstractSlot slot : slots) {
            slotsToIndex.put(slot, index++);
        }
        this.slots = ImmutableList.copyOf(slots);
        this.slotsToIndex = Object2IntMaps.unmodifiable(slotsToIndex);
        init();
    }

    void initWithChildren(List<AbstractMutableInventory> children, boolean lazySlots) {
        this.children = children;
        // Lazily initialize slots
        if (!lazySlots) {
            initSlots();
        }
        init();
    }

    private void initSlots() {
        if (this.slots != null || this.children == null) {
            return;
        }
        final ImmutableList.Builder<AbstractSlot> slotsBuilder = ImmutableList.builder();
        final Object2IntMap<AbstractSlot> slotsToIndex = new Object2IntOpenHashMap<>();
        slotsToIndex.defaultReturnValue(INVALID_SLOT_INDEX);
        int index = 0;
        for (AbstractMutableInventory inventory : this.children) {
            if (inventory instanceof AbstractSlot) {
                final AbstractSlot slot = (AbstractSlot) inventory;
                // Don't include duplicate slots, e.g. from queries
                if (slotsToIndex.putIfAbsent(slot, index) == INVALID_SLOT_INDEX) {
                    slotsBuilder.add(slot);
                    index++;
                }
            } else if (inventory instanceof AbstractChildrenInventory) {
                final AbstractChildrenInventory childrenInventory = (AbstractChildrenInventory) inventory;
                for (AbstractSlot slot : childrenInventory.getSlots()) {
                    // Don't include duplicate slots, e.g. from queries
                    if (slotsToIndex.putIfAbsent(slot, index) == INVALID_SLOT_INDEX) {
                        slotsBuilder.add(slot);
                        index++;
                    }
                }
            } else {
                throw new IllegalArgumentException("All the children inventories must be ordered.");
            }
        }
        this.slots = slotsBuilder.build();
        this.slotsToIndex = Object2IntMaps.unmodifiable(slotsToIndex);
    }

    /**
     * Gets a {@link List} with all the children
     * in this inventory.
     *
     * @return The children list
     */
    @Override
    protected List<AbstractMutableInventory> getChildren() {
        return this.children == null ? Collections.emptyList() : this.children;
    }

    @Override
    protected List<AbstractSlot> getSlots() {
        initSlots();
        return this.slots == null ? Collections.emptyList() : this.slots;
    }

    protected Object2IntMap<AbstractSlot> getSlotsToIndexMap() {
        initSlots();
        return this.slotsToIndex == null ? Object2IntMaps.emptyMap() : this.slotsToIndex;
    }

    @Override
    void close(CauseStack causeStack) {
        super.close(causeStack);
        getChildren().forEach(inventory -> inventory.close(causeStack));
    }

    @Override
    protected ViewableInventory toViewable() {
        final List<AbstractSlot> slots = getSlots();
        // Check if the slots fit the max chest size
        if (slots.size() > MAX_CHEST_SIZE) {
            return null;
        }
        final ViewBuilder<LanternChildrenInventory.Viewable> builder = AbstractChildrenInventory.viewBuilder()
                .inventories(slots) // Just put all the slots in a chest/hopper inventory layout
                .type(LanternChildrenInventory.Viewable.class);
        if (slots.size() <= HOPPER_SIZE) {
            builder.property(InventoryProperties.GUI_ID, GuiIds.HOPPER); // Hopper is a row of 5 slots
        } else {
            builder.property(InventoryProperties.GUI_ID, GuiIds.CHEST);
        }
        return builder.plugin(getPlugin()).build();
    }

    @Override
    void addViewer(Player viewer, AbstractContainer container) {
        super.addViewer(viewer, container);
        getChildren().forEach(child -> child.addViewer(viewer, container));
    }

    @Override
    void removeViewer(Player viewer, AbstractContainer container) {
        super.removeViewer(viewer, container);
        getChildren().forEach(child -> child.removeViewer(viewer, container));
    }

    @Override
    protected void queryInventories(QueryInventoryAdder adder) throws QueryInventoryAdder.Stop {
        // First test direct children
        getChildren().forEach(adder::add);
        // Then children of children
        getChildren().forEach(child -> child.queryInventories(adder));
    }

    @Override
    void setCarrier(Carrier carrier, boolean override) {
        super.setCarrier(carrier, override);
        getChildren().forEach(child -> child.setCarrier(carrier, override));
    }

    @Override
    public void addChangeListener(SlotChangeListener listener) {
        getChildren().forEach(child -> child.addChangeListener(listener));
    }

    @Override
    public void clear() {
        getChildren().forEach(AbstractMutableInventory::clear);
    }

    @Override
    public PeekedSetTransactionResult peekSet(ItemStack itemStack) {
        return new PeekedSetTransactionResult(ImmutableList.of(), itemStack.createSnapshot());
    }

    @Override
    public int size() {
        int size = 0;
        for (AbstractMutableInventory child : getChildren()) {
            size += child.size();
        }
        return size;
    }

    @Override
    public int totalItems() {
        int totalItems = 0;
        for (AbstractMutableInventory child : getChildren()) {
            totalItems += child.totalItems();
        }
        return totalItems;
    }

    @Override
    public int capacity() {
        return getSlots().size();
    }

    @Override
    public boolean hasChildren() {
        return !getChildren().isEmpty();
    }

    @Override
    public boolean containsInventory(Inventory inventory) {
        checkNotNull(inventory, "inventory");
        if (inventory == this) {
            return true;
        }
        for (AbstractMutableInventory child : getChildren()) {
            if (child == inventory || child.containsInventory(inventory)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains(ItemStack stack) {
        checkNotNull(stack, "stack");
        for (AbstractMutableInventory child : getChildren()) {
            if (child.contains(stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAny(ItemStack stack) {
        checkNotNull(stack, "stack");
        for (AbstractMutableInventory inventory : getChildren()) {
            if (inventory.containsAny(stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains(ItemType type) {
        checkNotNull(type, "type");
        for (AbstractMutableInventory inventory : getChildren()) {
            if (inventory.contains(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isValidItem(ItemStack stack) {
        checkNotNull(stack, "stack");
        for (AbstractMutableInventory child : getChildren()) {
            if (child.isValidItem(stack)) {
                return true;
            }
        }
        return false;
    }

    // The max stack size can only be modified specifically for slots,
    // so use the default value and disable modifying the max stack size

    @Override
    public int getMaxStackSize() {
        return AbstractSlot.DEFAULT_MAX_STACK_SIZE;
    }

    @Override
    public void setMaxStackSize(int size) {
    }

    @Override
    public LanternItemStack poll(Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        for (AbstractMutableInventory inventory : getChildren()) {
            final LanternItemStack itemStack = inventory.poll(matcher);
            if (itemStack.isNotEmpty()) {
                return itemStack;
            }
        }
        return LanternItemStack.empty();
    }

    @Override
    public LanternItemStack poll(int limit, Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        checkArgument(limit >= 0, "Limit may not be negative");
        if (limit == 0) {
            return LanternItemStack.empty();
        }
        LanternItemStack stack = null;
        for (AbstractInventory inventory : getChildren()) {
            // Check whether the slot a item contains
            if (stack == null) {
                stack = inventory.poll(limit, matcher);
                if (stack.isNotEmpty()) {
                    if (stack.getQuantity() >= limit) {
                        return stack;
                    } else {
                        limit -= stack.getQuantity();
                        if (!(matcher instanceof SimilarItemMatcher)) {
                            matcher = new SimilarItemMatcher(stack);
                        }
                    }
                } else {
                    stack = null;
                }
            } else {
                final LanternItemStack stack1 = inventory.poll(limit, matcher);
                if (stack1.isNotEmpty()) {
                    final int stackSize = stack1.getQuantity();
                    limit -= stackSize;
                    stack.setQuantity(stack.getQuantity() + stackSize);
                    if (limit <= 0) {
                        return stack;
                    }
                }
            }
        }
        return stack == null ? LanternItemStack.empty() : stack;
    }

    @Override
    public PeekedPollTransactionResult peekPoll(Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        for (AbstractMutableInventory inventory : getChildren()) {
            final PeekedPollTransactionResult peekResult = inventory.peekPoll(matcher);
            if (!peekResult.isEmpty()) {
                return peekResult;
            }
        }
        return PeekedPollTransactionResult.empty();
    }

    @Override
    public PeekedPollTransactionResult peekPoll(int limit, Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        checkArgument(limit >= 0, "Limit may not be negative");
        if (limit == 0) {
            return PeekedPollTransactionResult.empty();
        }
        PeekedPollTransactionResult peekResult = null;
        // Loop through the children inventories
        for (AbstractMutableInventory inventory : getChildren()) {
            // Check whether the slot a item contains
            if (peekResult == null) {
                peekResult = inventory.peekPoll(limit, matcher);
                if (!peekResult.isEmpty()) {
                    // We got enough items with one poll
                    if (peekResult.getPolledItem().getQuantity() >= limit) {
                        return peekResult;
                    } else {
                        limit -= peekResult.getPolledItem().getQuantity();
                        if (!(matcher instanceof SimilarItemMatcher)) {
                            matcher = new SimilarItemMatcher(peekResult.getPolledItem());
                        }
                    }
                } else {
                    peekResult = null;
                }
            } else {
                final PeekedPollTransactionResult peekResult1 = inventory.peekPoll(limit, matcher);
                if (!peekResult1.isEmpty()) {
                    final int peekedStackSize = peekResult1.getPolledItem().getQuantity();
                    final ItemStack peekedItem = peekResult.getPolledItem();
                    limit -= peekedStackSize;
                    peekedItem.setQuantity(peekedItem.getQuantity() + peekedStackSize);
                    final List<SlotTransaction> transactions = new ArrayList<>();
                    transactions.addAll(peekResult.getTransactions());
                    transactions.addAll(peekResult1.getTransactions());
                    peekResult = new PeekedPollTransactionResult(transactions, peekedItem);
                    if (limit <= 0) {
                        return peekResult;
                    }
                }
            }
        }
        return peekResult == null ? PeekedPollTransactionResult.empty() : peekResult;
    }

    @Override
    public LanternItemStack peek(Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        for (AbstractMutableInventory inventory : getChildren()) {
            final LanternItemStack itemStack = inventory.peek(matcher);
            if (itemStack.isNotEmpty()) {
                return itemStack;
            }
        }
        return LanternItemStack.empty();
    }

    @Override
    public LanternItemStack peek(int limit, Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        checkArgument(limit >= 0, "Limit may not be negative");
        if (limit == 0) {
            return LanternItemStack.empty();
        }
        LanternItemStack stack = null;
        for (AbstractMutableInventory inventory : getChildren()) {
            // Check whether the slot a item contains
            if (stack == null) {
                stack = inventory.peek(limit, matcher);
                if (stack.isNotEmpty()) {
                    if (stack.getQuantity() >= limit) {
                        return stack;
                    } else {
                        limit -= stack.getQuantity();
                        if (!(matcher instanceof SimilarItemMatcher)) {
                            matcher = new SimilarItemMatcher(stack);
                        }
                    }
                } else {
                    stack = null;
                }
            } else {
                int peekedStackSize = 0;
                // Check whether the inventory a slot is to avoid
                // boxing/unboxing and cloning the item stack
                if (inventory instanceof Slot) {
                    final LanternItemStack stack1 = ((AbstractSlot) inventory).getRawItemStack();
                    if (stack1.isNotEmpty() && matcher.test(stack1)) {
                        peekedStackSize = Math.min(inventory.totalItems(), limit);
                    }
                } else {
                    final LanternItemStack stack1 = inventory.peek(limit, matcher);
                    if (stack1.isNotEmpty()) {
                        peekedStackSize = stack1.getQuantity();
                    }
                }
                if (peekedStackSize > 0) {
                    limit -= peekedStackSize;
                    stack.setQuantity(stack.getQuantity() + peekedStackSize);
                    if (limit <= 0) {
                        return stack;
                    }
                }
            }
        }
        return stack == null ? LanternItemStack.empty() : stack;
    }

    @Override
    protected void peekOffer(ItemStack stack, @Nullable Consumer<SlotTransaction> transactionAdder) {
        final Set<Inventory> processed = new HashSet<>();
        final Inventory inventory = query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(stack));
        if (inventory instanceof AbstractChildrenInventory) {
            ((AbstractChildrenInventory) inventory).peekOffer(stack, processed, transactionAdder);
            // Stack got consumed, stop fast
            if (stack.isEmpty()) {
                return;
            }
        }
        peekOffer(stack, processed, transactionAdder);
    }

    private void peekOffer(ItemStack stack, Set<Inventory> processed, @Nullable Consumer<SlotTransaction> transactionAdder) {
        for (AbstractMutableInventory inventory : getChildren()) {
            AbstractInventory delegate = inventory;
            // Check for the delegate slot if present
            while (delegate instanceof AbstractForwardingSlot) {
                delegate = ((AbstractForwardingSlot) delegate).getDelegateSlot();
            }
            if (!processed.add(delegate)) {
                continue;
            }
            inventory.peekOffer(stack, transactionAdder);
            // Stack got consumed, stop fast
            if (stack.isEmpty()) {
                return;
            }
        }
    }

    @Override
    protected void offer(ItemStack stack, @Nullable Consumer<SlotTransaction> transactionAdder) {
        final Set<Inventory> processed = new HashSet<>();
        final Inventory inventory = query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(stack));
        if (inventory instanceof AbstractChildrenInventory) {
            ((AbstractChildrenInventory) inventory).offer(stack, processed, transactionAdder);
            // Stack got consumed, stop fast
            if (stack.isEmpty()) {
                return;
            }
        }
        offer(stack, processed, transactionAdder);
    }

    private void offer(ItemStack stack, Set<Inventory> processed, @Nullable Consumer<SlotTransaction> transactionAdder) {
        for (AbstractMutableInventory inventory : getChildren()) {
            AbstractInventory delegate = inventory;
            // Check for the delegate slot if present
            while (delegate instanceof AbstractForwardingSlot) {
                delegate = ((AbstractForwardingSlot) delegate).getDelegateSlot();
            }
            if (!processed.add(delegate)) {
                continue;
            }
            inventory.offer(stack, transactionAdder);
            // Stack got consumed, stop fast
            if (stack.isEmpty()) {
                return;
            }
        }
    }

    @Override
    protected void set(ItemStack stack, boolean force, @Nullable Consumer<SlotTransaction> transactionAdder) {
        for (AbstractMutableInventory inventory : getChildren()) {
            inventory.set(stack, force, transactionAdder);
            // Stack got consumed, stop fast
            if (stack.isEmpty()) {
                return;
            }
        }
    }

    @Override
    public Optional<ISlot> getSlot(int index) {
        final List<AbstractSlot> slots = getSlots();
        return index < 0 || index >= slots.size() ? Optional.empty() : Optional.ofNullable(slots.get(index));
    }

    @Override
    public int getSlotIndex(Slot slot) {
        return getSlotsToIndexMap().getInt(slot);
    }

    @Override
    protected <V> Optional<V> tryGetProperty(Inventory child, Property<V> property) {
        if (property == InventoryProperties.SLOT_INDEX && child instanceof Slot) {
            final int index = getSlotIndex((Slot) child);
            return index == INVALID_SLOT_INDEX ? Optional.empty() : Optional.of((V) SlotIndex.of(index));
        }
        return super.tryGetProperty(child, property);
    }

    @Override
    protected void initClientContainer(ClientContainer clientContainer) {
        super.initClientContainer(clientContainer);

        // Bind all the slots of this inventory
        final TopContainerPart part = clientContainer.getTop();
        getSlotsToIndexMap().object2IntEntrySet()
                .forEach(entry -> part.bindSlot(entry.getIntValue(), entry.getKey().viewedSlot()));
    }

    /**
     * A {@link ItemStack} matcher that matches stacks that are similar, internal use only.
     */
    private static final class SimilarItemMatcher implements Predicate<ItemStack> {

        private final ItemStack itemStack;

        SimilarItemMatcher(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        @Override
        public boolean test(ItemStack itemStack) {
            return ((LanternItemStack) this.itemStack).similarTo(itemStack);
        }
    }

    public static final class Builder<T extends AbstractChildrenInventory>
            extends AbstractArchetypeBuilder<T, AbstractChildrenInventory, Builder<T>>  {

        private final List<LanternInventoryArchetype<? extends AbstractMutableInventory>> inventories = new ArrayList<>();
        private int expandedSlots = 0;

        private Builder() {
        }

        void expand(int size) {
            this.expandedSlots += size;
        }

        @Override
        public <N extends AbstractChildrenInventory> Builder<N> type(Class<N> inventoryType) {
            return (Builder<N>) super.type(inventoryType);
        }

        /**
         * Adds the {@link InventoryArchetype} at
         * the first position.
         *
         * @param inventoryArchetype The inventory archetype
         * @return This builder, for chaining
         */
        public Builder<T> addFirst(LanternInventoryArchetype<? extends AbstractMutableInventory> inventoryArchetype) {
            return add(0, inventoryArchetype);
        }

        /**
         * Adds the {@link InventoryArchetype} at
         * the last position.
         *
         * @param inventoryArchetype The inventory archetype
         * @return This builder, for chaining
         */
        public Builder<T> addLast(LanternInventoryArchetype<? extends AbstractMutableInventory> inventoryArchetype) {
            return add(this.inventories.size(), inventoryArchetype);
        }

        /**
         * Adds the {@link InventoryArchetype} at
         * the specified position.
         *
         * @param inventoryArchetype The inventory archetype
         * @return This builder, for chaining
         */
        public Builder<T> add(int index, LanternInventoryArchetype<? extends AbstractMutableInventory> inventoryArchetype) {
            this.inventories.add(index, inventoryArchetype);
            if (inventoryArchetype.getBuilder() instanceof AbstractInventorySlot.Builder) {
                this.slots++;
                if (this.expandedSlots < this.slots) {
                    this.expandedSlots = this.slots;
                }
            } else {
                this.slots += inventoryArchetype.getBuilder().slots;
                if (this.expandedSlots < this.slots) {
                    this.expandedSlots = this.slots;
                }
            }
            return this;
        }

        @Override
        protected void build(AbstractChildrenInventory inventory) {
            checkState(this.expandedSlots <= this.slots, "The builder got expanded to %s slots, "
                    + "but only %s slots could be found.", this.expandedSlots, this.slots);
            final ImmutableList<AbstractMutableInventory> children = this.inventories.stream()
                    .map(e -> {
                        final AbstractMutableInventory inventory1 = e.build();
                        inventory1.setParentSafely(inventory);
                        return inventory1;
                    })
                    .collect(ImmutableList.toImmutableList());
            inventory.initWithChildren(children, false);
        }

        @Override
        protected void copyTo(Builder<T> copy) {
            super.copyTo(copy);
            copy.inventories.addAll(this.inventories);
            copy.expandedSlots = this.expandedSlots;
        }

        @Override
        protected Builder<T> newBuilder() {
            return new Builder<>();
        }

        @Override
        protected List<InventoryArchetype> getArchetypes() {
            return (List) this.inventories;
        }
    }

    public static final class ViewBuilder<T extends AbstractChildrenInventory>
            extends AbstractViewBuilder<T, AbstractChildrenInventory, ViewBuilder<T>>  {

        private final List<AbstractMutableInventory> inventories = new ArrayList<>();

        private ViewBuilder() {
        }

        @Override
        public <N extends AbstractChildrenInventory> ViewBuilder<N> type(Class<N> inventoryType) {
            return (ViewBuilder<N>) super.type(inventoryType);
        }

        /**
         * Adds the {@link AbstractMutableInventory}.
         *
         * @param inventory The inventory
         * @return This builder, for chaining
         */
        public ViewBuilder<T> inventory(Inventory inventory) {
            checkNotNull(inventory, "inventory");
            this.inventories.add((AbstractMutableInventory) inventory);
            return this;
        }

        /**
         * Adds the {@link AbstractMutableInventory}s.
         *
         * @param inventories The inventories
         * @return This builder, for chaining
         */
        public ViewBuilder<T> inventories(Iterable<? extends Inventory> inventories) {
            inventories.forEach(this::inventory);
            return this;
        }

        @Override
        protected void build(AbstractChildrenInventory inventory) {
            inventory.initWithChildren(ImmutableList.copyOf(this.inventories), true);
        }
    }
}
