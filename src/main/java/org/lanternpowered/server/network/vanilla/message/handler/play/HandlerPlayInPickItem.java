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
package org.lanternpowered.server.network.vanilla.message.handler.play;

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.HumanInventoryContainer;
import org.lanternpowered.server.inventory.entity.LanternHotbar;
import org.lanternpowered.server.inventory.entity.LanternHumanInventory;
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPickItem;
import org.spongepowered.api.item.inventory.ItemStack;

public final class HandlerPlayInPickItem implements Handler<MessagePlayInPickItem> {

    @Override
    public void handle(NetworkContext context, MessagePlayInPickItem message) {
        final LanternHumanInventory humanInventory = context.getSession().getPlayer().getInventory();
        final LanternSlot slot = humanInventory.getSlotAt(message.getSlot()).orElse(null);
        if (slot != null) {
            final LanternHotbar hotbar = humanInventory.getHotbar();

            // The slot we will swap items with
            LanternSlot hotbarSlot = hotbar.getSlots().stream()
                    .filter(slot1 -> !slot1.peek().isPresent())
                    .findFirst().orElse(hotbar.getSelectedSlot());

            ItemStack slotItem = slot.peek().orElse(null);
            ItemStack hotbarItem = hotbarSlot.peek().orElse(null);

            hotbarSlot.set(slotItem);
            slot.set(hotbarItem);

            final HumanInventoryContainer inventoryContainer = context.getSession().getPlayer().getInventoryContainer();
            inventoryContainer.queueSilentSlotChange(slot);
            inventoryContainer.queueSilentSlotChange(hotbarSlot);
            hotbar.setSelectedSlotIndex(hotbar.getSlotIndex(hotbarSlot));
        } else {
            Lantern.getLogger().warn("Unknown pick item slot index {} in main inventory {}", message.getSlot());
        }
    }
}
