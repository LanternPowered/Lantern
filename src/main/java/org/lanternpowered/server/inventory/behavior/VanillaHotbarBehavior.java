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
package org.lanternpowered.server.inventory.behavior;

import org.lanternpowered.server.event.CauseStack;
import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.inventory.client.ClientSlot;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VanillaHotbarBehavior extends SimpleHotbarBehavior {

    @Override
    public void handleSelectedSlotChange(ClientContainer clientContainer, int hotbarSlot) {
        final Optional<ClientSlot> oldHotbarSlot = clientContainer.getClientSlot(getSelectedSlotIndex());
        final Optional<ClientSlot> newHotbarSlot = clientContainer.getClientSlot(hotbarSlot);

        final AbstractSlot oldSlot = oldHotbarSlot.get() instanceof ClientSlot.Slot ?
                ((ClientSlot.Slot) oldHotbarSlot.get()).getSlot() : null;
        final AbstractSlot newSlot = newHotbarSlot.get() instanceof ClientSlot.Slot ?
                ((ClientSlot.Slot) newHotbarSlot.get()).getSlot() : null;

        if (oldSlot != null && newSlot != null) {
            final CauseStack causeStack = CauseStack.current();

            final ItemStackSnapshot oldItem = LanternItemStack.toSnapshot(oldSlot.getRawItemStack());
            final ItemStackSnapshot newItem = LanternItemStack.toSnapshot(newSlot.getRawItemStack());

            final SlotTransaction oldTransaction = new SlotTransaction(oldSlot, oldItem, oldItem);
            final SlotTransaction newTransaction = new SlotTransaction(newSlot, newItem, newItem);

            final List<SlotTransaction> transactions = new ArrayList<>();
            transactions.add(oldTransaction);
            transactions.add(newTransaction);

            try (CauseStack.Frame frame = causeStack.pushCauseFrame()) {
                frame.pushCause(clientContainer.getPlayer());
                final ChangeInventoryEvent.Held event = SpongeEventFactory.createChangeInventoryEventHeld(
                        causeStack.getCurrentCause(), newSlot, oldSlot, clientContainer.getPlayer().getInventoryContainer(), transactions);
                Sponge.getEventManager().post(event);
                if (event.isCancelled() || transactions.stream().noneMatch(SlotTransaction::isValid)) {
                    setSelectedSlotIndex(getSelectedSlotIndex());
                    return;
                } else {
                    transactions.stream().filter(Transaction::isValid).forEach(
                            transaction -> transaction.getSlot().set(transaction.getFinal().createStack()));
                }
            }
        }

        super.handleSelectedSlotChange(clientContainer, hotbarSlot);
    }
}
