/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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

import com.google.common.collect.Sets;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.inventory.entity.LanternPlayerInventory;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.text.translation.Translation;

import java.util.Set;

import javax.annotation.Nullable;

public class PlayerInventoryContainer extends LanternContainer {

    public PlayerInventoryContainer(@Nullable Translation name, LanternPlayerInventory playerInventory) {
        super(name, playerInventory, null);
    }

    @Override
    protected void openInventoryFor(LanternPlayer viewer) {
    }

    @Override
    void queueSlotChange(Slot slot, boolean silent) {
        this.queueHumanSlotChange(slot, silent);
    }

    @Override
    Set<Player> getRawViewers() {
        final Player player = this.playerInventory.getCarrier().orElse(null);
        if (player != null) {
            final Set<Player> viewers = Sets.newHashSet(this.viewers);
            viewers.add(player);
            return viewers;
        }
        return this.viewers;
    }
}
