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
package org.lanternpowered.server.item.behavior.vanilla;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
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
                    context.getCurrentCause(), player.getInventory(), transactions);
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
