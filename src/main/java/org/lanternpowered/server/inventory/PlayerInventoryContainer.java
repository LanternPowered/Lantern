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

import org.lanternpowered.server.inventory.behavior.VanillaContainerInteractionBehavior;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.inventory.client.PlayerClientContainer;
import org.lanternpowered.server.inventory.vanilla.LanternPlayerInventory;
import org.lanternpowered.server.text.translation.TextTranslation;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PlayerInventoryContainer extends CarriedLanternContainer<Player> {

    private PlayerClientContainer clientContainer;

    public PlayerInventoryContainer(LanternPlayerInventory playerInventory, AbstractOrderedInventory topInventory) {
        super(playerInventory, topInventory);
        // Construct the client container and attach the player
        this.clientContainer = new PlayerClientContainer(TextTranslation.toText(getName()));
        this.clientContainer.bindCursor(getCursorSlot());
        this.clientContainer.bindHotbarBehavior(playerInventory.getHotbar().getHotbarBehavior());
        this.clientContainer.bindInteractionBehavior(new VanillaContainerInteractionBehavior(this));
        this.clientContainer.bindBottom();
        getSlotsToIndexMap().object2IntEntrySet().forEach(
                entry -> this.clientContainer.bindSlot(entry.getIntValue(), entry.getKey().transform()));
        this.clientContainer.bind(playerInventory.getCarrier().get());
    }

    @Override
    void removeViewer(Player viewer) {
        if (viewer == this.playerInventory.getCarrier().orElse(null)) {
            return;
        }
        super.removeViewer(viewer);
    }

    @Override
    void addViewer(Player viewer) {
        if (viewer == this.playerInventory.getCarrier().orElse(null)) {
            return;
        }
        super.addViewer(viewer);
    }

    @Override
    public Optional<ClientContainer> getClientContainer(Player viewer) {
        checkNotNull(viewer, "viewer");
        // The client container of player who owns the inventory will
        // always be present
        final Player player = getPlayerInventory().getCarrier().orElse(null);
        if (player == viewer) {
            return Optional.of(this.clientContainer);
        }
        return super.getClientContainer(viewer);
    }

    @Override
    Collection<Player> getRawViewers() {
        final Player player = getPlayerInventory().getCarrier().orElse(null);
        if (player != null) {
            final Set<Player> viewers = new HashSet<>(super.getRawViewers());
            viewers.add(player);
            return viewers;
        }
        return super.getRawViewers();
    }

    public PlayerClientContainer getClientContainer() {
        return this.clientContainer;
    }

    public void initClientContainer() {
        this.clientContainer.init();
    }
}
