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

import org.lanternpowered.server.inventory.behavior.VanillaContainerInteractionBehavior;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.inventory.client.PlayerClientContainer;
import org.lanternpowered.server.inventory.vanilla.LanternPlayerInventory;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.type.ViewableInventory;

import javax.annotation.Nonnull;

@SuppressWarnings("ConstantConditions")
public class PlayerInventoryContainer extends PlayerTopBottomContainer implements ICarriedInventory<Player> {

    private final PlayerClientContainer clientContainer;

    public PlayerInventoryContainer(LanternPlayerInventory playerInventory, AbstractChildrenInventory topInventory) {
        super(playerInventory, topInventory);

        final Player player = playerInventory.getCarrier().get();
        setCarrier(player, false);

        // Construct the client container and attach the player
        this.clientContainer = new PlayerClientContainer(TextTranslation.toText(getName()));
        this.clientContainer.bindCursor(getCursorSlot());
        this.clientContainer.bindHotbarBehavior(playerInventory.getHotbar().getHotbarBehavior());
        this.clientContainer.bindInteractionBehavior(new VanillaContainerInteractionBehavior(this));
        this.clientContainer.bindBottom();
        // Bind all the slots
        getSlotsToIndexMap().object2IntEntrySet().forEach(
                entry -> this.clientContainer.bindSlot(entry.getIntValue(), entry.getKey().viewedSlot()));
        this.clientContainer.bind(player);
        // Attach the viewer to all the linked children inventories, etc.
        addViewer(player, this);

        // Set the title of the container
        setName(playerInventory.getName());
        // Bind the player to this container
        bind(player);
    }

    @Override
    protected ClientContainer constructClientContainer() {
        return null;
    }

    @Override
    protected ViewableInventory toViewable() {
        // This container can normally not be opened on the client,
        // so create a chest inventory which represents this inventory.
        return new ContainerProvidedWrappedInventory(this,
                () -> new ViewedPlayerInventoryContainer(getPlayerInventory(), getOpenInventory()));
    }

    @Override
    void open() {
        // Do nothing, it's always open
    }

    @Nonnull
    @Override
    public PlayerClientContainer getClientContainer() {
        return this.clientContainer;
    }

    public void initClientContainer() {
        this.clientContainer.init();
    }

    public void release() {
        this.clientContainer.release();
        removeViewer(getViewer(), this);
    }
}
