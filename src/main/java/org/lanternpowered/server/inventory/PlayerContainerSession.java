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

import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.entity.HumanInventoryView;
import org.lanternpowered.server.inventory.entity.HumanMainInventory;
import org.lanternpowered.server.inventory.entity.LanternHotbar;
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInClickWindow;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInCreativeWindowAction;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInDropHeldItem;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutCloseWindow;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetWindowSlot;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.slot.OutputSlot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

/**
 * Represents a session of a player interacting with a
 * {@link LanternContainer}. It is possible to switch
 * between {@link LanternContainer}s without canceling
 * the session.
 *
 * This will for example keep the cursor item until it
 * is placed or the session is finished.
 */
public class PlayerContainerSession {

    private final LanternPlayer player;

    /**
     * The container that is currently open.
     */
    @Nullable private LanternContainer openContainer;

    /**
     * The item stack currently on the cursor
     */
    @Nullable private ItemStack cursorItem;

    /**
     * All the slots currently in a drag session.
     */
    private final List<LanternSlot> dragSlots = new ArrayList<>();

    /**
     * Whether the dragging was started with the left mouse button.
     *
     * -1 means not started
     *  0 means left drag
     *  1 means right drag
     */
    private int dragState = -1;

    public PlayerContainerSession(LanternPlayer player) {
        this.player = player;
    }

    /**
     * Gets the open container.
     *
     * @return The container
     */
    @Nullable
    public LanternContainer getOpenContainer() {
        return this.openContainer;
    }

    /**
     * Sets the open container.
     *
     * @param container The container
     */
    public boolean setOpenContainer(@Nullable LanternContainer container, @Nullable Cause cause) {
        return this.setRawOpenContainer(container, cause, true);
    }

    public boolean setRawOpenContainer(@Nullable LanternContainer container, @Nullable Cause cause) {
        return this.setRawOpenContainer(container, cause, false);
    }

    /**
     * Sets the open container.
     *
     * @param container The container
     */
    private boolean setRawOpenContainer(@Nullable LanternContainer container, @Nullable Cause cause, boolean sendClose) {
        if (this.openContainer != container) {
            final ItemStackSnapshot oldCursorItemSnapshot = LanternItemStack.toSnapshot(this.cursorItem);
            ItemStackSnapshot cursorItemSnapshot = oldCursorItemSnapshot;
            if (this.openContainer != null) {
                if (cause != null) {
                    final InteractInventoryEvent.Close event = SpongeEventFactory.createInteractInventoryEventClose(
                            cause, new Transaction<>(cursorItemSnapshot, ItemStackSnapshot.NONE), this.openContainer);
                    Sponge.getEventManager().post(event);
                    if (event.isCancelled()) {
                        return false;
                    }
                    final Transaction<ItemStackSnapshot> transaction = event.getCursorTransaction();
                    if (transaction.isValid()) {
                        cursorItemSnapshot = transaction.getFinal();
                    }
                } else {
                    this.cursorItem = null;
                }
                if (LanternItemStack.toNullable(oldCursorItemSnapshot) != null) {
                    final List<Entity> entities = new ArrayList<>();
                    entities.add(this.createDroppedItem(oldCursorItemSnapshot));

                    final SpawnEntityEvent event1 = SpongeEventFactory.createDropItemEventDispense(cause, entities, this.player.getWorld());
                    Sponge.getEventManager().post(event1);
                    if (!event1.isCancelled()) {
                        this.finishSpawnEntityEvent(event1);
                    }
                }
            } else {
                sendClose = false;
            }
            if (container != null) {
                if (cause != null) {
                    final InteractInventoryEvent.Open event = SpongeEventFactory.createInteractInventoryEventOpen(
                            cause, new Transaction<>(cursorItemSnapshot, cursorItemSnapshot), container);
                    Sponge.getEventManager().post(event);
                    if (event.isCancelled()) {
                        this.cursorItem = LanternItemStack.toNullable(cursorItemSnapshot);
                        return false;
                    }
                    final Transaction<ItemStackSnapshot> transaction = event.getCursorTransaction();
                    if (transaction.isValid()) {
                        cursorItemSnapshot = transaction.getFinal();
                        this.cursorItem = LanternItemStack.toNullable(cursorItemSnapshot);
                    }
                }
                // The container is being used for the first time
                if (container.getRawViewers().isEmpty()) {
                    container.addSlotTrackers();
                }
                sendClose = false;
                container.addViewer(this.player, container);
                container.viewers.add(this.player);
                container.openInventoryForAndInitialize(this.player);
                this.updateCursorItem();
            } else {
                this.cursorItem = LanternItemStack.toNullable(cursorItemSnapshot);
            }
            if (sendClose && this.openContainer.windowId != -1) {
                this.player.getConnection().send(
                        new MessagePlayInOutCloseWindow(this.openContainer.windowId));
            }
            if (this.openContainer != null) {
                this.openContainer.viewers.remove(this.player);
                this.openContainer.removeViewer(this.player, this.openContainer);
                if (this.openContainer.getRawViewers().isEmpty()) {
                    this.openContainer.removeSlotTrackers();
                }
            }
        }
        this.openContainer = container;
        return true;
    }

