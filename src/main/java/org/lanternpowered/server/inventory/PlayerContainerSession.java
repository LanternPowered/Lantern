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

import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.event.LanternEventHelper;
import org.lanternpowered.server.inventory.client.AnvilClientContainer;
import org.lanternpowered.server.inventory.client.BeaconClientContainer;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.inventory.client.EnchantmentTableClientContainer;
import org.lanternpowered.server.inventory.client.PlayerClientContainer;
import org.lanternpowered.server.inventory.client.TradingClientContainer;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInAcceptBeaconEffects;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeItemName;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeOffer;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInClickRecipe;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInClickWindow;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInCreativeWindowAction;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInDisplayedRecipe;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInDropHeldItem;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInEnchantItem;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutCloseWindow;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutHeldItemChange;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPickItem;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutDisplayRecipe;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Represents a session of a player interacting with a
 * {@link AbstractContainer}. It is possible to switch
 * between {@link AbstractContainer}s without canceling
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
    @Nullable private AbstractContainer openContainer;

    public PlayerContainerSession(LanternPlayer player) {
        this.player = player;
    }

    private int getContainerId() {
        return getClientContainer().getContainerId();
    }

    private ClientContainer getClientContainer() {
        return checkNotNull(checkNotNull(this.openContainer).getClientContainer());
    }

    /**
     * Gets the open container.
     *
     * @return The container
     */
    @Nullable
    public AbstractContainer getOpenContainer() {
        return this.openContainer;
    }

    /**
     * Sets the open container.
     *
     * @param container The container
     */
    public boolean setOpenContainer(@Nullable AbstractContainer container) {
        return setRawOpenContainer(CauseStack.current(), container, true, false);
    }

    /**
     * Opens the players container when this is caused
     * by a players client.
     */
    private void openPlayerContainer() {
        this.player.resetOpenedSignPosition();
        final CauseStack causeStack = CauseStack.current();
        causeStack.pushCause(this.player);
        setRawOpenContainer(causeStack, this.player.getInventoryContainer());
        causeStack.popCause();
    }

    public boolean setRawOpenContainer(CauseStack causeStack, @Nullable AbstractContainer container) {
        return setRawOpenContainer(causeStack, container, false, false);
    }

    public void handleWindowClose(MessagePlayInOutCloseWindow message) {
        if (this.openContainer == null || message.getWindow() != getContainerId()) {
            return;
        }
        final CauseStack causeStack = CauseStack.current();
        causeStack.pushCause(this.player);
        setRawOpenContainer(causeStack, null, false, true);
        causeStack.popCause();
    }

    private boolean setRawOpenContainer(CauseStack causeStack, @Nullable AbstractContainer container, boolean sendClose, boolean client) {
        this.player.resetOpenedSignPosition();
        try (CauseStack.Frame frame = causeStack.pushCauseFrame()) {
            if (this.openContainer != container) {
                frame.addContext(EventContextKeys.PLAYER, this.player);
                ItemStackSnapshot cursorItem = ItemStackSnapshot.NONE;
                if (this.openContainer != null) {
                    final ItemStackSnapshot cursorItemSnapshot = LanternItemStackSnapshot.wrap(this.openContainer.getCursorSlot().peek());
                    final InteractInventoryEvent.Close event = SpongeEventFactory.createInteractInventoryEventClose(
                            frame.getCurrentCause(), new Transaction<>(cursorItemSnapshot, ItemStackSnapshot.NONE), this.openContainer);
                    Sponge.getEventManager().post(event);
                    if (event.isCancelled()) {
                        // Stop the client from closing the container, resend the open message
                        if (client) {
                            // This can't be done to the player inventory, player inventory uses index 0
                            // The optional should always return something at this point, otherwise
                            // something is broken
                            final ClientContainer clientContainer = getClientContainer();
                            if (clientContainer.getContainerId() != 0) {
                                // Reinitialize the client container
                                clientContainer.init();
                                return false;
                            }
                        } else {
                            // Just return
                            return false;
                        }
                    }
                    final Transaction<ItemStackSnapshot> transaction = event.getCursorTransaction();
                    if (transaction.isValid()) {
                        if (transaction.getFinal().isEmpty()) {
                            frame.pushCause(event); // Add the event that caused the drop to the cause
                            LanternEventHelper.handleDroppedItemSpawning(this.player.getTransform(), transaction.getOriginal());
                            frame.popCause();
                        } else {
                            cursorItem = transaction.getFinal();
                        }
                    }
                    // Close the inventory
                    this.openContainer.close(causeStack);
                } else {
                    sendClose = false;
                }
                if (container != null) {
                    final Transaction<ItemStackSnapshot> cursorTransaction = new Transaction<>(ItemStackSnapshot.NONE, cursorItem);
                    final InteractInventoryEvent.Open event = SpongeEventFactory.createInteractInventoryEventOpen(
                            frame.getCurrentCause(), cursorTransaction, container);
                    Sponge.getEventManager().post(event);
                    if (event.isCancelled()) {
                        if (cursorTransaction.isValid()) {
                            final ItemStackSnapshot cursorItem1 = cursorTransaction.getFinal();
                            if (!cursorItem1.isEmpty()) {
                                frame.pushCause(event); // Add the event that caused the drop to the cause
                                LanternEventHelper.handleDroppedItemSpawning(this.player.getTransform(), cursorItem1);
                                frame.popCause();
                            }
                        }
                        return false;
                    }
                    if (cursorTransaction.isValid()) {
                        final ItemStackSnapshot cursorItem1 = cursorTransaction.getFinal();
                        container.getCursorSlot().setRawItemStack(cursorItem1.createStack());
                    }
                    sendClose = false;
                    container.bind(this.player);
                    container.open();
                }
                if (sendClose && getContainerId() != 0) {
                    this.player.getConnection().send(new MessagePlayInOutCloseWindow(getContainerId()));
                }
                if (this.openContainer != null) {
                    this.openContainer.close();
                }
            }
            this.openContainer = container;
            return true;
        }
    }

    public void handleHeldItemChange(MessagePlayInOutHeldItemChange message) {
        final PlayerClientContainer clientContainer = this.player.getInventoryContainer().getClientContainer();
        clientContainer.handleHeldItemChange(message.getSlot());
    }

    private void applyIfContainerMatches(int windowId, Runnable runnable) {
        if (this.openContainer == null) {
            if (windowId == 0) {
                openPlayerContainer();
            } else {
                return;
            }
        } else if (windowId != getContainerId()) {
            return;
        }
        runnable.run();
    }

    public void handleRecipeClick(MessagePlayInClickRecipe message) {
        applyIfContainerMatches(message.getWindowId(), () -> {
            // Just display the recipe for now, all the other behavior will be implemented later,
            // this requires recipes to be added first
            this.player.getConnection().send(new MessagePlayOutDisplayRecipe(message.getWindowId(), message.getRecipeId()));
        });
    }

    public void handleWindowCreativeClick(MessagePlayInCreativeWindowAction message) {
        if (this.openContainer == null) {
            openPlayerContainer();
        }
        final ClientContainer clientContainer = getClientContainer();
        clientContainer.handleCreativeClick(message.getSlot(),
                message.getItemStack() == null ? LanternItemStack.empty() : message.getItemStack());
    }

    public void handleItemDrop(MessagePlayInDropHeldItem message) {
        final AbstractSlot slot = this.player.getInventory().getHotbar().getSelectedSlot();
        final ItemStack itemStack = message.isFullStack() ? slot.peek() : slot.peek(1);

        if (!itemStack.isEmpty()) {
            final CauseStack causeStack = CauseStack.current();
            try (CauseStack.Frame frame = causeStack.pushCauseFrame()) {
                frame.pushCause(this.player);
                frame.pushCause(slot);
                frame.addContext(EventContextKeys.PLAYER, this.player);
                frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.DROPPED_ITEM);

                final List<Entity> entities = new ArrayList<>();
                LanternEventHelper.handlePreDroppedItemSpawning(
                        this.player.getTransform(), LanternItemStackSnapshot.wrap(itemStack)).ifPresent(entities::add);

                final SpawnEntityEvent event = SpongeEventFactory.createDropItemEventDispense(causeStack.getCurrentCause(), entities);
                Sponge.getEventManager().post(event);

                if (!event.isCancelled()) {
                    if (message.isFullStack()) {
                        slot.poll();
                    } else {
                        slot.poll(1);
                    }
                    LanternWorld.finishSpawnEntityEvent(event);
                }
            }
        }
    }

    public void handleDisplayedRecipe(MessagePlayInDisplayedRecipe message) {
        if (this.openContainer == null) {
            openPlayerContainer();
        }
    }

    public void handleWindowClick(MessagePlayInClickWindow message) {
        applyIfContainerMatches(message.getWindowId(), () -> {
            final ClientContainer clientContainer = getClientContainer();
            clientContainer.handleClick(message.getSlot(), message.getMode(), message.getButton());
        });
    }

    public void handlePickItem(MessagePlayInPickItem message) {
        final ClientContainer clientContainer = getClientContainer();
        clientContainer.handlePick(message.getSlot());
    }

    public void handleAcceptBeaconEffects(MessagePlayInAcceptBeaconEffects message) {
        final ClientContainer clientContainer = getClientContainer();
        if (clientContainer instanceof BeaconClientContainer) {
            ((BeaconClientContainer) clientContainer).handleEffects(
                    message.getPrimaryEffect().orElse(null), message.getSecondaryEffect().orElse(null));
        }
    }

    public void handleItemRename(MessagePlayInChangeItemName message) {
        final ClientContainer clientContainer = getClientContainer();
        if (clientContainer instanceof AnvilClientContainer) {
            ((AnvilClientContainer) clientContainer).handleRename(message.getName());
        }
    }

    public void handleOfferChange(MessagePlayInChangeOffer message) {
        final ClientContainer clientContainer = getClientContainer();
        if (clientContainer instanceof TradingClientContainer) {
            ((TradingClientContainer) clientContainer).handleSelectOffer(message.getIndex());
        }
    }

    public void handleEnchantItem(MessagePlayInEnchantItem message) {
        if (message.getWindowId() != getContainerId()) {
            return;
        }
        final ClientContainer clientContainer = getClientContainer();
        if (clientContainer instanceof EnchantmentTableClientContainer) {
            ((EnchantmentTableClientContainer) clientContainer).handleButton(message.getEnchantmentSlot());
        }
    }
}
