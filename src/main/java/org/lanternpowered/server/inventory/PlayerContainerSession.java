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
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientAcceptBeaconEffectsPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientItemRenamePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ChangeTradeOfferPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientClickRecipePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientClickWindowPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientCreativeWindowActionPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInDisplayedRecipe;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientDropHeldItemPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientEnchantItemPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.CloseWindowPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PlayerHeldItemChangePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientPickItemPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutDisplayRecipe;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.item.inventory.container.InteractContainerEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

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

    public void handleWindowClose(CloseWindowPacket message) {
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
                ItemStackSnapshot cursorItem = ItemStackSnapshot.empty();
                if (this.openContainer != null) {
                    final ItemStackSnapshot cursorItemSnapshot = LanternItemStackSnapshot.wrap(this.openContainer.getCursorSlot().peek());
                    final InteractContainerEvent.Close event = SpongeEventFactory.createInteractContainerEventClose(
                            frame.getCurrentCause(), this.openContainer, new Transaction<>(cursorItemSnapshot, ItemStackSnapshot.empty()));
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
                    final Transaction<ItemStackSnapshot> cursorTransaction = new Transaction<>(ItemStackSnapshot.empty(), cursorItem);
                    final InteractContainerEvent.Open event = SpongeEventFactory.createInteractContainerEventOpen(
                            frame.getCurrentCause(), container, cursorTransaction);
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
                    this.player.getConnection().send(new CloseWindowPacket(getContainerId()));
                }
                if (this.openContainer != null) {
                    this.openContainer.close();
                }
            }
            this.openContainer = container;
            return true;
        }
    }

    public void handleHeldItemChange(PlayerHeldItemChangePacket message) {
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

    public void handleRecipeClick(ClientClickRecipePacket message) {
        applyIfContainerMatches(message.getWindowId(), () -> {
            // Just display the recipe for now, all the other behavior will be implemented later,
            // this requires recipes to be added first
            this.player.getConnection().send(new PacketPlayOutDisplayRecipe(message.getWindowId(), message.getRecipeId()));
        });
    }

    public void handleWindowCreativeClick(ClientCreativeWindowActionPacket message) {
        if (this.openContainer == null) {
            openPlayerContainer();
        }
        final ClientContainer clientContainer = getClientContainer();
        clientContainer.handleCreativeClick(message.getSlot(),
                message.getItemStack() == null ? LanternItemStack.empty() : message.getItemStack());
    }

    public void handleItemDrop(ClientDropHeldItemPacket message) {
        final AbstractSlot slot = this.player.getInventory().getHotbar().getSelectedSlot();
        final ItemStack itemStack = message.getFullStack() ? slot.peek() : slot.peek(1);

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
                    if (message.getFullStack()) {
                        slot.poll();
                    } else {
                        slot.poll(1);
                    }
                    LanternWorld.finishSpawnEntityEvent(event);
                }
            }
        }
    }

    public void handleDisplayedRecipe(PacketPlayInDisplayedRecipe message) {
        if (this.openContainer == null) {
            openPlayerContainer();
        }
    }

    public void handleWindowClick(ClientClickWindowPacket message) {
        applyIfContainerMatches(message.getWindowId(), () -> {
            final ClientContainer clientContainer = getClientContainer();
            clientContainer.handleClick(message.getSlot(), message.getMode(), message.getButton());
        });
    }

    public void handlePickItem(ClientPickItemPacket message) {
        final ClientContainer clientContainer = getClientContainer();
        clientContainer.handlePick(message.getSlot());
    }

    public void handleAcceptBeaconEffects(ClientAcceptBeaconEffectsPacket message) {
        final ClientContainer clientContainer = getClientContainer();
        if (clientContainer instanceof BeaconClientContainer) {
            ((BeaconClientContainer) clientContainer).handleEffects(
                    message.getPrimaryEffect(), message.getSecondaryEffect());
        }
    }

    public void handleItemRename(ClientItemRenamePacket message) {
        final ClientContainer clientContainer = getClientContainer();
        if (clientContainer instanceof AnvilClientContainer) {
            ((AnvilClientContainer) clientContainer).handleRename(message.getName());
        }
    }

    public void handleOfferChange(ChangeTradeOfferPacket message) {
        final ClientContainer clientContainer = getClientContainer();
        if (clientContainer instanceof TradingClientContainer) {
            ((TradingClientContainer) clientContainer).handleSelectOffer(message.getIndex());
        }
    }

    public void handleEnchantItem(ClientEnchantItemPacket message) {
        if (message.getWindowId() != getContainerId()) {
            return;
        }
        final ClientContainer clientContainer = getClientContainer();
        if (clientContainer instanceof EnchantmentTableClientContainer) {
            ((EnchantmentTableClientContainer) clientContainer).handleButton(message.getEnchantmentSlot());
        }
    }
}
