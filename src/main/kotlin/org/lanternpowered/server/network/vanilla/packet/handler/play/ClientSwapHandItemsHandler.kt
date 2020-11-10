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
package org.lanternpowered.server.network.vanilla.packet.handler.play

import org.lanternpowered.api.cause.CauseContextKeys
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.withFrame
import org.lanternpowered.api.item.inventory.stack.asSnapshot
import org.lanternpowered.server.event.LanternEventFactory
import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.packet.PacketHandler
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSwapHandItemsPacket
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent.SwapHand
import org.spongepowered.api.item.inventory.transaction.SlotTransaction
import java.util.ArrayList

object ClientSwapHandItemsHandler : PacketHandler<ClientSwapHandItemsPacket> {

    override fun handle(ctx: NetworkContext, packet: ClientSwapHandItemsPacket) {
        val player = ctx.session.player
        val inventory = player.inventory
        val hotbarSlot = inventory.hotbar.selectedSlot
        val offHandSlot = inventory.offhand
        val hotbarItem = hotbarSlot.peek().asSnapshot()
        val offHandItem = offHandSlot.peek().asSnapshot()
        val transactions = ArrayList<SlotTransaction>()
        transactions.add(SlotTransaction(hotbarSlot, hotbarItem, offHandItem))
        transactions.add(SlotTransaction(offHandSlot, offHandItem, hotbarItem))
        CauseStack.withFrame { frame ->
            frame.addContext(CauseContextKeys.PLAYER, player)
            frame.pushCause(player)
            val event: SwapHand = LanternEventFactory.createChangeInventoryEventSwapHand(
                    frame.currentCause, inventory, transactions)
            Sponge.getEventManager().post(event)
            if (!event.isCancelled) {
                for (transaction in transactions) {
                    if (transaction.isValid)
                        transaction.slot.set(transaction.final.createStack())
                }
                val inventoryContainer = ctx.session.player.getInventoryContainer()
                inventoryContainer.clientContainer.queueSilentSlotChange(hotbarSlot)
            }
        }
    }
}
