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

import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.event.LanternEventHelper;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.container.FurnaceInventoryContainer;
import org.lanternpowered.server.inventory.entity.HumanInventoryView;
import org.lanternpowered.server.inventory.entity.HumanMainInventory;
import org.lanternpowered.server.inventory.entity.LanternHotbar;
import org.lanternpowered.server.inventory.entity.LanternPlayerInventory;
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.lanternpowered.server.item.recipe.crafting.CraftingMatrix;
import org.lanternpowered.server.item.recipe.crafting.ExtendedCraftingResult;
import org.lanternpowered.server.item.recipe.crafting.MatrixResult;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInClickRecipe;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInClickWindow;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInCreativeWindowAction;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInDisplayedRecipe;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInDropHeldItem;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutCloseWindow;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutDisplayRecipe;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetWindowSlot;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.crafting.CraftingGridInventory;
import org.spongepowered.api.item.inventory.crafting.CraftingInventory;
import org.spongepowered.api.item.inventory.crafting.CraftingOutput;
import org.spongepowered.api.item.inventory.slot.FuelSlot;
import org.spongepowered.api.item.inventory.slot.InputSlot;
import org.spongepowered.api.item.inventory.slot.OutputSlot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.recipe.crafting.CraftingResult;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public boolean setOpenContainer(@Nullable LanternContainer container, Cause cause) {
        return setRawOpenContainer(container, cause, true, false);
    }

    public boolean setRawOpenContainer(@Nullable LanternContainer container, Cause cause) {
        return setRawOpenContainer(container, cause, false, false);
    }

    public void handleWindowClose(MessagePlayInOutCloseWindow message) {
        if (this.openContainer == null || message.getWindow() != this.openContainer.windowId) {
            return;
        }
        final Cause cause = Cause.source(this.player).build();
        player.getContainerSession().setRawOpenContainer(null, cause, false, true);
    }

    /**
     * Sets the open container.
     *
     * @param container The container
     */
    private boolean setRawOpenContainer(@Nullable LanternContainer container, Cause cause, boolean sendClose, boolean client) {
        if (this.openContainer != container) {
            ItemStackSnapshot cursorItemSnapshot = LanternItemStack.toSnapshot(this.cursorItem);
            if (this.openContainer != null) {
                final InteractInventoryEvent.Close event = SpongeEventFactory.createInteractInventoryEventClose(
                        cause, new Transaction<>(cursorItemSnapshot, ItemStackSnapshot.NONE), this.openContainer);
                Sponge.getEventManager().post(event);
                if (event.isCancelled()) {
                    // Stop the client from closing the container, resend the open message
                    if (client) {
                        // This can't be done to the player inventory, player inventory uses index 0
                        if (this.openContainer.windowId != 0) {
                            this.openContainer.openInventoryForAndInitialize(this.player);
                            return false;
                        }
                    } else {
                        // Just return
                        return false;
                    }
                }
                final Transaction<ItemStackSnapshot> transaction = event.getCursorTransaction();
                if (transaction.isValid()) {
                    cursorItemSnapshot = transaction.getFinal();
                }
                if (!cursorItemSnapshot.isEmpty()) {
                    final ItemStackSnapshot cursorItemSnapshot0 = cursorItemSnapshot;
                    LanternEventHelper.fireDropItemEventDispense(cause, entities -> entities.add(
                            LanternEventHelper.createDroppedItem(this.player.getLocation(), cursorItemSnapshot0)));
                }
                // Close the inventory
                this.openContainer.close();
            } else {
                sendClose = false;
            }
            if (container != null) {
                final InteractInventoryEvent.Open event = SpongeEventFactory.createInteractInventoryEventOpen(
                        cause, new Transaction<>(cursorItemSnapshot, cursorItemSnapshot), container);
                Sponge.getEventManager().post(event);
                if (event.isCancelled()) {
                    this.cursorItem = LanternItemStack.toNullable(cursorItemSnapshot);
                    container.removeViewer(this.player, container);
                    return false;
                }
                final Transaction<ItemStackSnapshot> transaction = event.getCursorTransaction();
                if (transaction.isValid()) {
                    cursorItemSnapshot = transaction.getFinal();
                    this.cursorItem = LanternItemStack.toNullable(cursorItemSnapshot);
                }
                // The container is being used for the first time
                if (container.getRawViewers().isEmpty()) {
                    container.addSlotTrackers();
                }
                sendClose = false;
                container.addViewer(this.player, container);
                container.viewers.add(this.player);
                container.openInventoryForAndInitialize(this.player);
                updateCursorItem();
            } else {
                this.cursorItem = LanternItemStack.toNullable(cursorItemSnapshot);
            }
            if (sendClose && this.openContainer.windowId != -1) {
                this.player.getConnection().send(new MessagePlayInOutCloseWindow(this.openContainer.windowId));
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
        updateCursorItem();
    }

    private void updateCursorItem() {
        this.player.getConnection().send(
                new MessagePlayOutSetWindowSlot(-1, -1, this.cursorItem));
    }

    @Nullable
    public ItemStack getCursorItem() {
        return this.cursorItem;
    }

    public void handleRecipeClick(MessagePlayInClickRecipe message) {
        final int windowId = message.getWindowId();
        if (this.openContainer == null) {
            if (message.getWindowId() == 0) {
                setRawOpenContainer(this.player.getInventoryContainer(), Cause.source(this.player).build());
            } else {
                return;
            }
        } else if (windowId != this.openContainer.windowId) {
            return;
        }
        // Just display the recipe for now, all the other behavior will be implemented later,
        // this requires recipes to be added first
        this.player.getConnection().send(new MessagePlayOutDisplayRecipe(message.getWindowId(), message.getRecipeId()));
    }

    public void handleWindowCreativeClick(MessagePlayInCreativeWindowAction message) {
        if (this.openContainer == null) {
            setRawOpenContainer(this.player.getInventoryContainer(), Cause.source(this.player).build());
        }
        ItemStack itemStack = LanternItemStack.toNullable(message.getItemStack());
        int slotIndex = message.getSlot();
        if (slotIndex < 0) {
            if (itemStack != null) {
                final Cause cause = Cause.builder().named("SpawnCause", SpawnCause.builder()
                        .type(SpawnTypes.DROPPED_ITEM).build()).named(NamedCause.SOURCE, this.player).build();
                LanternEventHelper.fireDropItemEventDispense(cause, entities ->
                        entities.add(LanternEventHelper.createDroppedItem(this.player.getLocation(), itemStack.createSnapshot())));
            }
        } else {
            final Optional<LanternSlot> optSlot = this.openContainer.playerInventory.getSlotAt(slotIndex);
            if (optSlot.isPresent()) {
                final Cause cause = Cause.builder().named(NamedCause.SOURCE, this.player).build();
                final LanternSlot slot = optSlot.get();

                final PeekSetTransactionsResult result = slot.peekSetTransactions(itemStack);

                // We do not know the remaining stack in the cursor,
                // so just use none as new item
                final Transaction<ItemStackSnapshot> cursorTransaction = new Transaction<>(
                        LanternItemStack.toSnapshot(itemStack), ItemStackSnapshot.NONE);

                final ClickInventoryEvent.Creative event = SpongeEventFactory.createClickInventoryEventCreative(
                        cause, cursorTransaction, this.openContainer, result.getTransactions());
                finishInventoryEvent(event);
            } else {
                Lantern.getLogger().warn("Unknown slot index {} in container {}", slotIndex, this.openContainer);
            }
        }
    }

    public void handleItemDrop(MessagePlayInDropHeldItem message) {
        final LanternSlot slot = this.player.getInventory().getHotbar().getSelectedSlot();
        final Optional<ItemStack> itemStack = message.isFullStack() ? slot.peek() : slot.peek(1);

        if (itemStack.isPresent()) {
            final Cause cause = Cause.builder()
                    .named("SpawnCause", SpawnCause.builder().type(SpawnTypes.DROPPED_ITEM).build())
                    .named(NamedCause.SOURCE, this.player)
                    .named("Slot", slot)
                    .build();

            final List<Entity> entities = new ArrayList<>();
            entities.add(LanternEventHelper.createDroppedItem(this.player.getLocation(), itemStack.get().createSnapshot()));

            final SpawnEntityEvent event = SpongeEventFactory.createDropItemEventDispense(cause, entities);
            Sponge.getEventManager().post(event);

            if (!event.isCancelled()) {
                if (message.isFullStack()) {
                    slot.poll();
                } else {
                    slot.poll(1);
                }
                LanternEventHelper.finishSpawnEntityEvent(event);
            }
        }
    }

    private void resetDrag() {
        this.dragState = -1;
        this.dragSlots.clear();
    }

    public void handleDisplayedRecipe(MessagePlayInDisplayedRecipe message) {
        // A quite useless packet, maybe only useful to track the inventory state
        if (this.openContainer == null) {
            setRawOpenContainer(this.player.getInventoryContainer(), Cause.source(this.player).build());
        }
    }

    private void updateCraftingGrid(CraftingInventory craftingInventory, MatrixResult matrixResult,
            List<SlotTransaction> transactions) {
        final CraftingMatrix matrix = matrixResult.getCraftingMatrix();
        final CraftingGridInventory grid = craftingInventory.getCraftingGrid();
        for (int x = 0; x < matrix.width(); x++) {
            for (int y = 0; y < matrix.height(); y++) {
                final ItemStack itemStack = matrix.get(x, y);
                final Slot slot = grid.getSlot(x, y).get();
                transactions.add(new SlotTransaction(slot, slot.peek().map(LanternItemStackSnapshot::wrap)
                        .orElse(LanternItemStackSnapshot.none()), LanternItemStackSnapshot.wrap(itemStack)));
            }
        }

        final Cause cause = Cause.builder()
                .named("SpawnCause", SpawnCause.builder().type(SpawnTypes.DROPPED_ITEM).build())
                .named(NamedCause.SOURCE, this.player)
                .build();
        final Location<World> location = this.player.getLocation();
        final List<Entity> entities = matrixResult.getRest().stream()
                .map(itemStack -> LanternEventHelper.createDroppedItem(location, LanternItemStackSnapshot.wrap(itemStack)))
                .collect(Collectors.toList());
        final SpawnEntityEvent event = SpongeEventFactory.createDropItemEventDispense(cause, entities);
        Sponge.getEventManager().post(event);
        LanternEventHelper.finishSpawnEntityEvent(event);
    }

    public void handleWindowClick(MessagePlayInClickWindow message) {
        final int windowId = message.getWindowId();
        if (this.openContainer == null) {
            if (message.getWindowId() == 0) {
                setRawOpenContainer(this.player.getInventoryContainer(), Cause.source(this.player).build());
            } else {
                return;
            }
        } else if (windowId != this.openContainer.windowId) {
            return;
        }
        final int button = message.getButton();
        final int mode = message.getMode();
        final int slotIndex = message.getSlot();

        // Drag mode
        if (mode == 5) {
            if (this.cursorItem == null) {
                resetDrag();
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
                            finishInventoryEvent(event);
                            resetDrag();
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
                            finishInventoryEvent(event);
                            resetDrag();
                        }
                    } else {
                        // Add slot
                        final Optional<LanternSlot> optSlot = this.openContainer.getSlotAt(slotIndex);
                        if (optSlot.isPresent()) {
                            final LanternSlot slot = optSlot.get();
                            if (!(slot instanceof OutputSlot) && slot.isValidItem(this.cursorItem) && (slot.getRawItemStack() == null ||
                                    ((LanternItemStack) this.cursorItem).similarTo(slot.getRawItemStack())) && !this.dragSlots.contains(slot)) {
                                this.dragSlots.add(slot);
                            }
                        }
                    }
                } else {
                    resetDrag();
                }
            } else if (button == 0) {
                this.dragState = 0;
            } else if (button == 4) {
                this.dragState = 1;
            }
        } else if (this.dragState != -1) {
            resetDrag();
        // Left/right click inside the inventory
        } else if (mode == 0 && (button == 0 || button == 1) && slotIndex != -999) {
            final Optional<LanternSlot> optSlot = this.openContainer.getSlotAt(slotIndex);
            if (optSlot.isPresent()) {
                final LanternSlot slot = optSlot.get();
                final Cause cause = Cause.builder().named(NamedCause.SOURCE, this.player).build();

                // Crafting slots have special click behavior
                if (slot instanceof CraftingOutput) {
                    final List<SlotTransaction> transactions = new ArrayList<>();
                    Transaction<ItemStackSnapshot> cursorTransaction;

                    final AbstractInventory parent = slot.parent();
                    if (parent instanceof CraftingInventory) {
                        ClickInventoryEvent event;

                        final CraftingInventory inventory = (CraftingInventory) parent;
                        final Optional<ExtendedCraftingResult> optResult = Lantern.getRegistry().getCraftingRecipeRegistry()
                                .getExtendedResult(inventory.getCraftingGrid(), this.player.getWorld());
                        final ItemStackSnapshot originalCursorItem = LanternItemStack.toSnapshot(this.cursorItem);
                        if (optResult.isPresent()) {
                            final CraftingResult result = optResult.get().getResult();
                            final ItemStackSnapshot resultItem = result.getResult();

                            int quantity = -1;
                            if (this.cursorItem == null) {
                                quantity = resultItem.getQuantity();
                            } else if (LanternItemStack.areSimilar(resultItem.createStack(), this.cursorItem)) {
                                final int quantity1 = resultItem.getQuantity() + this.cursorItem.getQuantity();
                                if (quantity1 < this.cursorItem.getMaxStackQuantity()) {
                                    quantity = quantity1;
                                }
                            }
                            if (quantity == -1) {
                                cursorTransaction = new Transaction<>(originalCursorItem, originalCursorItem);
                                transactions.add(new SlotTransaction(slot, resultItem, resultItem));
                            } else {
                                final LanternItemStack itemStack = (LanternItemStack) resultItem.createStack();
                                itemStack.setQuantity(quantity);
                                cursorTransaction = new Transaction<>(originalCursorItem, itemStack.createSnapshot());
                                transactions.add(new SlotTransaction(slot, resultItem, ItemStackSnapshot.NONE));
                                updateCraftingGrid(inventory, optResult.get().getMatrixResult(1), transactions);
                            }
                        } else {
                            cursorTransaction = new Transaction<>(originalCursorItem, originalCursorItem);
                            // No actual transaction, there shouldn't have been a item in the crafting result slot
                            transactions.add(new SlotTransaction(slot, ItemStackSnapshot.NONE, ItemStackSnapshot.NONE));
                        }

                        if (button == 0) {
                            event = SpongeEventFactory.createClickInventoryEventPrimary(cause, cursorTransaction,
                                    this.openContainer, transactions);
                        } else {
                            event = SpongeEventFactory.createClickInventoryEventSecondary(cause, cursorTransaction,
                                    this.openContainer, transactions);
                        }
                        finishInventoryEvent(event);
                        return;
                    } else {
                        Lantern.getLogger().warn("Found a CraftingOutput slot without a CraftingInventory as parent.");
                    }
                }

                ClickInventoryEvent event;
                // Left click
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
                // Right click
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
                            final ItemStackSnapshot oldCursor = this.cursorItem.createSnapshot();
                            int quantity = this.cursorItem.getQuantity() - 1;
                            if (quantity <= 0) {
                                cursorTransaction = new Transaction<>(oldCursor, ItemStackSnapshot.NONE);
                            } else {
                                final ItemStack newCursorItem = this.cursorItem.copy();
                                newCursorItem.setQuantity(quantity);
                                cursorTransaction = new Transaction<>(oldCursor, newCursorItem.createSnapshot());
                            }
                            transactions.addAll(result.getTransactions());
                        } else {
                            final PeekSetTransactionsResult result1 = slot.peekSetTransactions(this.cursorItem);
                            if (result1.getTransactionResult().getType().equals(InventoryTransactionResult.Type.SUCCESS) &&
                                    result1.getTransactionResult().getRejectedItems().isEmpty()) {
                                final Collection<ItemStackSnapshot> replaceItems = result1.getTransactionResult().getReplacedItems();
                                if (!replaceItems.isEmpty()) {
                                    setCursorItem(replaceItems.iterator().next().createStack());
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
                        final ItemStackSnapshot cursorItem = LanternItemStack.toSnapshot(this.cursorItem);
                        cursorTransaction = new Transaction<>(cursorItem, cursorItem);
                    }
                    event = SpongeEventFactory.createClickInventoryEventSecondary(cause, cursorTransaction, this.openContainer, transactions);
                }
                finishInventoryEvent(event);
            } else {
                Lantern.getLogger().warn("Unknown slot index {} in container {}", slotIndex, this.openContainer);
            }
        // Shift + left/right click
        } else if (mode == 1 && (button == 0 || button == 1)) {
            Optional<LanternSlot> optSlot = this.openContainer.getSlotAt(slotIndex);
            if (optSlot.isPresent()) {
                final LanternSlot slot = optSlot.get();
                final ItemStack itemStack = slot.peek().orElse(null);

                final Cause cause = Cause.builder().named(NamedCause.SOURCE, this.player).build();
                final Transaction<ItemStackSnapshot> cursorTransaction;
                final List<SlotTransaction> transactions = new ArrayList<>();

                if (slot instanceof CraftingOutput) {
                    final ItemStackSnapshot cursorItem = LanternItemStack.toSnapshot(this.cursorItem);
                    cursorTransaction = new Transaction<>(cursorItem, cursorItem);

                    final AbstractInventory parent = slot.parent();
                    if (parent instanceof CraftingInventory) {
                        final CraftingInventory inventory = (CraftingInventory) parent;
                        final Optional<ExtendedCraftingResult> optResult = Lantern.getRegistry().getCraftingRecipeRegistry()
                                .getExtendedResult(inventory.getCraftingGrid(), this.player.getWorld());
                        if (optResult.isPresent()) {
                            final ExtendedCraftingResult result = optResult.get();
                            final ItemStackSnapshot resultItem = result.getResult().getResult();

                            int times = result.getMaxTimes();
                            final ItemStack itemStack1 = resultItem.createStack();
                            itemStack1.setQuantity(times * itemStack1.getQuantity());

                            final AbstractMutableInventory targetInventory = this.openContainer.playerInventory
                                    .getInventoryView(HumanInventoryView.REVERSE_MAIN_AND_HOTBAR);
                            PeekOfferTransactionsResult peekResult = targetInventory.peekOfferFastTransactions(itemStack1);

                            if (peekResult.getOfferResult().isSuccess()) {
                                transactions.add(new SlotTransaction(slot, resultItem, ItemStackSnapshot.NONE));

                                final ItemStack restItem = peekResult.getOfferResult().getRest();
                                if (restItem != null) {
                                    final int added = itemStack1.getQuantity() - restItem.getQuantity();
                                    times = added / resultItem.getQuantity();
                                    final int diff = added % resultItem.getQuantity();
                                    if (diff != 0) {
                                        itemStack1.setQuantity(resultItem.getQuantity() * times);
                                        peekResult = targetInventory.peekOfferFastTransactions(itemStack1);
                                        checkState(peekResult.getOfferResult().isSuccess());
                                    }
                                }

                                transactions.addAll(peekResult.getTransactions());
                                updateCraftingGrid(inventory, result.getMatrixResult(times), transactions);
                            }
                        } else {
                            // No actual transaction, there shouldn't have been a item in the crafting result slot
                            transactions.add(new SlotTransaction(slot, ItemStackSnapshot.NONE, ItemStackSnapshot.NONE));
                        }
                    } else {
                        Lantern.getLogger().warn("Found a CraftingOutput slot without a CraftingInventory as parent.");
                        return;
                    }
                } else {
                    final ItemStackSnapshot cursorItem = LanternItemStack.toSnapshot(this.cursorItem);
                    cursorTransaction = new Transaction<>(cursorItem, cursorItem);

                    if (itemStack != null) {
                        final boolean offhand = slot == this.openContainer.playerInventory.getOffhand();
                        final PeekOfferTransactionsResult result = getShiftPeekOfferResult(windowId, slot, itemStack.copy(), offhand);

                        // Force updates if the max stack size on the client and server don't match
                        final int originalMaxStackSize = DefaultStackSizes.getOriginalMaxSize(itemStack.getType());
                        if (itemStack.getMaxStackQuantity() != originalMaxStackSize) {
                            final LanternItemStack tempStack = (LanternItemStack) itemStack.copy();
                            tempStack.setTempMaxQuantity(originalMaxStackSize);
                            final PeekOfferTransactionsResult result1 = getShiftPeekOfferResult(windowId, slot, tempStack, offhand);
                            result1.getTransactions().forEach(transaction -> this.openContainer.queueSlotChange(transaction.getSlot()));
                        }
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
                        // Fix client glitches
                        // TODO: Remove when refactoring shift clicking
                        if (this.openContainer instanceof FurnaceInventoryContainer) {
                            final LanternOrderedInventory inventory = this.openContainer.openInventory;
                            this.openContainer.queueSlotChange(inventory.query(InputSlot.class).<InputSlot>first());
                            this.openContainer.queueSlotChange(inventory.query(FuelSlot.class).<FuelSlot>first());
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

                finishInventoryEvent(event);
            } else {
                Lantern.getLogger().warn("Unknown slot index {} in container {}", slotIndex, this.openContainer);
            }
        // Double click
        } else if (mode == 6 && button == 0) {
            final Optional<LanternSlot> optSlot = this.openContainer.getSlotAt(slotIndex);
            if (optSlot.isPresent()) {
                final Cause cause = Cause.builder().named(NamedCause.SOURCE, this.player).build();
                final ItemStackSnapshot oldItem = LanternItemStack.toSnapshot(this.cursorItem);
                ItemStackSnapshot newItem = oldItem;

                final List<SlotTransaction> transactions = new ArrayList<>();

                if (this.cursorItem != null && !(optSlot.get() instanceof OutputSlot)) {
                    final ItemStack cursorItem = this.cursorItem.copy();
                    int quantity = cursorItem.getQuantity();
                    final int maxQuantity = cursorItem.getMaxStackQuantity();
                    if (quantity < maxQuantity) {
                        final AbstractMutableInventory inventory;
                        if (windowId != 0) {
                            inventory = new AbstractChildrenInventory(null, null, Arrays.asList(
                                    this.openContainer.openInventory, this.openContainer.playerInventory
                                            .getInventoryView(HumanInventoryView.PRIORITY_MAIN_AND_HOTBAR)));
                        } else {
                            inventory = this.openContainer.playerInventory
                                    .getInventoryView(HumanInventoryView.ALL_PRIORITY_MAIN);
                        }

                        // Try first to get enough unfinished stacks
                        PeekPollTransactionsResult peekResult = inventory.peekPollTransactions(maxQuantity - quantity, stack ->
                                stack.getQuantity() < stack.getMaxStackQuantity() &&
                                        ((LanternItemStack) cursorItem).similarTo(stack)).orElse(null);
                        if (peekResult != null) {
                            quantity += peekResult.getPeekedItem().getQuantity();
                            transactions.addAll(peekResult.getTransactions());
                        }
                        // Get the last items for the stack from a full stack
                        if (quantity <= maxQuantity) {
                            peekResult = this.openContainer.peekPollTransactions(maxQuantity - quantity, stack ->
                                    stack.getQuantity() >= stack.getMaxStackQuantity() &&
                                            ((LanternItemStack) cursorItem).similarTo(stack)).orElse(null);
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

                finishInventoryEvent(event);
            } else {
                Lantern.getLogger().warn("Unknown slot index {} in container {}", slotIndex, this.openContainer);
            }
        // Number keys
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
                    finishInventoryEvent(event);
                } else {
                    Lantern.getLogger().warn("Unknown hotbar slot index {}", mode);
                }
            } else {
                Lantern.getLogger().warn("Unknown slot index {} in container {}", slotIndex, this.openContainer);
            }
        // Left/right click outside inventory
        // Drop or control drop click a slot
        } else if ((mode == 4 || mode == 0) && (button == 0 || button == 1)) {
            ClickInventoryEvent.Drop event = null;

            final Cause cause = Cause.builder().named("SpawnCause", SpawnCause.builder()
                    .type(SpawnTypes.DROPPED_ITEM).build()).named(NamedCause.SOURCE, this.player).build();
            final List<Entity> entities = new ArrayList<>();

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
                        entities.add(LanternEventHelper.createDroppedItem(this.player.getLocation(), LanternItemStack.toSnapshot(stack)));
                    } else {
                        entities.add(LanternEventHelper.createDroppedItem(this.player.getLocation(), oldItem));
                    }
                }
                cursorTransaction = new Transaction<>(oldItem, newItem);
                if (button == 0) {
                    event = SpongeEventFactory.createClickInventoryEventDropOutsidePrimary(cause, cursorTransaction, entities,
                            this.openContainer, slotTransactions);
                } else {
                    event = SpongeEventFactory.createClickInventoryEventDropOutsideSecondary(cause, cursorTransaction, entities,
                            this.openContainer, slotTransactions);
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
                        itemStack.setQuantity(itemStack.getQuantity() - transactions.get(0).getFinal().getQuantity());
                        entities.add(LanternEventHelper.createDroppedItem(this.player.getLocation(), itemStack.createSnapshot()));
                    }
                    if (button == 0) {
                        event = SpongeEventFactory.createClickInventoryEventDropSingle(cause, cursorTransaction, entities,
                                this.openContainer, slotTransactions);
                    } else {
                        event = SpongeEventFactory.createClickInventoryEventDropFull(cause, cursorTransaction, entities,
                                this.openContainer, slotTransactions);
                    }
                } else {
                    Lantern.getLogger().warn("Unknown slot index {} in container {}", slotIndex, this.openContainer);
                }
            }
            if (event != null) {
                finishInventoryEvent(event);
            }
        // Middle lock a slot
        } else if (mode == 3) {
            final Cause cause = Cause.builder().named(NamedCause.SOURCE, this.player).build();
            final ItemStackSnapshot oldItem = LanternItemStack.toSnapshot(this.cursorItem);
            Transaction<ItemStackSnapshot> cursorTransaction = null;

            final Optional<GameMode> gameMode = this.player.get(Keys.GAME_MODE);
            if (gameMode.isPresent() && gameMode.get().equals(GameModes.CREATIVE)
                    && this.cursorItem == null) {
                final Optional<LanternSlot> optSlot = this.openContainer.getSlotAt(slotIndex);
                if (optSlot.isPresent()) {
                    final LanternSlot slot = optSlot.get();
                    final ItemStack stack = slot.peek().orElse(null);
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

            final ClickInventoryEvent.Middle event = SpongeEventFactory.createClickInventoryEventMiddle(
                    cause, cursorTransaction, this.openContainer, new ArrayList<>());
            finishInventoryEvent(event);
        } else {
            Lantern.getLogger().warn("Unknown action: mode: {}, button: {}", message.getMode(), message.getButton());
        }
    }

    private PeekOfferTransactionsResult getShiftPeekOfferResult(int windowId, LanternSlot slot,
            ItemStack itemStack, boolean offhand) {
        final LanternPlayerInventory playerInventory = this.openContainer.playerInventory;
        final HumanMainInventory mainInventory = playerInventory.getMain();
        AbstractInventory inventory;
        PeekOfferTransactionsResult result;
        checkNotNull(this.openContainer);
        if ((windowId != 0 && this.openContainer.openInventory.getSlotIndex(slot) != -1) ||
                (windowId == 0 && !mainInventory.isChild(slot) && !offhand)) {
            if (slot.isReverseShiftClickOfferOrder()) {
                inventory = playerInventory.getInventoryView(HumanInventoryView.REVERSE_MAIN_AND_HOTBAR);
            } else {
                inventory = playerInventory.getInventoryView(HumanInventoryView.PRIORITY_MAIN_AND_HOTBAR);
            }
            result = inventory.peekOfferFastTransactions(itemStack);
        } else {
            // In case the we can't shift click to the top inventory, the inventory will be used in the else statement
            inventory = this.openContainer.openInventory.query(inv -> !mainInventory.isChild(inv) && inv instanceof Slot &&
                    ((LanternSlot) inv).doesAllowShiftClickOffer() && !(inv instanceof OutputSlot), false);
            result = inventory.peekOfferFastTransactions(itemStack);
            if (result.getOfferResult().getRest() != null) {
                if (slot.parent() instanceof LanternHotbar || offhand) {
                    inventory = playerInventory.getInventoryView(HumanInventoryView.MAIN);
                } else {
                    inventory = playerInventory.getHotbar();
                }
                PeekOfferTransactionsResult result1 = inventory.peekOfferFastTransactions(result.getOfferResult().getRest());
                if (result1.getOfferResult().isSuccess()) {
                    result1.getTransactions().addAll(result.getTransactions());
                    result = result1;
                }
            }
        }
        return result;
    }

    private void finishInventoryEvent(ChangeInventoryEvent event) {
        final List<SlotTransaction> slotTransactions = event.getTransactions();
        Sponge.getEventManager().post(event);
        if (!event.isCancelled()) {
            if (!(event instanceof ClickInventoryEvent.Creative) && event instanceof ClickInventoryEvent) {
                final Transaction<ItemStackSnapshot> cursorTransaction = ((ClickInventoryEvent) event).getCursorTransaction();
                if (!cursorTransaction.isValid()) {
                    updateCursorItem();
                } else {
                    setCursorItem(cursorTransaction.getFinal().createStack());
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
                LanternEventHelper.finishSpawnEntityEvent((SpawnEntityEvent) event);
            }
        } else {
            updateCursorItem();
            for (SlotTransaction slotTransaction : slotTransactions) {
                // Force the slot to update
                this.openContainer.queueSlotChange(slotTransaction.getSlot());
            }
        }
    }
}
