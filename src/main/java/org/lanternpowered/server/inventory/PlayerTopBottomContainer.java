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

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.inventory.behavior.VanillaContainerInteractionBehavior;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.inventory.client.ClientContainerType;
import org.lanternpowered.server.inventory.vanilla.LanternPlayerInventory;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.property.GuiId;
import org.spongepowered.api.item.inventory.property.GuiIdProperty;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

/**
 * A {@link AbstractContainer} which uses the classic "top-bottom" inventory layout
 * where the bottom inventory represents the {@link LanternPlayerInventory} and
 * the top inventory the opened one.
 */
public class PlayerTopBottomContainer extends AbstractContainer {

    /**
     * Creates a new {@link AbstractContainer}, the specified {@link LanternPlayerInventory} is
     * used as the bottom inventory and also as top inventory if {@code null} is provided
     * for the inventory that should be opened.
     *
     * @param playerInventory The player inventory
     * @param openInventory The inventory to open
     */
    public static AbstractContainer construct(LanternPlayerInventory playerInventory, AbstractChildrenInventory openInventory) {
        if (openInventory instanceof CarriedInventory) {
            return new Carried<>(playerInventory, openInventory);
        }
        return new PlayerTopBottomContainer(playerInventory, openInventory);
    }

    /**
     * The inventory which holds the bottom inventory.
     */
    private final LanternPlayerInventory playerInventory;

    public PlayerTopBottomContainer(LanternPlayerInventory playerInventory, AbstractChildrenInventory openInventory) {
        super(ImmutableList.of(openInventory, playerInventory.getPrimary()));
        this.playerInventory = playerInventory;
    }

    /**
     * Gets the {@link Inventory} that is being opened. It is possible that this
     * inventory is equal to {@link #getPlayerInventory()} in case the player
     * opened it's own inventory.
     *
     * @return The inventory
     */
    public AbstractChildrenInventory getOpenInventory() {
        return (AbstractChildrenInventory) getViewed().get(0);
    }

    /**
     * Gets the {@link LanternPlayerInventory}.
     *
     * @return The player inventory
     */
    public LanternPlayerInventory getPlayerInventory() {
        return this.playerInventory;
    }

    @Override
    protected ClientContainer constructClientContainer() {
        final ClientContainer clientContainer;
        final AbstractChildrenInventory openInventory = getOpenInventory();
        // Get the gui id (ClientContainerType)
        final GuiId guiId = openInventory.getProperty(GuiIdProperty.class)
                .map(GuiIdProperty::getValue).orElseThrow(IllegalStateException::new);
        clientContainer = ((ClientContainerType) guiId).createContainer(openInventory);
        clientContainer.bindCursor(getCursorSlot());
        clientContainer.bindInteractionBehavior(new VanillaContainerInteractionBehavior(this));
        openInventory.initClientContainer(clientContainer);
        // Bind the default bottom container part if the custom one is missing
        if (!clientContainer.getBottom().isPresent()) {
            final LanternPlayer player = (LanternPlayer) getPlayerInventory().getCarrier().get();
            clientContainer.bindBottom(player.getInventoryContainer().getClientContainer().getBottom().get());
        }
        return clientContainer;
    }

    @Override
    public AbstractContainer copy() {
        return new PlayerTopBottomContainer(this.playerInventory, getOpenInventory());
    }

    private static class Carried<C extends Carrier> extends PlayerTopBottomContainer implements ICarriedInventory<C> {

        Carried(LanternPlayerInventory playerInventory, AbstractChildrenInventory openInventory) {
            super(playerInventory, openInventory);
            playerInventory.getCarrier().ifPresent(this::setCarrier);
        }
    }
}