    /**
     * Sets the cursor item.
     *
     * @param cursorItem The cursor item
     */
    public void setCursorItem(@Nullable ItemStack cursorItem) {
        this.cursorItem = LanternItemStack.toNullable(cursorItem);
        this.updateCursorItem();
    }

    private void updateCursorItem() {
        this.player.getConnection().send(
                new MessagePlayOutSetWindowSlot(-1, -1, this.cursorItem));
    }

    @Nullable
    public ItemStack getCursorItem() {
        return this.cursorItem;
    }

    public void handleWindowCreativeClick(MessagePlayInCreativeWindowAction message) {
        if (this.openContainer == null) {
            return;
        }
        ItemStack itemStack = LanternItemStack.toNullable(message.getItemStack());
        int slotIndex = message.getSlot();
        if (slotIndex < 0) {
            if (itemStack != null) {
                final Cause cause = Cause.builder().named("SpawnCause", SpawnCause.builder()
                        .type(SpawnTypes.DROPPED_ITEM).build()).named(NamedCause.SOURCE, this.player).build();

                final List<Entity> entities = new ArrayList<>();
                entities.add(this.createDroppedItem(itemStack.createSnapshot()));
                final World world = this.player.getWorld();

                final SpawnEntityEvent event = SpongeEventFactory.createDropItemEventDispense(cause, entities, world);
                Sponge.getEventManager().post(event);

                if (!event.isCancelled()) {
                    this.finishSpawnEntityEvent(event);
                }
            }
        } else {
            Optional<LanternSlot> optSlot = this.openContainer.playerInventory.getSlotAt(slotIndex);
            if (optSlot.isPresent()) {
                final Cause cause = Cause.builder().named(NamedCause.SOURCE, this.player).build();
                final LanternSlot slot = optSlot.get();

                PeekSetTransactionsResult result = slot.peekSetTransactions(itemStack);

                // We do not know the remaining stack in the cursor,
                // so just use none as new item
                Transaction<ItemStackSnapshot> cursorTransaction = new Transaction<>(
                        LanternItemStack.toSnapshot(itemStack), ItemStackSnapshot.NONE);

                ClickInventoryEvent.Creative event = SpongeEventFactory.createClickInventoryEventCreative(
                        cause, cursorTransaction, this.openContainer, result.getTransactions());
                this.finishInventoryEvent(event);
            } else {
                Lantern.getLogger().warn("Unknown slot index {} in container {}", slotIndex, this.openContainer);
            }
        }
    }

    public void handleItemDrop(MessagePlayInDropHeldItem message) {
        final LanternSlot slot = this.player.getInventory().getHotbar().getSelectedSlot();
        final Optional<ItemStack> itemStack = message.isFullStack() ? slot.peek() : slot.peek(1);

        if (itemStack.isPresent()) {
            final Cause cause = Cause.builder().named("SpawnCause", SpawnCause.builder()
                    .type(SpawnTypes.DROPPED_ITEM).build())
                    .named(NamedCause.SOURCE, this.player)
                    .named("Slot", slot)
                    .build();

            final List<Entity> entities = new ArrayList<>();
            entities.add(this.createDroppedItem(itemStack.get().createSnapshot()));

            final SpawnEntityEvent event = SpongeEventFactory.createDropItemEventDispense(cause, entities, this.player.getWorld());
            Sponge.getEventManager().post(event);

            if (!event.isCancelled()) {
                if (message.isFullStack()) {
                    slot.poll();
                } else {
                    slot.poll(1);
                }
                this.finishSpawnEntityEvent(event);
            }
        }
    }

