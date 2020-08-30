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
package org.lanternpowered.api.item.inventory.container.layout

import org.lanternpowered.api.entity.player.Player
import org.spongepowered.api.item.merchant.TradeOffer

/**
 * Represents the top container layout of a merchant.
 */
interface MerchantContainerLayout : ContainerLayout {

    /**
     * The sub layout with all the inputs.
     */
    val inputs: ContainerLayout

    /**
     * The output slot.
     */
    val output: ContainerSlot

    /**
     * All the offers that are visible in the container.
     */
    var offers: List<TradeOffer>

    /**
     * Is called when a player on a specific trade offer clicks.
     */
    fun onClickOffer(fn: (player: Player, offer: TradeOffer) -> Unit)
}
