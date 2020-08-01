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
package org.lanternpowered.server.network.vanilla.packet.handler.play;

import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.LanternItemStackSnapshot;
import org.lanternpowered.server.inventory.PlayerInventoryContainer;
import org.lanternpowered.server.inventory.vanilla.LanternPlayerInventory;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.packet.handler.Handler;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSwapHandItemsPacket;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import java.util.ArrayList;
import java.util.List;

public final class HandlerPlayInSwapHandItems implements Handler<ClientSwapHandItemsPacket> {

    @Override
    public void handle(NetworkContext context, ClientSwapHandItemsPacket packet) {
        final LanternPlayer player = context.getSession().getPlayer();
        final LanternPlayerInventory inventory = player.getInventory();

        final AbstractSlot hotbarSlot = inventory.getHotbar().getSelectedSlot();
        final AbstractSlot offHandSlot = inventory.getOffhand();

        final ItemStackSnapshot hotbarItem = LanternItemStackSnapshot.wrap(hotbarSlot.peek());
        final ItemStackSnapshot offHandItem = LanternItemStackSnapshot.wrap(offHandSlot.peek());

        final List<SlotTransaction> transactions = new ArrayList<>();
        transactions.add(new SlotTransaction(hotbarSlot, hotbarItem, offHandItem));
        transactions.add(new SlotTransaction(offHandSlot, offHandItem, hotbarItem));

        try (CauseStack.Frame frame = CauseStack.current().pushCauseFrame()) {
            frame.addContext(EventContextKeys.PLAYER, player);
            frame.pushCause(player);

            final ChangeInventoryEvent.SwapHand event = SpongeEventFactory.createChangeInventoryEventSwapHand(
                    frame.getCurrentCause(), inventory, transactions);
            Sponge.getEventManager().post(event);
            if (!event.isCancelled()) {
                transactions.stream().filter(Transaction::isValid).forEach(
                        transaction -> transaction.getSlot().set(transaction.getFinal().createStack()));

                final PlayerInventoryContainer inventoryContainer = context.getSession().getPlayer().getInventoryContainer();
                inventoryContainer.getClientContainer().queueSilentSlotChange(hotbarSlot);
            }
        }
    }
}
