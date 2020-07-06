/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.inventory;

import static org.lanternpowered.server.text.translation.TranslationHelper.tr;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.data.property.Property;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperties;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.ViewableInventory;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.api.text.translation.Translation;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("unchecked")
class LanternEmptyInventory extends AbstractInventory implements EmptyInventory, IQueryInventory {

    static class Name {
        static final Translation INSTANCE = tr("inventory.empty.name");
    }

    private static final UUID EMPTY_UNIQUE_ID = new UUID(0L, 0L);

    @Override
    public EmptyInventory empty() {
        return this;
    }

    @Override
    public void addChangeListener(SlotChangeListener listener) {
    }

    @Override
    public void addViewListener(InventoryViewerListener listener) {
    }

    @Override
    public void addCloseListener(InventoryCloseListener listener) {
    }

    @Override
    public LanternItemStack poll(ItemType itemType) {
        return LanternItemStack.empty();
    }

    @Override
    public LanternItemStack poll(Predicate<ItemStack> matcher) {
        return LanternItemStack.empty();
    }

    @Override
    public LanternItemStack poll(int limit, ItemType itemType) {
        return LanternItemStack.empty();
    }

    @Override
    public LanternItemStack poll(int limit, Predicate<ItemStack> matcher) {
        return LanternItemStack.empty();
    }

    @Override
    public LanternItemStack peek(ItemType itemType) {
        return LanternItemStack.empty();
    }

    @Override
    public LanternItemStack peek(Predicate<ItemStack> matcher) {
        return LanternItemStack.empty();
    }

    @Override
    public LanternItemStack peek(int limit, ItemType itemType) {
        return LanternItemStack.empty();
    }

    @Override
    public LanternItemStack peek(int limit, Predicate<ItemStack> matcher) {
        return LanternItemStack.empty();
    }

    @Override
    public PeekedOfferTransactionResult peekOffer(ItemStack stack) {
        return new PeekedOfferTransactionResult(ImmutableList.of(), stack.createSnapshot());
    }

    @Override
    public PeekedPollTransactionResult peekPoll(Predicate<ItemStack> matcher) {
        return PeekedPollTransactionResult.empty();
    }

    @Override
    public PeekedPollTransactionResult peekPoll(int limit, Predicate<ItemStack> matcher) {
        return PeekedPollTransactionResult.empty();
    }

    @Override
    public PeekedSetTransactionResult peekSet(ItemStack stack) {
        return new PeekedSetTransactionResult(ImmutableList.of(), stack.createSnapshot());
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
    protected void peekOffer(ItemStack stack, @Nullable Consumer<SlotTransaction> transactionAdder) {
    }

    @Override
    protected void offer(ItemStack stack, @Nullable Consumer<SlotTransaction> transactionAdder) {
    }

    @Override
    protected void set(ItemStack stack, boolean force, @Nullable Consumer<SlotTransaction> transactionAdder) {
    }

    @Override
    protected ViewableInventory toViewable() {
        return null;
    }

    @Override
    public boolean isValidItem(ItemStack stack) {
        return false;
    }

    @Override
    public IInventory intersect(Inventory inventory) {
        return this;
    }

    @Override
    public IInventory union(Inventory inventory) {
        return inventory instanceof EmptyInventory ? this : (IInventory) inventory;
    }

    @Override
    public boolean containsInventory(Inventory inventory) {
        return false;
    }

    @Override
    public LanternItemStack poll() {
        return LanternItemStack.empty();
    }

    @Override
    public LanternItemStack poll(int limit) {
        return LanternItemStack.empty();
    }

    @Override
    public LanternItemStack peek() {
        return LanternItemStack.empty();
    }

    @Override
    public LanternItemStack peek(int limit) {
        return LanternItemStack.empty();
    }

    @Override
    public InventoryTransactionResult offer(ItemStack stack) {
        return CachedInventoryTransactionResults.FAIL_NO_TRANSACTIONS;
    }

    @Override
    public boolean canFit(ItemStack stack) {
        return false;
    }

    @Override
    public InventoryTransactionResult setForced(@Nullable ItemStack stack) {
        return CachedInventoryTransactionResults.FAIL_NO_TRANSACTIONS;
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
    public InventoryTransactionResult set(@Nullable ItemStack stack) {
        return CachedInventoryTransactionResults.FAIL_NO_TRANSACTIONS;
    }

    @Override
    public void clear() {
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int totalItems() {
        return 0;
    }

    @Override
    public int capacity() {
        return 0;
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public boolean contains(ItemStack stack) {
        return false;
    }

    @Override
    public boolean contains(ItemType type) {
        return false;
    }

    @Override
    public boolean containsAny(ItemStack stack) {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 0;
    }

    @Override
    public void setMaxStackSize(int size) {
    }

    @Override
    public PluginContainer getPlugin() {
        // Use the plugin container from the parent if possible
        final AbstractInventory parent = parent();
        return parent == this ? Lantern.getMinecraftPlugin() : parent.getPlugin();
    }

    @Override
    public InventoryArchetype getArchetype() {
        return LanternInventoryArchetypes.EMPTY;
    }

    @Override
    public Translation getName() {
        return Name.INSTANCE;
    }

    @Override
    protected void queryInventories(QueryInventoryAdder adder) {
    }

    @Override
    protected <V> Optional<V> tryGetProperty(Property<V> property) {
        if (property == InventoryProperties.UNIQUE_ID) {
            return Optional.of((V) EMPTY_UNIQUE_ID);
        }
        return super.tryGetProperty(property);
    }
}
