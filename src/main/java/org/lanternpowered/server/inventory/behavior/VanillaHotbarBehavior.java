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
package org.lanternpowered.server.inventory.behavior;

import org.lanternpowered.api.cause.CauseStack;
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
                final ChangeInventoryEvent.Held event = SpongeEventFactory.createChangeInventoryEventHeld(causeStack.getCurrentCause(),
                        newSlot, clientContainer.getPlayer().getInventoryContainer(), oldSlot, transactions);
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
