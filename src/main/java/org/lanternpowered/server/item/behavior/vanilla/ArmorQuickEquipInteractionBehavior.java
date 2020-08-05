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
package org.lanternpowered.server.item.behavior.vanilla;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.entity.player.LanternPlayer;
import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.PeekedOfferTransactionResult;
import org.lanternpowered.server.item.behavior.types.InteractWithItemBehavior;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import java.util.ArrayList;
import java.util.List;

public class ArmorQuickEquipInteractionBehavior implements InteractWithItemBehavior {

    @Override
    public BehaviorResult tryInteract(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final LanternPlayer player = (LanternPlayer) context.requireContext(ContextKeys.PLAYER);
        final ItemStack stack = context.requireContext(ContextKeys.USED_ITEM_STACK).copy();

        final PeekedOfferTransactionResult peekResult = player.getInventory().getArmor().peekOffer(stack);
        if (!peekResult.isEmpty()) {
            final List<SlotTransaction> transactions = new ArrayList<>(peekResult.getTransactions());
            final AbstractSlot slot = (AbstractSlot) context.getContext(ContextKeys.USED_SLOT).orElse(null);
            if (slot != null) {
                transactions.addAll(slot.peekSet(stack).getTransactions());
            }
            final ChangeInventoryEvent.Equipment event = SpongeEventFactory.createChangeInventoryEventEquipment(
                    context.getCurrentCause(), player.getInventory(), ImmutableList.copyOf(transactions));
            if (event.isCancelled()) {
                return BehaviorResult.CONTINUE;
            }
            event.getTransactions().stream().filter(Transaction::isValid).forEach(slotTransaction ->
                    slotTransaction.getSlot().set(slotTransaction.getFinal().createStack()));
            return BehaviorResult.SUCCESS;
        }
        return BehaviorResult.CONTINUE;
    }
}
