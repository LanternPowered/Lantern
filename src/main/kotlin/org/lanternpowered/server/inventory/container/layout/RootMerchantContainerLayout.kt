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
package org.lanternpowered.server.inventory.container.layout

import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.item.inventory.container.layout.ContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.ContainerSlot
import org.lanternpowered.api.item.inventory.container.layout.MerchantContainerLayout
import org.lanternpowered.api.item.inventory.stack.asStack
import org.lanternpowered.api.item.inventory.stack.orEmptySnapshot
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.server.inventory.container.ClientWindowTypes
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenWindowPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.SetWindowTradeOffersPacket
import org.lanternpowered.server.network.vanilla.trade.NetworkTradeOffer
import org.spongepowered.api.item.merchant.TradeOffer

class RootMerchantContainerLayout : LanternTopBottomContainerLayout<MerchantContainerLayout>(
        title = TITLE, slotFlags = ALL_INVENTORY_FLAGS
) {

    companion object {

        private val TITLE = translatableTextOf("container.trading")

        private val TOP_INVENTORY_FLAGS = intArrayOf(
                Flags.DISABLE_SHIFT_INSERTION, // First input slot
                Flags.DISABLE_SHIFT_INSERTION, // Second input slot
                Flags.REVERSE_SHIFT_INSERTION + Flags.DISABLE_SHIFT_INSERTION or Flags.IGNORE_DOUBLE_CLICK // Result slot
        )

        private val ALL_INVENTORY_FLAGS = MAIN_INVENTORY_FLAGS + TOP_INVENTORY_FLAGS

        private const val UPDATE_TRADE_OFFERS = 0x1
    }

    private val onClickOffer = ArrayList<(Player, TradeOffer) -> Unit>()

    override fun createOpenPackets(data: ContainerData): List<Packet> =
            listOf(OpenWindowPacket(data.containerId, ClientWindowTypes.MERCHANT, this.title))

    override val top: MerchantContainerLayout = SubMerchantContainerLayout(0, TOP_INVENTORY_FLAGS.size, this)

    override fun collectChangePackets(data: ContainerData, packets: MutableList<Packet>) {
        if (data.slotUpdateFlags[0] and UpdateFlags.NEEDS_UPDATE != 0 ||
                data.slotUpdateFlags[1] and UpdateFlags.NEEDS_UPDATE != 0) {
            // Force update the result slot if one of the inputs is modified
            data.queueSilentSlotChangeSafely(this.slots[2])
        }
        if (data.extraUpdateFlags and UPDATE_TRADE_OFFERS != 0) {
            val networkOffers = this.offers
                    .map { offer ->
                        val firstInput = offer.firstBuyingItem.asStack()
                        val secondInput = offer.secondBuyingItem.orEmptySnapshot().asStack()
                        val output = offer.sellingItem.asStack()
                        val disabled = offer.hasExpired()
                        val uses = offer.uses
                        val maxUses = offer.maxUses
                        val experience = offer.experienceGrantedToMerchant
                        val specialPrice = offer.demandBonus // TODO: Check if this is correct
                        val priceMultiplier = offer.priceGrowthMultiplier
                        NetworkTradeOffer(firstInput, secondInput, output, disabled, uses, maxUses,
                                experience, specialPrice, priceMultiplier)
                    }
            // TODO: Check if we need to expose some of these options.
            packets += SetWindowTradeOffersPacket(data.containerId, villagerLevel = 1, experience = 0,
                    regularVillager = true, canRestock = true, tradeOffers = networkOffers)
            data.extraUpdateFlags = 0
        }
        super.collectChangePackets(data, packets)
    }

    var offers: List<TradeOffer> = emptyList()
        set(value) {
            field = value.toImmutableList()
            for (data in this.viewerData)
                data.extraUpdateFlags = data.extraUpdateFlags or UPDATE_TRADE_OFFERS
        }

    fun onClickOffer(fn: (player: Player, offer: TradeOffer) -> Unit) {
        this.onClickOffer += fn
    }

    /**
     * Handles a click offer packet for the given player.
     */
    fun handleClickOffer(player: Player, index: Int) {
        this.getData(player) ?: return
        // TODO: Check if updates are needed

        val offer = if (index >= 0 && index < this.offers.size) this.offers[index] else return
        for (onClickOffer in this.onClickOffer)
            onClickOffer(player, offer)
    }
}

private class SubMerchantContainerLayout(
        offset: Int, size: Int, private val root: RootMerchantContainerLayout
) : SubContainerLayout(offset, size, root), MerchantContainerLayout {

    // 0..last - 1 = inputs
    // last = output

    override val inputs: ContainerLayout = SubContainerLayout(offset, this.size - 1, this.base)
    override val output: ContainerSlot get() = this[this.size - 1]

    override var offers: List<TradeOffer>
        get() = this.root.offers
        set(value) { this.root.offers = value }

    override fun onClickOffer(fn: (player: Player, offer: TradeOffer) -> Unit) {
        this.root.onClickOffer(fn)
    }
}
