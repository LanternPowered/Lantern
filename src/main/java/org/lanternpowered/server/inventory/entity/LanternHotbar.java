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
package org.lanternpowered.server.inventory.entity;

import static com.google.common.base.Preconditions.checkArgument;

import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.inventory.LanternInventoryRow;
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutHeldItemChange;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.text.translation.Translation;

import javax.annotation.Nullable;

public class LanternHotbar extends LanternInventoryRow implements Hotbar {

    private int selectedSlotIndex;

    public LanternHotbar(@Nullable Inventory parent) {
        super(parent);
    }

    public LanternHotbar(@Nullable Inventory parent, @Nullable Translation name) {
        super(parent, name);
    }

    public LanternSlot getSelectedSlot() {
        return this.getSlotAt(this.selectedSlotIndex).get();
    }

    @Override
    public int getSelectedSlotIndex() {
        return this.selectedSlotIndex;
    }

    public void setRawSelectedSlotIndex(int index) {
        this.selectedSlotIndex = index;
    }

    @Override
    public void setSelectedSlotIndex(int index) {
        checkArgument(index >= 0 && index < this.slots.size(), "The index %s may not be smaller then 0 or greater then %s",
                index, this.slots.size() - 1);
        Inventory inventory = this;
        while (!(inventory instanceof LanternPlayerInventory)) {
            Inventory inventory1 = inventory.parent();
            if (inventory == inventory1) {
                inventory = null;
                break;
            }
            inventory = inventory1;
        }
        if (inventory != null) {
            ((LanternPlayerInventory) inventory).getCarrier().filter(human -> human instanceof Player)
                    .ifPresent(player -> ((LanternPlayer) player).getConnection().send(new MessagePlayInOutHeldItemChange(index)));
        }
        this.setRawSelectedSlotIndex(index);
    }
}
