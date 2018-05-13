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
package org.lanternpowered.server.inventory.behavior;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.Streams;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.event.CauseStack;
import org.lanternpowered.server.event.LanternEventHelper;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.AbstractInventory;
import org.lanternpowered.server.inventory.AbstractInventorySlot;
import org.lanternpowered.server.inventory.AbstractOrderedInventory;
import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.IInventory;
import org.lanternpowered.server.inventory.LanternContainer;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.inventory.LanternItemStackSnapshot;
import org.lanternpowered.server.inventory.PeekedOfferTransactionResult;
import org.lanternpowered.server.inventory.PeekedPollTransactionResult;
import org.lanternpowered.server.inventory.PeekedSetTransactionResult;
import org.lanternpowered.server.inventory.PlayerInventoryContainer;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.inventory.client.ClientSlot;
import org.lanternpowered.server.inventory.client.PlayerClientContainer;
import org.lanternpowered.server.inventory.transformation.InventoryTransforms;
import org.lanternpowered.server.inventory.vanilla.LanternHotbarInventory;
import org.lanternpowered.server.item.recipe.crafting.CraftingMatrix;
import org.lanternpowered.server.item.recipe.crafting.ExtendedCraftingResult;
import org.lanternpowered.server.item.recipe.crafting.MatrixResult;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.crafting.CraftingGridInventory;
import org.spongepowered.api.item.inventory.crafting.CraftingInventory;
import org.spongepowered.api.item.inventory.crafting.CraftingOutput;
import org.spongepowered.api.item.inventory.slot.OutputSlot;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.recipe.crafting.CraftingResult;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public class VanillaContainerInteractionBehavior extends AbstractContainerInteractionBehavior {

    private final LanternContainer container;

    public VanillaContainerInteractionBehavior(LanternContainer container) {
        this.container = container;
    }

    /**
     * Sets the cursor item.
     *
     * @param cursorItem The cursor item
     */
    private void setCursorItem(@Nullable ItemStack cursorItem) {
        this.container.getCursorSlot().setRawItemStack(cursorItem);
    }

    /**
     * Gets the {@link ItemStack} in the cursor.
     *
     * @return The cursor item
     */
    @Nullable
    private ItemStack getCursorItem() {
        return this.container.getCursorSlot().getRawItemStack();
    }

    @Override
    public void handleShiftClick(ClientContainer clientContainer, ClientSlot clientSlot, MouseButton mouseButton) {
        final LanternPlayer player = clientContainer.getPlayer();
        if (player != this.container.getPlayerInventory().getCarrier().orElse(null) ||
                !(clientSlot instanceof ClientSlot.Slot) || mouseButton == MouseButton.MIDDLE) {
            return;
        }
        final AbstractInventorySlot slot = ((ClientSlot.Slot) clientSlot).getSlot();
        final ItemStack itemStack = slot.peek().orElse(null);

        final Transaction<ItemStackSnapshot> cursorTransaction;
        final List<SlotTransaction> transactions = new ArrayList<>();

        if (slot instanceof CraftingOutput) {
            final ItemStackSnapshot cursorItem = LanternItemStack.toSnapshot(getCursorItem());
            cursorTransaction = new Transaction<>(cursorItem, cursorItem);

            final AbstractInventory parent = slot.parent();
            if (parent instanceof CraftingInventory) {
                final CraftingInventory inventory = (CraftingInventory) parent;
                final Optional<ExtendedCraftingResult> optResult = Lantern.getRegistry().getCraftingRecipeRegistry()
                        .getExtendedResult(inventory.getCraftingGrid(), player.getWorld());
                if (optResult.isPresent()) {
                    final ExtendedCraftingResult result = optResult.get();
                    final ItemStackSnapshot resultItem = result.getResult().getResult();

                    int times = result.getMaxTimes();
                    final ItemStack itemStack1 = resultItem.createStack();
                    itemStack1.setQuantity(times * itemStack1.getQuantity());

                    final AbstractInventory targetInventory = (AbstractInventory) this.container.getPlayerInventory()
                            .getMain().transform(InventoryTransforms.REVERSE);
                    PeekedOfferTransactionResult peekResult = targetInventory.peekOffer(itemStack1);

                    if (peekResult.isSuccess()) {
                        transactions.add(new SlotTransaction(slot, resultItem, ItemStackSnapshot.NONE));

                        final ItemStack rejectedItem = peekResult.getRejectedItem().orElse(null);
                        if (rejectedItem != null) {
                            final int added = itemStack1.getQuantity() - rejectedItem.getQuantity();
                            times = added / resultItem.getQuantity();
                            final int diff = added % resultItem.getQuantity();
                            if (diff != 0) {
                                itemStack1.setQuantity(resultItem.getQuantity() * times);
                                peekResult = targetInventory.peekOffer(itemStack1);
                                checkState(peekResult.isSuccess());
                            }
                        }

                        transactions.addAll(peekResult.getTransactions());
                        updateCraftingGrid(player, inventory, result.getMatrixResult(times), transactions);
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
            final ItemStackSnapshot cursorItem = LanternItemStack.toSnapshot(getCursorItem());
            cursorTransaction = new Transaction<>(cursorItem, cursorItem);

            if (itemStack != null) {
                final IInventory target = this.container.getOpenInventory().getShiftClickBehavior().getTarget(this.container, slot);
                final PeekedOfferTransactionResult result = target.peekOffer(itemStack.copy());
                if (result.isSuccess()) {
                    transactions.addAll(result.getTransactions());
                    final ItemStack rejectedItem = result.getRejectedItem().orElse(null);
                    if (rejectedItem != null) {
                        slot.peekPoll(itemStack.getQuantity() - rejectedItem.getQuantity(), stack -> true)
                                .ifPresent(peekResult -> transactions.addAll(peekResult.getTransactions()));
                    } else {
                        slot.peekPoll(stack -> true)
                                .ifPresent(peekResult -> transactions.addAll(peekResult.getTransactions()));
                    }
                }
            }
        }

        final List<SlotTransaction> transactions1 = this.container.transformSlots(transactions);
        final CauseStack causeStack = CauseStack.current();
        final ClickInventoryEvent.Shift event;
        if (mouseButton == MouseButton.LEFT) {
            event = SpongeEventFactory.createClickInventoryEventShiftPrimary(
                    causeStack.getCurrentCause(), cursorTransaction, this.container, transactions1);
        } else {
            event = SpongeEventFactory.createClickInventoryEventShiftSecondary(
                    causeStack.getCurrentCause(), cursorTransaction, this.container, transactions1);
        }
        finishInventoryEvent(event);
    }

    @Override
    public void handleDoubleClick(ClientContainer clientContainer, ClientSlot clientSlot) {
        final LanternPlayer player = clientContainer.getPlayer();
        if (player != this.container.getPlayerInventory().getCarrier().orElse(null) ||
                !(clientSlot instanceof ClientSlot.Slot)) {
            return;
        }
        final AbstractSlot slot = ((ClientSlot.Slot) clientSlot).getSlot();
        final ItemStackSnapshot oldItem = LanternItemStack.toSnapshot(getCursorItem());
        ItemStackSnapshot newItem = oldItem;

        final List<SlotTransaction> transactions = new ArrayList<>();
        if (getCursorItem() != null && !(slot instanceof OutputSlot)) {
            final ItemStack cursorItem = getCursorItem().copy();
            int quantity = cursorItem.getQuantity();
            final int maxQuantity = cursorItem.getMaxStackQuantity();
            if (quantity < maxQuantity) {
                final AbstractInventory inventory;
                if (clientContainer instanceof PlayerClientContainer) {
                    inventory = this.container.getPlayerInventory();
                } else {
                    inventory = AbstractOrderedInventory.viewBuilder()
                            .inventory(this.container.getOpenInventory())
                            .inventory(this.container.getPlayerInventory().getMain())
                            .build();
                }

                // Try first to get enough unfinished stacks
                PeekedPollTransactionResult peekResult = inventory.peekPoll(maxQuantity - quantity, stack ->
                        stack.getQuantity() < stack.getMaxStackQuantity() && ((LanternItemStack) cursorItem).similarTo(stack)).orElse(null);
                if (peekResult != null) {
                    quantity += peekResult.getPolledItem().getQuantity();
                    transactions.addAll(peekResult.getTransactions());
                }
                // Get the last items for the stack from a full stack
                if (quantity <= maxQuantity) {
                    peekResult = this.container.peekPoll(maxQuantity - quantity, stack ->
                            stack.getQuantity() >= stack.getMaxStackQuantity() && ((LanternItemStack) cursorItem).similarTo(stack)).orElse(null);
                    if (peekResult != null) {
                        quantity += peekResult.getPolledItem().getQuantity();
                        transactions.addAll(peekResult.getTransactions());
                    }
                }
                cursorItem.setQuantity(quantity);
                newItem = cursorItem.createSnapshot();
            }
        }

        final CauseStack causeStack = CauseStack.current();

        final Transaction<ItemStackSnapshot> cursorTransaction = new Transaction<>(oldItem, newItem);
        final ClickInventoryEvent.Double event = SpongeEventFactory.createClickInventoryEventDouble(
                causeStack.getCurrentCause(), cursorTransaction, this.container,
                this.container.transformSlots(transactions));
        finishInventoryEvent(event);
    }

    @Override
    public void handleClick(ClientContainer clientContainer, @Nullable ClientSlot clientSlot, MouseButton mouseButton) {
        final LanternPlayer player = clientContainer.getPlayer();
        if (player != this.container.getPlayerInventory().getCarrier().orElse(null) ||
                (clientSlot != null && !(clientSlot instanceof ClientSlot.Slot))) {
            return;
        }
        final CauseStack causeStack = CauseStack.current();
        if (clientSlot == null) {
            causeStack.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.DROPPED_ITEM);

            final List<Entity> entities = new ArrayList<>();
            final Transaction<ItemStackSnapshot> cursorTransaction;

            // Clicking outside the container
            ItemStackSnapshot oldItem = ItemStackSnapshot.NONE;
            ItemStackSnapshot newItem = ItemStackSnapshot.NONE;
            if (getCursorItem() != null) {
                oldItem = getCursorItem().createSnapshot();
                final ItemStackSnapshot droppedItem;
                if (mouseButton != MouseButton.LEFT) {
                    final ItemStack stack = getCursorItem().copy();
                    stack.setQuantity(stack.getQuantity() - 1);
                    newItem = LanternItemStack.toSnapshot(stack);
                    stack.setQuantity(1);
                    droppedItem = LanternItemStack.toSnapshot(stack);
                } else {
                    droppedItem = oldItem;
                }
                LanternEventHelper.handlePreDroppedItemSpawning(player.getTransform(), droppedItem).ifPresent(entities::add);
            }
            cursorTransaction = new Transaction<>(oldItem, newItem);
            final ClickInventoryEvent.Drop event;
            if (mouseButton == MouseButton.LEFT) {
                event = SpongeEventFactory.createClickInventoryEventDropOutsidePrimary(
                        causeStack.getCurrentCause(), cursorTransaction, entities, this.container, new ArrayList<>());
            } else {
                event = SpongeEventFactory.createClickInventoryEventDropOutsideSecondary(
                        causeStack.getCurrentCause(), cursorTransaction, entities, this.container, new ArrayList<>());
            }
            finishInventoryEvent(event);
            return;
        }
        // Clicking inside the container
        final AbstractSlot slot = ((ClientSlot.Slot) clientSlot).getSlot();
        if (mouseButton == MouseButton.MIDDLE) {
            final ItemStackSnapshot oldItem = LanternItemStack.toSnapshot(getCursorItem());
            Transaction<ItemStackSnapshot> cursorTransaction = null;

            final Optional<GameMode> gameMode = player.get(Keys.GAME_MODE);
            if (gameMode.isPresent() && gameMode.get().equals(GameModes.CREATIVE) && getCursorItem() == null) {
                final ItemStack stack = slot.peek().orElse(null);
                if (stack != null) {
                    stack.setQuantity(stack.getMaxStackQuantity());
                    cursorTransaction = new Transaction<>(oldItem, stack.createSnapshot());
                }
            }
            if (cursorTransaction == null) {
                cursorTransaction = new Transaction<>(oldItem, oldItem);
            }

            final ClickInventoryEvent.Middle event = SpongeEventFactory.createClickInventoryEventMiddle(
                    causeStack.getCurrentCause(), cursorTransaction, this.container, new ArrayList<>());
            finishInventoryEvent(event);
        } else {
            // Crafting slots have special click behavior
            if (slot instanceof CraftingOutput) {
                List<SlotTransaction> transactions = new ArrayList<>();
                Transaction<ItemStackSnapshot> cursorTransaction;

                final AbstractInventory parent = slot.parent();
                if (parent instanceof CraftingInventory) {
                    ClickInventoryEvent event;

                    final CraftingInventory inventory = (CraftingInventory) parent;
                    final Optional<ExtendedCraftingResult> optResult = Lantern.getRegistry().getCraftingRecipeRegistry()
                            .getExtendedResult(inventory.getCraftingGrid(), player.getWorld());
                    final ItemStackSnapshot originalCursorItem = LanternItemStack.toSnapshot(getCursorItem());
                    if (optResult.isPresent()) {
                        final CraftingResult result = optResult.get().getResult();
                        final ItemStackSnapshot resultItem = result.getResult();

                        int quantity = -1;
                        if (getCursorItem() == null) {
                            quantity = resultItem.getQuantity();
                        } else if (LanternItemStack.areSimilar(resultItem.createStack(), getCursorItem())) {
                            final int quantity1 = resultItem.getQuantity() + getCursorItem().getQuantity();
                            if (quantity1 < getCursorItem().getMaxStackQuantity()) {
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
                            updateCraftingGrid(player, inventory, optResult.get().getMatrixResult(1), transactions);
                        }
                    } else {
                        cursorTransaction = new Transaction<>(originalCursorItem, originalCursorItem);
                        // No actual transaction, there shouldn't have been a item in the crafting result slot
                        transactions.add(new SlotTransaction(slot, ItemStackSnapshot.NONE, ItemStackSnapshot.NONE));
                    }

                    transactions = this.container.transformSlots(transactions);
                    if (mouseButton == MouseButton.LEFT) {
                        event = SpongeEventFactory.createClickInventoryEventPrimary(causeStack.getCurrentCause(), cursorTransaction,
                                this.container, transactions);
                    } else {
                        event = SpongeEventFactory.createClickInventoryEventSecondary(causeStack.getCurrentCause(), cursorTransaction,
                                this.container, transactions);
                    }
                    finishInventoryEvent(event);
                    return;
                } else {
                    Lantern.getLogger().warn("Found a CraftingOutput slot without a CraftingInventory as parent.");
                }
            }

            ClickInventoryEvent event;
            if (mouseButton == MouseButton.LEFT) {
                final List<SlotTransaction> transactions = new ArrayList<>();
                Transaction<ItemStackSnapshot> cursorTransaction = null;

                if (getCursorItem() != null && !(slot instanceof OutputSlot)) {
                    final PeekedOfferTransactionResult result = slot.peekOffer(getCursorItem());
                    if (result.isSuccess()) {
                        transactions.addAll(result.getTransactions());
                        cursorTransaction = new Transaction<>(getCursorItem().createSnapshot(),
                                LanternItemStack.toSnapshot(result.getRejectedItem().orElse(null)));
                    } else {
                        final PeekedSetTransactionResult result1 = slot.peekSet(getCursorItem());
                        if (result1.isSuccess()) {
                            cursorTransaction = new Transaction<>(getCursorItem().createSnapshot(),
                                    LanternItemStack.toSnapshot(result1.getReplacedItem().orElse(null)));
                            transactions.addAll(result1.getTransactions());
                        }
                    }
                } else if (getCursorItem() == null) {
                    final PeekedPollTransactionResult result = slot.peekPoll(stack -> true).orElse(null);
                    if (result != null) {
                        cursorTransaction = new Transaction<>(ItemStackSnapshot.NONE, LanternItemStack.toSnapshot(result.getPolledItem()));
                        transactions.addAll(result.getTransactions());
                    } else {
                        cursorTransaction = new Transaction<>(ItemStackSnapshot.NONE, ItemStackSnapshot.NONE);
                    }
                }
                if (cursorTransaction == null) {
                    final ItemStackSnapshot cursorItem = LanternItemStack.toSnapshot(getCursorItem());
                    cursorTransaction = new Transaction<>(cursorItem, cursorItem);
                }
                event = SpongeEventFactory.createClickInventoryEventPrimary(causeStack.getCurrentCause(),
                        cursorTransaction, this.container, this.container.transformSlots(transactions));
            } else {
                final List<SlotTransaction> transactions = new ArrayList<>();
                Transaction<ItemStackSnapshot> cursorTransaction = null;

                if (getCursorItem() == null) {
                    int stackSize = slot.getStackSize();
                    if (stackSize != 0) {
                        stackSize = stackSize - (stackSize / 2);
                        final PeekedPollTransactionResult result = slot.peekPoll(stackSize, stack -> true).get();
                        transactions.addAll(result.getTransactions());
                        cursorTransaction = new Transaction<>(ItemStackSnapshot.NONE, result.getPolledItem().createSnapshot());
                    }
                } else {
                    final ItemStack itemStack = getCursorItem().copy();
                    itemStack.setQuantity(1);

                    final PeekedOfferTransactionResult result = slot.peekOffer(itemStack);
                    if (result.isSuccess()) {
                        final ItemStackSnapshot oldCursor = getCursorItem().createSnapshot();
                        int quantity = getCursorItem().getQuantity() - 1;
                        if (quantity <= 0) {
                            cursorTransaction = new Transaction<>(oldCursor, ItemStackSnapshot.NONE);
                        } else {
                            final ItemStack newCursorItem = getCursorItem().copy();
                            newCursorItem.setQuantity(quantity);
                            cursorTransaction = new Transaction<>(oldCursor, newCursorItem.createSnapshot());
                        }
                        transactions.addAll(result.getTransactions());
                    } else {
                        final PeekedSetTransactionResult result1 = slot.peekSet(getCursorItem());
                        if (result1.isSuccess()) {
                            final ItemStack replacedItem = result1.getReplacedItem().orElse(null);
                            if (replacedItem != null) {
                                setCursorItem(replacedItem);
                                cursorTransaction = new Transaction<>(getCursorItem().createSnapshot(),
                                        LanternItemStack.toSnapshot(replacedItem));
                            } else {
                                cursorTransaction = new Transaction<>(getCursorItem().createSnapshot(),
                                        ItemStackSnapshot.NONE);
                            }
                            transactions.addAll(result1.getTransactions());
                        }
                    }
                }
                if (cursorTransaction == null) {
                    final ItemStackSnapshot cursorItem = LanternItemStack.toSnapshot(getCursorItem());
                    cursorTransaction = new Transaction<>(cursorItem, cursorItem);
                }
                event = SpongeEventFactory.createClickInventoryEventSecondary(
                        causeStack.getCurrentCause(), cursorTransaction, this.container,
                        this.container.transformSlots(transactions));
            }
            finishInventoryEvent(event);
        }
    }

    @Override
    public void handleDropKey(ClientContainer clientContainer, ClientSlot clientSlot, boolean ctrl) {
        final LanternPlayer player = clientContainer.getPlayer();
        if (player != this.container.getPlayerInventory().getCarrier().orElse(null) ||
                !(clientSlot instanceof ClientSlot.Slot)) {
            return;
        }
        final AbstractSlot slot = ((ClientSlot.Slot) clientSlot).getSlot();

        final CauseStack causeStack = CauseStack.current();
        causeStack.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.DROPPED_ITEM);

        final List<Entity> entities = new ArrayList<>();
        final Transaction<ItemStackSnapshot> cursorTransaction;
        List<SlotTransaction> slotTransactions = new ArrayList<>();

        final ItemStackSnapshot item = LanternItemStack.toSnapshot(getCursorItem());
        cursorTransaction = new Transaction<>(item, item);
        final Optional<PeekedPollTransactionResult> result = ctrl ? slot.peekPoll(itemStack -> true) :
                slot.peekPoll(1, itemStack -> true);
        if (result.isPresent()) {
            final List<SlotTransaction> transactions = result.get().getTransactions();
            slotTransactions.addAll(transactions);
            final ItemStack itemStack = transactions.get(0).getOriginal().createStack();
            itemStack.setQuantity(itemStack.getQuantity() - transactions.get(0).getFinal().getQuantity());
            LanternEventHelper.handlePreDroppedItemSpawning(
                    player.getTransform(), LanternItemStackSnapshot.wrap(itemStack)).ifPresent(entities::add);
        }
        slotTransactions = this.container.transformSlots(slotTransactions);
        final ClickInventoryEvent.Drop event;
        if (ctrl) {
            event = SpongeEventFactory.createClickInventoryEventDropFull(causeStack.getCurrentCause(), cursorTransaction, entities,
                    this.container, slotTransactions);
        } else {
            event = SpongeEventFactory.createClickInventoryEventDropSingle(causeStack.getCurrentCause(), cursorTransaction, entities,
                    this.container, slotTransactions);
        }
        finishInventoryEvent(event);
    }

    @Override
    public void handleNumberKey(ClientContainer clientContainer, ClientSlot clientSlot, int number) {
        if (clientContainer.getPlayer() != this.container.getPlayerInventory().getCarrier().orElse(null) ||
                !(clientSlot instanceof ClientSlot.Slot)) {
            return;
        }
        final ClientSlot hotbarSlot = clientContainer.getClientSlot(clientContainer.getHotbarSlotIndex(number - 1))
                .orElseThrow(() -> new IllegalStateException("Missing hotbar client slot: " + number));
        if (!(hotbarSlot instanceof ClientSlot.Slot)) {
            return;
        }
        final AbstractSlot slot1 = ((ClientSlot.Slot) clientSlot).getSlot();
        final AbstractSlot hotbarSlot1 = ((ClientSlot.Slot) hotbarSlot).getSlot();
        if (slot1 != hotbarSlot1) {
            final List<SlotTransaction> transactions = new ArrayList<>();
            final Transaction<ItemStackSnapshot> cursorTransaction;

            if (getCursorItem() == null) {
                cursorTransaction = new Transaction<>(ItemStackSnapshot.NONE, ItemStackSnapshot.NONE);

                final ItemStack itemStack = slot1.getRawItemStack();
                final ItemStack hotbarItemStack = hotbarSlot1.getRawItemStack();

                final ItemStackSnapshot itemStackSnapshot = LanternItemStack.toSnapshot(itemStack);
                final ItemStackSnapshot hotbarItemStackSnapshot = LanternItemStack.toSnapshot(hotbarItemStack);

                if (!(itemStackSnapshot != ItemStackSnapshot.NONE && (!hotbarSlot1.isValidItem(itemStack) ||
                        itemStackSnapshot.getQuantity() > hotbarSlot1.getMaxStackSize())) &&
                        !(hotbarItemStackSnapshot != ItemStackSnapshot.NONE && (!slot1.isValidItem(hotbarItemStack) ||
                                hotbarItemStack.getQuantity() > slot1.getMaxStackSize()))) {
                    transactions.add(new SlotTransaction(slot1, itemStackSnapshot, hotbarItemStackSnapshot));
                    transactions.add(new SlotTransaction(hotbarSlot1, hotbarItemStackSnapshot, itemStackSnapshot));
                }
            } else {
                final ItemStackSnapshot cursorItem = getCursorItem().createSnapshot();
                cursorTransaction = new Transaction<>(cursorItem, cursorItem);
            }

            final CauseStack causeStack = CauseStack.current();
            final ClickInventoryEvent.NumberPress event = SpongeEventFactory.createClickInventoryEventNumberPress(
                    causeStack.getCurrentCause(), cursorTransaction, this.container,
                    this.container.transformSlots(transactions), number - 1);
            finishInventoryEvent(event);
        }
    }

    @Override
    public void handleDrag(ClientContainer clientContainer, List<ClientSlot> clientSlots, MouseButton mouseButton) {
        final LanternPlayer player = clientContainer.getPlayer();
        if (player != this.container.getPlayerInventory().getCarrier().orElse(null)) {
            return;
        }
        final List<AbstractSlot> slots = clientSlots.stream()
                .filter(clientSlot -> clientSlot instanceof ClientSlot.Slot)
                .map(clientSlot -> ((ClientSlot.Slot) clientSlot).getSlot())
                .collect(Collectors.toList());
        if (slots.size() != clientSlots.size()) {
            // TODO: Is this the behavior we want?
            return;
        }
        final ItemStack cursorItem = getCursorItem();
        if (cursorItem == null || cursorItem.isEmpty()) {
            return;
        }

        final CauseStack causeStack = CauseStack.current();
        if (mouseButton == MouseButton.LEFT) {
            final int quantity = cursorItem.getQuantity();
            final int slotCount = slots.size();
            final int itemsPerSlot = quantity / slotCount;
            final int rest = quantity - itemsPerSlot * slotCount;

            final List<SlotTransaction> transactions = new ArrayList<>();
            for (AbstractSlot slot : slots) {
                final ItemStack itemStack = cursorItem.copy();
                itemStack.setQuantity(itemsPerSlot);
                transactions.addAll(slot.peekOffer(itemStack).getTransactions());
            }

            ItemStackSnapshot newCursorItem = ItemStackSnapshot.NONE;
            if (rest > 0) {
                final ItemStack itemStack = cursorItem.copy();
                itemStack.setQuantity(rest);
                newCursorItem = LanternItemStackSnapshot.wrap(itemStack);
            }
            final ItemStackSnapshot oldCursorItem = cursorItem.createSnapshot();
            final Transaction<ItemStackSnapshot> cursorTransaction = new Transaction<>(oldCursorItem, newCursorItem);

            final ClickInventoryEvent.Drag.Primary event = SpongeEventFactory.createClickInventoryEventDragPrimary(
                    causeStack.getCurrentCause(), cursorTransaction, this.container, transactions);
            finishInventoryEvent(event);
        } else if (mouseButton == MouseButton.RIGHT) {
            int quantity = cursorItem.getQuantity();
            final int size = Math.min(slots.size(), quantity);

            final List<SlotTransaction> transactions = new ArrayList<>();
            for (AbstractSlot slot : slots) {
                final ItemStack itemStack = cursorItem.copy();
                itemStack.setQuantity(1);
                transactions.addAll(slot.peekOffer(itemStack).getTransactions());
            }
            quantity -= size;

            ItemStackSnapshot newCursorItem = ItemStackSnapshot.NONE;
            if (quantity > 0) {
                final ItemStack itemStack = cursorItem.copy();
                itemStack.setQuantity(quantity);
                newCursorItem = LanternItemStackSnapshot.wrap(itemStack);
            }
            final ItemStackSnapshot oldCursorItem = getCursorItem().createSnapshot();
            final Transaction<ItemStackSnapshot> cursorTransaction = new Transaction<>(oldCursorItem, newCursorItem);

            final ClickInventoryEvent.Drag.Secondary event = SpongeEventFactory.createClickInventoryEventDragSecondary(
                    causeStack.getCurrentCause(), cursorTransaction, this.container, this.container.transformSlots(transactions));
            finishInventoryEvent(event);
        } else {
            // TODO: Middle mouse drag mode
        }
    }

    @Override
    public void handleCreativeClick(ClientContainer clientContainer, @Nullable ClientSlot clientSlot, @Nullable ItemStack itemStack) {
        final LanternPlayer player = clientContainer.getPlayer();

        final CauseStack causeStack = CauseStack.current();
        if (clientSlot == null) {
            if (itemStack != null) {
                causeStack.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.DROPPED_ITEM);
                LanternEventHelper.handleDroppedItemSpawning(player.getTransform(), itemStack.createSnapshot());
            }
        } else if (clientSlot instanceof ClientSlot.Slot) {
            final AbstractSlot slot = ((ClientSlot.Slot) clientSlot).getSlot();
            final PeekedSetTransactionResult result = slot.peekSet(itemStack);

            // We do not know the remaining stack in the cursor,
            // so just use none as new item
            final Transaction<ItemStackSnapshot> cursorTransaction = new Transaction<>(
                    LanternItemStack.toSnapshot(itemStack), ItemStackSnapshot.NONE);

            final ClickInventoryEvent.Creative event = SpongeEventFactory.createClickInventoryEventCreative(
                    causeStack.getCurrentCause(), cursorTransaction, this.container,
                    this.container.transformSlots(result.getTransactions()));
            finishInventoryEvent(event);
        }
    }

    @Override
    public void handlePick(ClientContainer clientContainer, @Nullable ClientSlot clientSlot) {
        final LanternPlayer player = clientContainer.getPlayer();
        if (player != this.container.getPlayerInventory().getCarrier().orElse(null) ||
                !(clientSlot instanceof ClientSlot.Slot)) {
            return;
        }
        final PlayerInventoryContainer inventoryContainer = player.getInventoryContainer();
        final ClientSlot hotbarClientSlot = inventoryContainer.getClientContainer().getSelectedHotbarSlot();
        if (!(hotbarClientSlot instanceof ClientSlot.Slot)) {
            return;
        }
        final LanternHotbarInventory hotbar = player.getInventory().getHotbar();
        final AbstractSlot slot = ((ClientSlot.Slot) clientSlot).getSlot();

        // The slot we will swap items with
        AbstractSlot hotbarSlot = hotbar.getSelectedSlot();
        if (hotbarSlot.peek().isPresent()) {
            final Optional<AbstractSlot> optSlot = Streams.stream(hotbar.<AbstractSlot>slots())
                    .filter(slot1 -> !slot1.peek().isPresent())
                    .findFirst();
            if (optSlot.isPresent()) {
                hotbarSlot = optSlot.get();
            }
        }

        final ItemStack slotItem = slot.peek().orElse(null);
        final ItemStack hotbarItem = hotbarSlot.peek().orElse(null);

        hotbarSlot.set(slotItem);
        hotbar.setSelectedSlotIndex(hotbar.getSlotIndex(hotbarSlot));
        slot.set(hotbarItem);
    }

    private void updateCraftingGrid(Player player, CraftingInventory craftingInventory,
            MatrixResult matrixResult, List<SlotTransaction> transactions) {
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

        final CauseStack causeStack = CauseStack.current();
        causeStack.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.DROPPED_ITEM);

        final Transform<World> transform = player.getTransform();
        final List<Entity> entities = LanternEventHelper.handlePreDroppedItemSpawning(matrixResult.getRest().stream()
                .map(itemStack -> new Tuple<ItemStackSnapshot, Transform<World>>(
                        LanternItemStackSnapshot.wrap(itemStack), transform))
                .collect(Collectors.toList()));
        final SpawnEntityEvent event = SpongeEventFactory.createDropItemEventDispense(causeStack.getCurrentCause(), entities);
        Sponge.getEventManager().post(event);

        // Spawn all the entities in the world if the event isn't cancelled
        LanternWorld.finishSpawnEntityEvent(event);
    }

    private void finishInventoryEvent(ChangeInventoryEvent event) {
        final List<SlotTransaction> slotTransactions = event.getTransactions();
        Sponge.getEventManager().post(event);
        if (!event.isCancelled()) {
            if (!(event instanceof ClickInventoryEvent.Creative) && event instanceof ClickInventoryEvent) {
                final Transaction<ItemStackSnapshot> cursorTransaction = ((ClickInventoryEvent) event).getCursorTransaction();
                if (cursorTransaction.isValid()) {
                    setCursorItem(cursorTransaction.getFinal().createStack());
                }
            }
            slotTransactions.stream().filter(Transaction::isValid).forEach(
                    transaction -> transaction.getSlot().set(transaction.getFinal().createStack()));
            if (event instanceof SpawnEntityEvent) {
                LanternWorld.finishSpawnEntityEvent((SpawnEntityEvent) event);
            }
        }
    }
}
