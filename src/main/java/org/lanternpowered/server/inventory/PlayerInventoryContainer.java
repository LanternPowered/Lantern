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

import org.lanternpowered.server.inventory.behavior.VanillaContainerInteractionBehavior;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.inventory.client.PlayerClientContainer;
import org.lanternpowered.server.inventory.vanilla.LanternPlayerInventory;
import org.lanternpowered.server.text.translation.TextTranslation;
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
