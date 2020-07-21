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
package org.lanternpowered.server.network.vanilla.packet.type.play

import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.trade.NetworkTradeOffer

data class SetWindowTradeOffersPacket(
        val windowId: Int,
        val villagerLevel: Int,
        val experience: Int,
        val regularVillager: Boolean,
        val canRestock: Boolean,
        val tradeOffers: List<NetworkTradeOffer>
) : Packet