    private void resetDrag() {
        this.dragState = -1;
        this.dragSlots.clear();
    }

    public void handleWindowClick(MessagePlayInClickWindow message) {
        if (this.openContainer == null) {
            return;
        }
        final int windowId = message.getWindowId();
        if (windowId != this.openContainer.windowId) {
            return;
        }
        final int button = message.getButton();
        final int mode = message.getMode();
        final int slotIndex = message.getSlot();

        // Drag mode
        if (mode == 5) {
            if (this.cursorItem == null) {
                this.resetDrag();
            } else if (this.dragState != -1) {
                if ((this.dragState == 0 && (button == 1 || button == 2)) ||
                        (this.dragState == 1 && (button == 5 || button == 6))) {
                    if (button == 2 || button == 6) {
                        final Cause cause = Cause.builder().named(NamedCause.SOURCE, this.player).build();
                        if (button == 2) {
                            int quantity = this.cursorItem.getQuantity();
                            int slots = this.dragSlots.size();
                            int itemsPerSlot = quantity / slots;
                            int rest = quantity - itemsPerSlot * slots;

                            final List<SlotTransaction> transactions = new ArrayList<>();
                            for (LanternSlot slot : this.dragSlots) {
                                final ItemStack itemStack = this.cursorItem.copy();
                                itemStack.setQuantity(itemsPerSlot);
                                transactions.addAll(slot.peekOfferFastTransactions(itemStack).getTransactions());
                            }

                            ItemStackSnapshot newCursorItem = ItemStackSnapshot.NONE;
                            if (rest > 0) {
                                ItemStack itemStack = this.cursorItem.copy();
                                itemStack.setQuantity(rest);
                                newCursorItem = itemStack.createSnapshot();
                            }
                            final ItemStackSnapshot oldCursorItem = this.cursorItem.createSnapshot();
                            final Transaction<ItemStackSnapshot> cursorTransaction = new Transaction<>(oldCursorItem, newCursorItem);

                            final ClickInventoryEvent.Drag.Primary event = SpongeEventFactory.createClickInventoryEventDragPrimary(
                                    cause, cursorTransaction, this.openContainer, transactions);
                            this.finishInventoryEvent(event);
                            this.resetDrag();
                        } else {
                            int quantity = this.cursorItem.getQuantity();
                            int size = Math.min(this.dragSlots.size(), quantity);

                            final List<SlotTransaction> transactions = new ArrayList<>();
                            for (LanternSlot slot : this.dragSlots) {
                                final ItemStack itemStack = this.cursorItem.copy();
                                itemStack.setQuantity(1);
                                transactions.addAll(slot.peekOfferFastTransactions(itemStack).getTransactions());
                            }
                            quantity -= size;

                            ItemStackSnapshot newCursorItem = ItemStackSnapshot.NONE;
                            if (quantity > 0) {
                                ItemStack itemStack = this.cursorItem.copy();
                                itemStack.setQuantity(quantity);
                                newCursorItem = itemStack.createSnapshot();
                            }
                            final ItemStackSnapshot oldCursorItem = this.cursorItem.createSnapshot();
                            final Transaction<ItemStackSnapshot> cursorTransaction = new Transaction<>(oldCursorItem, newCursorItem);

                            final ClickInventoryEvent.Drag.Secondary event = SpongeEventFactory.createClickInventoryEventDragSecondary(
                                    cause, cursorTransaction, this.openContainer, transactions);
                            this.finishInventoryEvent(event);
                            this.resetDrag();
                        }
                    } else {
                        // Add slot
                        final Optional<LanternSlot> optSlot = this.openContainer.getSlotAt(slotIndex);
                        if (optSlot.isPresent()) {
                            final LanternSlot slot = optSlot.get();
                            if (!(slot instanceof OutputSlot) && slot.isValidItem(this.cursorItem) && (slot.getRawItemStack() == null ||
                                    ((LanternItemStack) this.cursorItem).isEqualToOther(slot.getRawItemStack())) && !this.dragSlots.contains(slot)) {
                                this.dragSlots.add(slot);
                            }
                        }
                    }
                } else {
                    this.resetDrag();
                }
            } else if (button == 0) {
                this.dragState = 0;
            } else if (button == 4) {
                this.dragState = 1;
            }
        } else if (this.dragState != -1) {
            this.resetDrag();
        } else if (mode == 0 && (button == 0 || button == 1) && slotIndex != -999) {
            final Optional<LanternSlot> optSlot = this.openContainer.getSlotAt(slotIndex);
            if (optSlot.isPresent()) {
                final LanternSlot slot = optSlot.get();
                final Cause cause = Cause.builder().named(NamedCause.SOURCE, this.player).build();

                ClickInventoryEvent event;
                if (button == 0) {
                    final List<SlotTransaction> transactions = new ArrayList<>();
                    Transaction<ItemStackSnapshot> cursorTransaction = null;

                    if (this.cursorItem != null && !(slot instanceof OutputSlot)) {
                        final PeekOfferTransactionsResult result = slot.peekOfferFastTransactions(this.cursorItem);
                        if (result.getOfferResult().isSuccess()) {
                            transactions.addAll(result.getTransactions());
                            cursorTransaction = new Transaction<>(this.cursorItem.createSnapshot(),
                                    LanternItemStack.toSnapshot(result.getOfferResult().getRest()));
                        } else {
                            final PeekSetTransactionsResult result1 = slot.peekSetTransactions(this.cursorItem);
                            if (result1.getTransactionResult().getType().equals(InventoryTransactionResult.Type.SUCCESS)) {
                                final Collection<ItemStackSnapshot> replaceItems = result1.getTransactionResult().getReplacedItems();
                                if (!replaceItems.isEmpty()) {
                                    cursorTransaction = new Transaction<>(this.cursorItem.createSnapshot(),
                                            replaceItems.iterator().next());
                                } else {
                                    cursorTransaction = new Transaction<>(this.cursorItem.createSnapshot(),
                                            ItemStackSnapshot.NONE);
                                }
                                transactions.addAll(result1.getTransactions());
                            }
                        }
                    } else if (this.cursorItem == null) {
                        final PeekPollTransactionsResult result = slot.peekPollTransactions(stack -> true).orElse(null);
                        if (result != null) {
                            cursorTransaction = new Transaction<>(ItemStackSnapshot.NONE, LanternItemStack.toSnapshot(result.getPeekedItem()));
                            transactions.addAll(result.getTransactions());
                        } else {
                            cursorTransaction = new Transaction<>(ItemStackSnapshot.NONE, ItemStackSnapshot.NONE);
                        }
                    }
                    if (cursorTransaction == null) {
                        final ItemStackSnapshot cursorItem = LanternItemStack.toSnapshot(this.cursorItem);
                        cursorTransaction = new Transaction<>(cursorItem, cursorItem);
                    }
                    event = SpongeEventFactory.createClickInventoryEventPrimary(cause, cursorTransaction, this.openContainer, transactions);
                } else {
                    final List<SlotTransaction> transactions = new ArrayList<>();
                    Transaction<ItemStackSnapshot> cursorTransaction = null;

                    if (this.cursorItem == null) {
                        int stackSize = slot.getStackSize();
                        if (stackSize != 0) {
                            stackSize = stackSize - (stackSize / 2);
                            final PeekPollTransactionsResult result = slot.peekPollTransactions(stackSize, stack -> true).get();
                            transactions.addAll(result.getTransactions());
                            cursorTransaction = new Transaction<>(ItemStackSnapshot.NONE, result.getPeekedItem().createSnapshot());
                        }
                    } else {
                        final ItemStack itemStack = this.cursorItem.copy();
                        itemStack.setQuantity(1);

                        final PeekOfferTransactionsResult result = slot.peekOfferFastTransactions(itemStack);
                        if (result.getOfferResult().isSuccess()) {
                            int quantity = this.cursorItem.getQuantity() - 1;
                            if (quantity <= 0) {
                                cursorTransaction = new Transaction<>(this.cursorItem.createSnapshot(), ItemStackSnapshot.NONE);
                            } else {
                                final ItemStack cursorItem = this.cursorItem.copy();
                                cursorItem.setQuantity(quantity);
                                cursorTransaction = new Transaction<>(cursorItem.createSnapshot(), ItemStackSnapshot.NONE);
                            }
                        } else {
                            final PeekSetTransactionsResult result1 = slot.peekSetTransactions(this.cursorItem);
                            if (result1.getTransactionResult().getType().equals(InventoryTransactionResult.Type.SUCCESS)) {
                                final Collection<ItemStackSnapshot> replaceItems = result1.getTransactionResult().getReplacedItems();
                                if (!replaceItems.isEmpty()) {
                                    this.setCursorItem(replaceItems.iterator().next().createStack());
                                    cursorTransaction = new Transaction<>(this.cursorItem.createSnapshot(),
                                            replaceItems.iterator().next());
                                } else {
                                    cursorTransaction = new Transaction<>(this.cursorItem.createSnapshot(),
                                            ItemStackSnapshot.NONE);
                                }
                                transactions.addAll(result1.getTransactions());
                            }
                        }
                    }
                    if (cursorTransaction == null) {
                        ItemStackSnapshot cursorItem = LanternItemStack.toSnapshot(this.cursorItem);
                        cursorTransaction = new Transaction<>(cursorItem, cursorItem);
                    }
                    event = SpongeEventFactory.createClickInventoryEventSecondary(cause, cursorTransaction, this.openContainer, transactions);
                }
                this.finishInventoryEvent(event);
            } else {
                Lantern.getLogger().warn("Unknown slot index {} in container {}", slotIndex, this.openContainer);
            }
        } else if (mode == 1 && (button == 0 || button == 1)) {
            Optional<LanternSlot> optSlot = this.openContainer.getSlotAt(slotIndex);
            if (optSlot.isPresent()) {
                final LanternSlot slot = optSlot.get();
                final ItemStack itemStack = slot.peek().orElse(null);

                final Cause cause = Cause.builder().named(NamedCause.SOURCE, this.player).build();
                final List<SlotTransaction> transactions = new ArrayList<>();

                final ItemStackSnapshot cursorItem = LanternItemStack.toSnapshot(this.cursorItem);
                final Transaction<ItemStackSnapshot> cursorTransaction = new Transaction<>(cursorItem, cursorItem);

                if (itemStack != null) {
                    InventoryBase inventory;

                    final HumanMainInventory mainInventory = this.openContainer.playerInventory.getMain();
                    if ((windowId != 0 && this.openContainer.openInventory.getSlotIndex(slot) != -1) ||
                            (windowId == 0 && !mainInventory.isChild(slot))) {
                        if (slot.isReverseShiftClickOfferOrder()) {
                            inventory = this.openContainer.playerInventory.getInventoryView(HumanInventoryView.REVERSE_MAIN_AND_HOTBAR);
                        } else {
                            inventory = this.openContainer.playerInventory.getInventoryView(HumanInventoryView.PRIORITY_MAIN_AND_HOTBAR);
                        }
                    } else {
                        inventory = this.openContainer.openInventory.query(inv -> !mainInventory.isChild(inv) && inv instanceof Slot &&
                                ((LanternSlot) inv).doesAllowShiftClickOffer() && !(inv instanceof OutputSlot), false);
                        if (!inventory.isValidItem(itemStack)) {
                            if (slot.parent() instanceof LanternHotbar) {
                                inventory = this.openContainer.playerInventory.getInventoryView(HumanInventoryView.MAIN);
                            } else {
                                inventory = this.openContainer.playerInventory.getHotbar();
                            }
                        }
                    }

                    final PeekOfferTransactionsResult result = inventory.peekOfferFastTransactions(itemStack.copy());
                    if (result.getOfferResult().isSuccess()) {
                        transactions.addAll(result.getTransactions());
                        final ItemStack rest = result.getOfferResult().getRest();
                        if (rest != null) {
                            transactions.addAll(slot.peekPollTransactions(
                                    itemStack.getQuantity() - rest.getQuantity(), stack -> true).get().getTransactions());
                        } else {
                            transactions.addAll(slot.peekPollTransactions(
                                    stack -> true).get().getTransactions());
                        }
                    }
                }

                final ClickInventoryEvent.Shift event;
                if (button == 0) {
                    event = SpongeEventFactory.createClickInventoryEventShiftPrimary(
                            cause, cursorTransaction, this.openContainer, transactions);
                } else {
                    event = SpongeEventFactory.createClickInventoryEventShiftSecondary(
                            cause, cursorTransaction, this.openContainer, transactions);
                }

                this.finishInventoryEvent(event);
            } else {
                Lantern.getLogger().warn("Unknown slot index {} in container {}", slotIndex, this.openContainer);
            }
        } else if (mode == 6 && button == 0) {
            final Optional<LanternSlot> optSlot = this.openContainer.getSlotAt(slotIndex);
            if (optSlot.isPresent()) {
                final Cause cause = Cause.builder().named(NamedCause.SOURCE, this.player).build();
                final ItemStackSnapshot oldItem = LanternItemStack.toSnapshot(this.cursorItem);
                ItemStackSnapshot newItem = oldItem;

                final List<SlotTransaction> transactions = new ArrayList<>();

                if (this.cursorItem != null) {
                    final ItemStack cursorItem = this.cursorItem.copy();
                    int quantity = cursorItem.getQuantity();
                    final int maxQuantity = cursorItem.getMaxStackQuantity();
                    if (quantity < maxQuantity) {
                        final InventoryBase inventory;
                        if (windowId != 0) {
                            inventory = new ChildrenInventoryBase(null, null, Arrays.asList(
                                    this.openContainer.openInventory, this.openContainer.playerInventory
                                            .getInventoryView(HumanInventoryView.PRIORITY_MAIN_AND_HOTBAR)));
                        } else {
                            inventory = this.openContainer.playerInventory
                                    .getInventoryView(HumanInventoryView.ALL_PRIORITY_MAIN);
                        }

                        // Try first to get enough unfinished stacks
                        PeekPollTransactionsResult peekResult = inventory.peekPollTransactions(maxQuantity - quantity, stack ->
                                stack.getQuantity() < stack.getMaxStackQuantity() &&
                                        ((LanternItemStack) cursorItem).isEqualToOther(stack)).orElse(null);
                        if (peekResult != null) {
                            quantity += peekResult.getPeekedItem().getQuantity();
                            transactions.addAll(peekResult.getTransactions());
                        }
                        // Get the last items for the stack from a full stack
                        if (quantity <= maxQuantity) {
                            peekResult = this.openContainer.peekPollTransactions(maxQuantity - quantity, stack ->
                                    stack.getQuantity() >= stack.getMaxStackQuantity() &&
                                            ((LanternItemStack) cursorItem).isEqualToOther(stack)).orElse(null);
                            if (peekResult != null) {
                                quantity += peekResult.getPeekedItem().getQuantity();
                                transactions.addAll(peekResult.getTransactions());
                            }
                        }
                        cursorItem.setQuantity(quantity);
                        newItem = cursorItem.createSnapshot();
                    }
                }

                final Transaction<ItemStackSnapshot> cursorTransaction = new Transaction<>(oldItem, newItem);
                final ClickInventoryEvent.Double event = SpongeEventFactory.createClickInventoryEventDouble(
                        cause, cursorTransaction, this.openContainer, transactions);

                this.finishInventoryEvent(event);
            } else {
                Lantern.getLogger().warn("Unknown slot index {} in container {}", slotIndex, this.openContainer);
            }
        } else if (mode == 2) {
            final Optional<LanternSlot> optSlot = this.openContainer.getSlotAt(slotIndex);
            if (optSlot.isPresent()) {
                final LanternSlot slot = optSlot.get();

                final LanternHotbar hotbar = this.openContainer.playerInventory.getHotbar();
                final Optional<LanternSlot> optHotbarSlot = hotbar.getSlotAt(button);
                if (optHotbarSlot.isPresent()) {
                    final LanternSlot hotbarSlot = optHotbarSlot.get();

                    final Cause cause = Cause.builder().named(NamedCause.SOURCE, this.player).build();
                    final List<SlotTransaction> transactions = new ArrayList<>();

                    final Transaction<ItemStackSnapshot> cursorTransaction;

                    if (this.cursorItem == null) {
                        cursorTransaction = new Transaction<>(ItemStackSnapshot.NONE, ItemStackSnapshot.NONE);

                        ItemStack otherItemStack = slot.getRawItemStack();
                        ItemStack hotbarItemStack = hotbarSlot.getRawItemStack();

                        ItemStackSnapshot otherItem = LanternItemStack.toSnapshot(otherItemStack);
                        ItemStackSnapshot hotbarItem = LanternItemStack.toSnapshot(hotbarItemStack);

                        if (!(otherItem != ItemStackSnapshot.NONE && (!hotbarSlot.isValidItem(otherItemStack) ||
                                otherItemStack.getQuantity() > hotbarSlot.getMaxStackSize())) &&
                                !(hotbarItem != ItemStackSnapshot.NONE && (!slot.isValidItem(hotbarItemStack) ||
                                        hotbarItemStack.getQuantity() > slot.getMaxStackSize()))) {
                            transactions.add(new SlotTransaction(slot, otherItem, hotbarItem));
                            transactions.add(new SlotTransaction(hotbarSlot, hotbarItem, otherItem));
                        }
                    } else {
                        final ItemStackSnapshot cursorItem = this.cursorItem.createSnapshot();
                        cursorTransaction = new Transaction<>(cursorItem, cursorItem);
                    }

                    final ClickInventoryEvent.NumberPress event = SpongeEventFactory.createClickInventoryEventNumberPress(
                            cause, cursorTransaction, this.openContainer, transactions, button);
                    this.finishInventoryEvent(event);
                } else {
                    Lantern.getLogger().warn("Unknown hotbar slot index {}", mode);
                }
            } else {
                Lantern.getLogger().warn("Unknown slot index {} in container {}", slotIndex, this.openContainer);
            }
        } else if ((mode == 4 || mode == 0) && (button == 0 || button == 1)) {
            ClickInventoryEvent.Drop event = null;

            final Cause cause = Cause.builder().named("SpawnCause", SpawnCause.builder()
                    .type(SpawnTypes.DROPPED_ITEM).build()).named(NamedCause.SOURCE, this.player).build();
            final List<Entity> entities = new ArrayList<>();
            final World world = this.player.getWorld();

            final Transaction<ItemStackSnapshot> cursorTransaction;
            final List<SlotTransaction> slotTransactions = new ArrayList<>();

            if (slotIndex == -999) {
                ItemStackSnapshot oldItem = ItemStackSnapshot.NONE;
                ItemStackSnapshot newItem = ItemStackSnapshot.NONE;
                if (this.cursorItem != null) {
                    oldItem = this.cursorItem.createSnapshot();
                    if (button != 0) {
                        final ItemStack stack = this.cursorItem.copy();
                        stack.setQuantity(stack.getQuantity() - 1);
                        newItem = LanternItemStack.toSnapshot(stack);
                        stack.setQuantity(1);
                        entities.add(this.createDroppedItem(LanternItemStack.toSnapshot(stack)));
                    } else {
                        entities.add(this.createDroppedItem(oldItem));
                    }
                }
                cursorTransaction = new Transaction<>(oldItem, newItem);
                if (button == 0) {
                    event = SpongeEventFactory.createClickInventoryEventDropOutsidePrimary(cause, cursorTransaction, entities,
                            this.openContainer, world, slotTransactions);
                } else {
                    event = SpongeEventFactory.createClickInventoryEventDropOutsideSecondary(cause, cursorTransaction, entities,
                            this.openContainer, world, slotTransactions);
                }
            } else {
                final ItemStackSnapshot item = LanternItemStack.toSnapshot(this.cursorItem);
                cursorTransaction = new Transaction<>(item, item);
                final Optional<LanternSlot> optSlot = this.openContainer.getSlotAt(slotIndex);
                if (optSlot.isPresent()) {
                    final LanternSlot slot = optSlot.get();
                    final Optional<PeekPollTransactionsResult> result = button == 0 ?
                            slot.peekPollTransactions(1, itemStack -> true) : slot.peekPollTransactions(itemStack -> true);
                    if (result.isPresent()) {
                        final List<SlotTransaction> transactions = result.get().getTransactions();
                        slotTransactions.addAll(transactions);
                        final ItemStack itemStack = transactions.get(0).getOriginal().createStack();
                        itemStack.setQuantity(itemStack.getQuantity() - transactions.get(0).getFinal().getCount());
                        entities.add(this.createDroppedItem(itemStack.createSnapshot()));
                    }
                    if (button == 0) {
                        event = SpongeEventFactory.createClickInventoryEventDropSingle(cause, cursorTransaction, entities,
                                this.openContainer, world, slotTransactions);
                    } else {
                        event = SpongeEventFactory.createClickInventoryEventDropFull(cause, cursorTransaction, entities,
                                this.openContainer, world, slotTransactions);
                    }
                } else {
                    Lantern.getLogger().warn("Unknown slot index {} in container {}", slotIndex, this.openContainer);
                }
            }
            if (event != null) {
                this.finishInventoryEvent(event);
            }
        } else if (mode == 3) {
            final Cause cause = Cause.builder().named(NamedCause.SOURCE, this.player).build();
            final ItemStackSnapshot oldItem = LanternItemStack.toSnapshot(this.cursorItem);
            Transaction<ItemStackSnapshot> cursorTransaction = null;

            Optional<GameMode> gameMode = this.player.get(Keys.GAME_MODE);
            if (gameMode.isPresent() && gameMode.get().equals(GameModes.CREATIVE)
                    && this.cursorItem == null) {
                Optional<LanternSlot> optSlot = this.openContainer.getSlotAt(slotIndex);
                if (optSlot.isPresent()) {
                    final LanternSlot slot = optSlot.get();
                    ItemStack stack = slot.peek().orElse(null);
                    if (stack != null) {
                        stack.setQuantity(stack.getMaxStackQuantity());
                        cursorTransaction = new Transaction<>(oldItem, stack.createSnapshot());
                    }
                } else {
                    Lantern.getLogger().warn("Unknown slot index {} in container {}", slotIndex, this.openContainer);
                }
            }
            if (cursorTransaction == null) {
                cursorTransaction = new Transaction<>(oldItem, oldItem);
            }

            ClickInventoryEvent.Middle event = SpongeEventFactory.createClickInventoryEventMiddle(
                    cause, cursorTransaction, this.openContainer, new ArrayList<>());
            this.finishInventoryEvent(event);
        }
    }

