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

import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.inventory.client.PlayerClientContainer;
import org.lanternpowered.server.inventory.entity.LanternPlayerInventory;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.translation.Translation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PlayerInventoryContainer extends LanternContainer {

    private PlayerClientContainer clientContainer;

    public PlayerInventoryContainer(LanternPlayerInventory playerInventory) {
        super((Translation) null, playerInventory, null);
        // Construct the client container and attach the player
        this.clientContainer = (PlayerClientContainer) playerInventory.constructClientContainer(this);
        this.clientContainer.bind(playerInventory.getCarrier().get());
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
        final Player player = this.playerInventory.getCarrier().orElse(null);
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

    public void init() {
        this.clientContainer.init();
    }
}