    private Entity createDroppedItem(ItemStackSnapshot snapshot) {
        final Entity entity = this.player.getWorld().createEntity(EntityTypes.ITEM, this.player.getPosition().add(0, 0.5, 0));
        entity.offer(Keys.REPRESENTED_ITEM, snapshot);
        return entity;
    }

    private void finishSpawnEntityEvent(SpawnEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final Cause cause = Cause.source(event).build();
        for (Entity entity : event.getEntities()) {
            entity.getWorld().spawnEntity(entity, cause);
        }
    }

    private void finishInventoryEvent(ClickInventoryEvent event) {
        final List<SlotTransaction> slotTransactions = event.getTransactions();
        Sponge.getEventManager().post(event);
        if (!event.isCancelled()) {
            if (!(event instanceof ClickInventoryEvent.Creative)) {
                final Transaction<ItemStackSnapshot> cursorTransaction = event.getCursorTransaction();
                if (!cursorTransaction.isValid()) {
                    this.updateCursorItem();
                } else {
                    this.setCursorItem(cursorTransaction.getFinal().createStack());
                }
            }
            for (SlotTransaction slotTransaction : slotTransactions) {
                if (slotTransaction.isValid()) {
                    slotTransaction.getSlot().set(slotTransaction.getFinal().createStack());
                } else {
                    // Force the slot to update
                    this.openContainer.queueSlotChange(slotTransaction.getSlot());
                }
            }
            if (event instanceof SpawnEntityEvent) {
                this.finishSpawnEntityEvent((SpawnEntityEvent) event);
            }
        } else {
            this.updateCursorItem();
            for (SlotTransaction slotTransaction : slotTransactions) {
                // Force the slot to update
                this.openContainer.queueSlotChange(slotTransaction.getSlot());
            }
        }
    }
}
